/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import com.pets4ds.calendar.network.NetworkException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ServerSocketUpdater extends SocketUpdater implements Runnable {
    private final CommunicationSetupHandler _handler;
    private final ServerSocket _serverSocket;
    private final List<ClientConnection> _connections;
    private volatile boolean _isActive;
    
    public ServerSocketUpdater(CommunicationSession session, CommunicationParty localParty, CommunicationSetupHandler handler, int port) throws IOException {
        super(session);
        _handler = handler;
        _serverSocket = new ServerSocket(port);
        _connections = new ArrayList<>();
        _isActive = true;
        
        session.getParties().add(localParty);
    }

    @Override
    public void run() {
        while (_isActive) {
            try {
                Socket socket = _serverSocket.accept();
                synchronized(_connections) {
                    _connections.add(new ClientConnection(socket));
                    getSession().getParties().add(new CommunicationParty("<unknown>", null, false));
                }
            } catch(IOException exception) {
                _handler.handleSetupError(new NetworkException("Failed to accept incoming connection.", exception));
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        _isActive = false;
        
        synchronized(_connections) {
            for(ClientConnection connection : _connections)
                connection.getSocket().close();

            _connections.clear();
            getSession().getParties().clear();
        }
        
        _serverSocket.close();
    }
    
    @Override
    protected void processMessages(CommunicationSetupHandler handler) {
        for(int i = _connections.size() - 1; i >= 0; --i) {
            ClientConnection connection = _connections.get(i);
            
            ParticipantStatusMessage statusMessage;
            while((statusMessage = readMessage(connection.getSocket(), handler)) != null) {
                handleStatusMessage(statusMessage, i);
            }
        }
    }
    
    private void handleStatusMessage(ParticipantStatusMessage statusMessage, int index) {
        synchronized(_connections) {
            getSession().getParties().set(index + 1, statusMessage.getParty());
        }
    }
    
    @Override
    protected void sendStatus(CommunicationSetupHandler handler) {
        for(int i = _connections.size() - 1; i >= 0; --i) {
            ClientConnection connection = _connections.get(i);
            
            InitiatorStatusMessage statusMessage = new InitiatorStatusMessage((CommunicationParty[])getSession().getParties().toArray(), i + 1);
            writeMessage(connection.getSocket(), statusMessage, handler);
        }
    }
}
