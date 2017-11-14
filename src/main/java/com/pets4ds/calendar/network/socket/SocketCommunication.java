/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.*;
import java.io.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SocketCommunication implements CommunicationSetup, Closeable {
    private final static int BROADCAST_PORT = 23934;
    
    private CommunicationSetupHandler _handler;
    private BroadcastManager _broadcastManager;
    private CommunicationSession _currentSession;
    
    public SocketCommunication(CommunicationSetupHandler handler) {
        _handler = handler;
        _broadcastManager = new BroadcastManager(handler, BROADCAST_PORT);
        _currentSession = null;
        
        (new Thread(_broadcastManager, "BroadcastThread")).start();
    }
    
    @Override
    public void close() {
        _broadcastManager.close();
    }
    
    @Override
    public void createSession(CommunicationSession session) {
        if(session == null)
            throw new IllegalArgumentException("Session must not be null.");
        
        _currentSession = session;
        _broadcastManager.publishSession(session);
    }

    @Override
    public void joinSession(CommunicationSession session) {
        if(session == null)
            throw new IllegalArgumentException("Session must not be null.");
        
        // TODO
        
        _currentSession = session;
    }

    @Override
    public void leaveSession() {
        // TODO
        _currentSession = null;
    }

    @Override
    public CommunicationSession getCurrentSession() {
        return _currentSession;
    }

    @Override
    public Serializable getSetupState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSetupState(Serializable state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
