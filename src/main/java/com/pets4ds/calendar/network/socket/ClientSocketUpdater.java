/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ClientSocketUpdater extends SocketUpdater {
    private final CommunicationParty _localParty;
    private final Socket _socket;
    
    public ClientSocketUpdater(CommunicationSession session, CommunicationParty localParty, InetSocketAddress address) throws IOException {
        super(session);
        _localParty = localParty;
        _socket = new Socket(address.getAddress(), address.getPort());
    }
    
    @Override
    public void close() throws IOException {
        _socket.close();
    }
    
    @Override
    protected void processMessages(CommunicationSetupHandler handler) {
        InitiatorStatusMessage statusMessage;
        while((statusMessage = readMessage(_socket, handler)) != null) {
            handleStatusMessage(statusMessage);
        }
    }
    
    private void handleStatusMessage(InitiatorStatusMessage statusMessage) {
        // TODO
    }
    
    @Override
    protected void sendStatus(CommunicationSetupHandler handler) {
        ParticipantStatusMessage statusMessage = new ParticipantStatusMessage(_localParty);
        writeMessage(_socket, statusMessage, handler);
    }
}
