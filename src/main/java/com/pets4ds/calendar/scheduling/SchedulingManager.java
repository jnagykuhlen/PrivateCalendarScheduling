/*
 * MIT License
 *
 * Copyright (c) 2018 Jonas Nagy-Kuhlen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.circuit.CircuitGenerator;
import com.pets4ds.calendar.mpc.scapi.ScapiSecureComputation;
import com.pets4ds.calendar.mpc.SecureComputation;
import com.pets4ds.calendar.mpc.SecureComputationException;
import com.pets4ds.calendar.network.AddressHelper;
import com.pets4ds.calendar.network.BroadcastChannel;
import com.pets4ds.calendar.network.BroadcastChannelRunner;
import com.pets4ds.calendar.network.BroadcastHandler;
import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.CommunicationSetup;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import com.pets4ds.calendar.network.NetworkException;
import com.pets4ds.calendar.network.socket.DatagramBroadcastChannel;
import com.pets4ds.calendar.network.socket.SocketCommunicationSetup;
import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingManager implements Closeable {
    private final static int MAX_NUMBER_OF_OPEN_INVITES = 4;
    
    private final CommunicationSetup _communicationSetup;
    private final BroadcastChannel _broadcastChannel;
    private final AddressHelper _addressHelper;
    private final SchedulingHandler _handler;
    private final Map<SchedulingSession, SessionInfo> _sessions;
    private final Set<SchedulingSession> _openInvites;
    private String _localName;
    
    public SchedulingManager(SchedulingHandler handler, int minSetupPort, int maxSetupPort, int broadcastPort) {
        CommunicationSetupHandler setupHandler = new CommunicationSetupHandler() {
            @Override
            public void sessionChanged(CommunicationSession session, CommunicationSessionState sessionState) {
                handleSessionChanged(session, sessionState);
            }

            @Override
            public void sessionDisconnected(CommunicationSession session) {
                handleSessionDisconnected(session);
            }

            @Override
            public void setupError(NetworkException exception) {
                handleNetworkError(exception);
            }
        };
                
        BroadcastHandler broadcastHandler = new BroadcastHandler() {
            @Override
            public void broadcastReceived(Serializable serializable) {
                handleBroadcastReceived(serializable);
            }

            @Override
            public void broadcastError(NetworkException exception) {
                handleNetworkError(exception);
            }
        };
        
        _handler = handler;
        _communicationSetup = new SocketCommunicationSetup(setupHandler);
        _broadcastChannel = BroadcastChannelRunner.startInNewThread(new DatagramBroadcastChannel(broadcastHandler, broadcastPort));
        _addressHelper = new AddressHelper(minSetupPort, maxSetupPort);
        _sessions = new ConcurrentHashMap<>();
        _openInvites = new HashSet<>();
        _localName = "User" + (new Random()).nextInt(10000);
    }
    
    private void handleSessionChanged(CommunicationSession session, CommunicationSessionState sessionState) {
        SessionInfo sessionInfo = _sessions.get((SchedulingSession)session);
        if(sessionInfo == null)
            throw new IllegalArgumentException("Scheduling session is not established.");
        
        _handler.sessionChanged((SchedulingSession)session, sessionState);
        
        if(sessionInfo.getSchedulingState() == SchedulingState.SETUP) {
            CommunicationParty[] parties = sessionState.getParties();

            boolean setupCompleted = parties.length > 0;
            for(int i = 0; i < parties.length; ++i)
                setupCompleted = setupCompleted && parties[i].isReady();

            if(setupCompleted) {
                sessionInfo.setSchedulingState(SchedulingState.COMPUTING);
                
                TimeSlotAllocation[] localInput = _handler.getLocalInput((SchedulingSession)session);
                _handler.schedulingStarted((SchedulingSession)session);
                (new Thread(() -> { schedule((SchedulingSession)session, sessionState, sessionInfo, localInput); })).start();
            }
        }
    }
    
    private void schedule(SchedulingSession session, CommunicationSessionState sessionState, SessionInfo sessionInfo, TimeSlotAllocation[] localInput) {
        if(localInput.length != session.getTimeSlots().length)
            throw new IllegalArgumentException("Number of local input values must match the number of time slots.");
        
        InetSocketAddress[] partyAddresses = new InetSocketAddress[sessionState.getParties().length];
        for(int i = 0; i < partyAddresses.length; ++i)
            partyAddresses[i] = (InetSocketAddress)sessionState.getParties()[i].getSetupState();
        
        byte[] localInputBits = new byte[localInput.length];
        for(int i = 0; i < localInputBits.length; ++i) {
            localInputBits[i] = 0;
            if(localInput[i] == TimeSlotAllocation.FREE)
                localInputBits[i] = 1;
        }
        
        SchedulingScheme schedulingScheme = session.getSchedulingSchemeIdentifier().getSchedulingScheme();
        CircuitGenerator circuitGenerator = 
            schedulingScheme.createCircuitGenerator(sessionState.getParties().length, session.getTimeSlots().length);
        
        String cacheDirectory = MessageFormat.format("scapi-cache-{0}-{1}/", session.getUUID(), sessionState.getLocalPartyIndex());
        SecureComputation secureComputation = new ScapiSecureComputation(partyAddresses, cacheDirectory);
        
        try {
            byte[] result = secureComputation.evaluate(circuitGenerator, sessionState.getLocalPartyIndex(), localInputBits);
            Optional<Integer> selectedSlotIndex = schedulingScheme.getSelectedSlotIndex(result);
            
            _handler.schedulingFinished(session, selectedSlotIndex);
        } catch(SecureComputationException exception) {
            _handler.schedulingFailed(session, new SchedulingException("Unable to perform secure computation.", exception));
        } catch(InvalidOutputException exception) {
            _handler.schedulingFailed(session, new SchedulingException("Secure computation resulted in inconsistent output.", exception));
        }
        
        sessionInfo.setSchedulingState(SchedulingState.FINISHED);
    }
    
    private void handleSessionDisconnected(CommunicationSession session) {
        _handler.sessionDisconnected((SchedulingSession)session);
        _sessions.remove((SchedulingSession)session);
    }
    
    private void handleBroadcastReceived(Serializable serializable) {
        if(serializable instanceof SchedulingSession) {
            SchedulingSession session = (SchedulingSession)serializable;
            
            if(!_communicationSetup.isParticipating(session)) {
                synchronized(_openInvites) {
                    if(!_openInvites.contains(session) && _openInvites.size() < MAX_NUMBER_OF_OPEN_INVITES) {
                        _openInvites.add(session);
                        _handler.sessionInviteReceived(session);
                    }
                }
            }
        }
    }
    
    private void handleNetworkError(NetworkException exception) {
        _handler.networkError(exception);
    }
    
    @Override
    public void close() throws IOException {
        _broadcastChannel.stop();
        _communicationSetup.close();
        
        _sessions.clear();
        synchronized(_openInvites) {
            _openInvites.clear();
        }
    }
    
    public SchedulingSession createSchedulingSession(String name, String descriptionText, SchedulingSchemeIdentifier schedulingScheme, TimeSlot[] timeSlots) throws IOException {
        SchedulingSession session = new SchedulingSession(
            name,
            descriptionText,
            _addressHelper.getNextLocalAddress(),
            schedulingScheme,
            timeSlots
        );
        
        _communicationSetup.createSession(session);
        _sessions.put(session, new SessionInfo(_addressHelper.getRandomLocalAddress())); // TODO: Add real address
        
        _broadcastChannel.publish(session);
        
        return session;
    }
    
    public void acceptInvite(SchedulingSession session) throws IOException {
        if(!_openInvites.contains(session))
            throw new IllegalArgumentException("There is no invite present for the specified session.");
        
        synchronized(_openInvites) {
            _openInvites.remove(session);
        }
        
        _communicationSetup.joinSession(session);
        _sessions.put(session, new SessionInfo(_addressHelper.getRandomLocalAddress())); // TODO: Add real address
    }
    
    public void ignoreInvite(SchedulingSession session) {
        if(!_openInvites.contains(session))
            throw new IllegalArgumentException("There is no invite present for the specified session.");
        
        synchronized(_openInvites) {
            _openInvites.remove(session);
        }
    }
    
    public void leaveSchedulingSession(SchedulingSession session) throws IOException {
        _communicationSetup.leaveSession(session);
        _sessions.remove(session);
    }
    
    public void resendInvite(SchedulingSession session) {
        _broadcastChannel.publish(session);
    }
    
    public String getLocalName() {
        return _localName;
    }
    
    public void setLocalName(String localName) {
        _localName = localName;
        for(SchedulingSession session : _sessions.keySet())
            publishLocalPartyState(session);
    }
    
    public boolean getLocalReadyState(SchedulingSession session) {
        SessionInfo sessionInfo = _sessions.get(session);
        if(sessionInfo == null)
            throw new IllegalArgumentException("Scheduling session is not established.");
        
        return sessionInfo.getReadyState();
    }
    
    public void setLocalReadyState(SchedulingSession session, boolean isReady) {
        SessionInfo sessionInfo = _sessions.get(session);
        if(sessionInfo == null)
            throw new IllegalArgumentException("Scheduling session is not established.");
        
        sessionInfo.setReadyState(isReady);
        publishLocalPartyState(session);
    }
    
    public void publishLocalPartyState(SchedulingSession session) {
        SessionInfo sessionInfo = _sessions.get(session);
        if(sessionInfo == null)
            throw new IllegalArgumentException("Scheduling session is not established.");
            
        _communicationSetup.setLocalParty(
            session,
            new CommunicationParty(_localName, sessionInfo.getLocalSchedulingAddress(), sessionInfo.getReadyState())
        );
    }
    
    public InetSocketAddress getLocalSchedulingAddress(SchedulingSession session) {
        SessionInfo sessionInfo = _sessions.get(session);
        if(sessionInfo == null)
            throw new IllegalArgumentException("Scheduling session is not established.");
        
        return sessionInfo.getLocalSchedulingAddress();
    }
    
    public SchedulingState getSchedulingState(SchedulingSession session) {
        SessionInfo sessionInfo = _sessions.get(session);
        if(sessionInfo == null)
            throw new IllegalArgumentException("Scheduling session is not established.");
        
        return sessionInfo.getSchedulingState();
    }
    
    public boolean isParticipating(SchedulingSession session) {
        return _sessions.containsKey(session);
    }
}
