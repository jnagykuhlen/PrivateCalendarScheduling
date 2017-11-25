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
    
    private CommunicationSetupHandler _handler;
    private HashMap<CommunicationSessionDescription, SocketUpdater> _currentSessions;
    private ScheduledExecutorService _updateExecutor;
    
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
        for(SocketUpdater updater : _currentSessions.values())
            updater.update(_handler, UPDATE_INTERVAL);
    }
    
    @Override
    public synchronized CommunicationSession createSession(CommunicationSessionDescription sessionDescription, CommunicationParty localParty) throws IOException {
        if(sessionDescription == null)
            throw new IllegalArgumentException("Session description must not be null.");
        
        if(_currentSessions.get(sessionDescription) != null)
            throw new IllegalArgumentException("Session is already established.");
        
        CommunicationSession session = new CommunicationSession(sessionDescription, PartyRole.INITIATOR);
        
        SocketUpdater updater = new ServerSocketUpdater(session, localParty, _handler, sessionDescription.getInitiatorAddress().getPort());
        (new Thread((Runnable)updater)).start();
        
        _currentSessions.put(sessionDescription, updater);
        
        return session;
    }

    @Override
    public synchronized CommunicationSession joinSession(CommunicationSessionDescription sessionDescription, CommunicationParty localParty) throws IOException {
        if(sessionDescription == null)
            throw new IllegalArgumentException("Session must not be null.");
        
        if(_currentSessions.get(sessionDescription) != null)
            throw new IllegalArgumentException("Session is already established.");
        
        CommunicationSession session = new CommunicationSession(sessionDescription, PartyRole.PARTICIPANT);
        SocketUpdater updater = null; // TODO: Create ClientSocketConnection
        
        // TODO: Connect sockets etc.
        
        // _currentSessions.put(sessionDescription, updater);
        
        return session;
    }
    
    @Override
    public synchronized void leaveSession(CommunicationSession session) {
        SocketUpdater connection = _currentSessions.get(session.getDescription());
        if(connection == null || connection.getSession() != session)
            throw new IllegalArgumentException("Session is not established.");
        
        if(session.getLocalRole() == PartyRole.INITIATOR) {
            // TODO: Cleanup listening server
        }
        
        _currentSessions.remove(session.getDescription());
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
    
    @Override
    public Iterator<CommunicationSession> getSessions() {
        return new CommunicationSessionIterator(_currentSessions.values().iterator());
    }
    
    @Override
    public synchronized CommunicationSession getSession(CommunicationSessionDescription sessionDescription) {
        SocketUpdater updater = _currentSessions.get(sessionDescription);
        if(updater != null)
            return updater.getSession();
        
        return null;
    }
}
