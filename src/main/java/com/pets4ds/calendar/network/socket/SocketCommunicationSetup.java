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
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SocketCommunicationSetup implements CommunicationSetup, Closeable {
    private static final int UPDATE_INTERVAL = 100;
    
    private final CommunicationSetupHandler _handler;
    private final HashMap<CommunicationSession, SocketUpdater> _currentSessions;
    private final ScheduledExecutorService _updateExecutor;
    
    public SocketCommunicationSetup(CommunicationSetupHandler handler) {
        _handler = handler;
        _currentSessions = new HashMap<>();
        
        _updateExecutor = Executors.newSingleThreadScheduledExecutor();
        _updateExecutor.scheduleAtFixedRate(() -> { updateConnections(); }, UPDATE_INTERVAL, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public synchronized void close() {
        _updateExecutor.shutdown();
        
        for(SocketUpdater updater : _currentSessions.values()) {
            try {
                updater.close();
            } catch(IOException exception) {
                _handler.setupError(new NetworkException("Unable to close socket.", exception));
            }
        }
        
        _currentSessions.clear();
    }
    
    private synchronized void updateConnections() {
        Iterator<Map.Entry<CommunicationSession, SocketUpdater>> iterator = _currentSessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CommunicationSession, SocketUpdater> entry = iterator.next();
            SocketUpdater updater = entry.getValue();
            
            updater.update(_handler, UPDATE_INTERVAL);
            if(!updater.isConnected()) {
                try {
                    updater.close();
                } catch(IOException exception) {
                    _handler.setupError(new NetworkException("Unable to close disconnected updater.", exception));
                }
                
                iterator.remove();
                
                _handler.sessionDisconnected(entry.getKey());
            }
        }
    }
    
    @Override
    public synchronized void createSession(CommunicationSession session) throws IOException {
        if(session == null)
            throw new IllegalArgumentException("Session description must not be null.");
        
        if(_currentSessions.get(session) != null)
            throw new IllegalArgumentException("Session is already established.");
        
        SocketUpdater updater = new ServerSocketUpdater(session, _handler, session.getInitiatorAddress().getPort());
        (new Thread((Runnable)updater)).start();
        
        _currentSessions.put(session, updater);
    }

    @Override
    public synchronized void joinSession(CommunicationSession session) throws IOException {
        if(session == null)
            throw new IllegalArgumentException("Session must not be null.");
        
        if(_currentSessions.get(session) != null)
            throw new IllegalArgumentException("Session is already established.");
        
        SocketUpdater updater = new ClientSocketUpdater(session, session.getInitiatorAddress());
        
        _currentSessions.put(session, updater);
    }
    
    @Override
    public synchronized void leaveSession(CommunicationSession session) throws IOException {
        SocketUpdater updater = _currentSessions.get(session);
        if(updater == null)
            throw new IllegalArgumentException("Session is not established.");
        
        updater.close();
        _currentSessions.remove(session);
    }
    
    @Override
    public synchronized void setLocalParty(CommunicationSession session, CommunicationParty localParty) {
        SocketUpdater updater = _currentSessions.get(session);
        if(updater == null)
            throw new IllegalArgumentException("Session is not established.");
        
        updater.setLocalParty(_handler, localParty);
    }
    
    @Override
    public synchronized boolean isParticipating(CommunicationSession session) {
        return _currentSessions.containsKey(session);
    }
    
    /*
    @Override
    public synchronized void updateSessionState(CommunicationSession session, Serializable state) {
        SocketUpdater updater = _currentSessions.get(session.getDescription());
        if(updater == null || updater.getSession() != session)
            throw new IllegalArgumentException("Session is not established.");
        
        if(session.getLocalRole() != PartyRole.INITIATOR)
            throw new IllegalArgumentException("Session state can only be updated as initiator.");
        
        SessionCommand command = new StateUpdateCommand(session.getStateRevision() + 1, state);
        updater.sendMessage(command);
        command.execute(session, _handler);
    }
    */
}
