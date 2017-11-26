/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.CommunicationSessionDescription;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import com.pets4ds.calendar.network.NetworkException;
import com.pets4ds.calendar.network.PartyRole;
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
    private CommunicationParty _localParty;
    private int _currentRevision;
    private volatile boolean _isActive;
    
    public ServerSocketUpdater(CommunicationSessionDescription sessionDescription, CommunicationSetupHandler handler, int port) throws IOException {
        super(sessionDescription);
        _handler = handler;
        _serverSocket = new ServerSocket(port);
        _connections = new ArrayList<>();
        _localParty = CommunicationParty.UNINITIALIZED;
        _currentRevision = 0;
        _isActive = true;
    }

    @Override
    public void run() {
        while (_isActive) {
            try {
                Socket socket = _serverSocket.accept();
                synchronized(_connections) {
                    _connections.add(new ClientConnection(socket));
                }
            } catch(IOException exception) {
                if(_isActive)
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
        }
        
        _localParty = CommunicationParty.UNINITIALIZED;
        _serverSocket.close();
    }
    
    @Override
    public void setLocalParty(CommunicationSetupHandler handler, CommunicationParty localParty) {
        synchronized(_connections) {
            _localParty = localParty;
            publishUpdate(handler);
        }
    }
    
    @Override
    protected void processMessages(CommunicationSetupHandler handler) {
        synchronized(_connections) {
            boolean clientUpdated = false;
            
            for(int i = _connections.size() - 1; i >= 0; --i) {
                ClientConnection connection = _connections.get(i);

                try {
                    ParticipantStatusMessage statusMessage;
                    while((statusMessage = readMessage(connection.getSocket(), handler)) != null) {
                        if(statusMessage.getRevision() > connection.getRevision()) {
                            connection.setParty(statusMessage.getParty());
                            connection.setRevision(statusMessage.getRevision());
                            clientUpdated = true;
                        }
                    }
                } catch(IOException exception) {
                    closeConnection(handler, i);
                }
            }
            
            if(clientUpdated)
                publishUpdate(handler);
        }
    }
    
    @Override
    protected void sendStatus(CommunicationSetupHandler handler) {
        synchronized(_connections) {
            for(int i = _connections.size() - 1; i >= 0; --i) {
                InitiatorStatusMessage statusMessage = new InitiatorStatusMessage(getParties(), i + 1, _currentRevision);
                
                try {
                    writeMessage(_connections.get(i).getSocket(), statusMessage, handler);
                } catch(IOException exception) {
                    closeConnection(handler, i);
                }
            }
        }
    }
    
    private void closeConnection(CommunicationSetupHandler handler, int index) {
        try {
            _connections.remove(index).getSocket().close();
        } catch(IOException exception) {
            handler.handleSetupError(new NetworkException("Unable to close socket.", exception));
        }
        
        publishUpdate(handler);
    }
    
    private void publishUpdate(CommunicationSetupHandler handler) {
        synchronized(_connections) {
            _currentRevision++;
            handler.handleSessionChanged(getSessionDescription(), new CommunicationSessionState(PartyRole.INITIATOR, getParties(), 0));
            sendStatus(handler);
        }
    }
    
    private CommunicationParty[] getParties() {
        CommunicationParty[] parties = new CommunicationParty[_connections.size() + 1];
        parties[0] = _localParty;
        for(int i = 0; i < _connections.size(); ++i)
            parties[i + 1] = _connections.get(i).getParty();

        return parties;
    }
    
    @Override
    public boolean isConnected() {
        return true;
    }
}
