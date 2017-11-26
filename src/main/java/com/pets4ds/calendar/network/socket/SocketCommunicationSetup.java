/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private final HashMap<CommunicationSessionDescription, SocketUpdater> _currentSessions;
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
                _handler.handleSetupError(new NetworkException("Unable to close socket.", exception));
            }
        }
        
        _currentSessions.clear();
    }
    
    private synchronized void updateConnections() {
        Iterator<Map.Entry<CommunicationSessionDescription, SocketUpdater>> iterator = _currentSessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<CommunicationSessionDescription, SocketUpdater> entry = iterator.next();
            SocketUpdater updater = entry.getValue();
            
            updater.update(_handler, UPDATE_INTERVAL);
            if(!updater.isConnected()) {
                try {
                    updater.close();
                } catch(IOException exception) {
                    _handler.handleSetupError(new NetworkException("Unable to close disconnected updater.", exception));
                }
                
                iterator.remove();
                
                _handler.handleSessionDisconnected(entry.getKey());
            }
        }
    }
    
    @Override
    public synchronized void createSession(CommunicationSessionDescription sessionDescription) throws IOException {
        if(sessionDescription == null)
            throw new IllegalArgumentException("Session description must not be null.");
        
        if(_currentSessions.get(sessionDescription) != null)
            throw new IllegalArgumentException("Session is already established.");
        
        SocketUpdater updater = new ServerSocketUpdater(sessionDescription, _handler, sessionDescription.getInitiatorAddress().getPort());
        (new Thread((Runnable)updater)).start();
        
        _currentSessions.put(sessionDescription, updater);
    }

    @Override
    public synchronized void joinSession(CommunicationSessionDescription sessionDescription) throws IOException {
        if(sessionDescription == null)
            throw new IllegalArgumentException("Session must not be null.");
        
        if(_currentSessions.get(sessionDescription) != null)
            throw new IllegalArgumentException("Session is already established.");
        
        SocketUpdater updater = new ClientSocketUpdater(sessionDescription, sessionDescription.getInitiatorAddress());
        
        _currentSessions.put(sessionDescription, updater);
    }
    
    @Override
    public synchronized void leaveSession(CommunicationSessionDescription sessionDescription) throws IOException {
        SocketUpdater updater = _currentSessions.get(sessionDescription);
        if(updater == null)
            throw new IllegalArgumentException("Session is not established.");
        
        updater.close();
        _currentSessions.remove(sessionDescription);
    }
    
    @Override
    public synchronized void setLocalParty(CommunicationSessionDescription sessionDescription, CommunicationParty localParty) {
        SocketUpdater updater = _currentSessions.get(sessionDescription);
        if(updater == null)
            throw new IllegalArgumentException("Session is not established.");
        
        updater.setLocalParty(_handler, localParty);
    }
    
    @Override
    public synchronized boolean isParticipating(CommunicationSessionDescription sessionDescription) {
        return _currentSessions.containsKey(sessionDescription);
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
