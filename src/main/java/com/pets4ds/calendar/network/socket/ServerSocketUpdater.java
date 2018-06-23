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

import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.CommunicationSession;
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
    
    public ServerSocketUpdater(CommunicationSession session, CommunicationSetupHandler handler, int port) throws IOException {
        super(session);
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
                    _handler.setupError(new NetworkException("Failed to accept incoming connection.", exception));
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
            handler.setupError(new NetworkException("Unable to close socket.", exception));
        }
        
        publishUpdate(handler);
    }
    
    private void publishUpdate(CommunicationSetupHandler handler) {
        synchronized(_connections) {
            _currentRevision++;
            handler.sessionChanged(getSession(), new CommunicationSessionState(PartyRole.INITIATOR, getParties(), 0));
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
