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
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ClientSocketUpdater extends SocketUpdater {
    private final Socket _socket;
    private CommunicationParty _localParty;
    private int _currentRevision;
    private int _serverRevision;
    private boolean _isConnected;
    
    public ClientSocketUpdater(CommunicationSession session, InetSocketAddress address) throws IOException {
        super(session);
        _socket = new Socket(address.getAddress(), address.getPort());
        _localParty = CommunicationParty.UNINITIALIZED;
        _currentRevision = 0;
        _serverRevision = 0;
        _isConnected = true;
    }
    
    @Override
    public void close() throws IOException {
        _isConnected = false;
        _socket.close();
    }
    
    @Override
    public void setLocalParty(CommunicationSetupHandler handler, CommunicationParty localParty) {
        _localParty = localParty;
        _currentRevision++;
        sendStatus(handler);
    }
    
    @Override
    protected void processMessages(CommunicationSetupHandler handler) {
        try {
            InitiatorStatusMessage statusMessage;
            while((statusMessage = readMessage(_socket, handler)) != null) {
                if(statusMessage.getRevision() > _serverRevision) {
                    CommunicationSessionState sessionState =
                            new CommunicationSessionState(PartyRole.PARTICIPANT, statusMessage.getParties(), statusMessage.getPartyIndex());
                    handler.sessionChanged(getSession(), sessionState);
                    _serverRevision = statusMessage.getRevision();
                }
            }
        } catch(IOException exception) {
            disconnect();
        }
    }
    
    @Override
    protected void sendStatus(CommunicationSetupHandler handler) {
        try {
            ParticipantStatusMessage statusMessage = new ParticipantStatusMessage(_localParty, _currentRevision);
            writeMessage(_socket, statusMessage, handler);
        } catch(IOException exception) {
            disconnect();
        }
    }
    
    private void disconnect() {
        _isConnected = false;
    }
    
    @Override
    public boolean isConnected() {
        return _isConnected;
    }
}
