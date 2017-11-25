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
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public abstract class SocketUpdater implements Closeable {
    private static final int SEND_INTERVAL = 2000;
    
    private CommunicationSession _session;
    private int _sendTimer;
    
    public SocketUpdater(CommunicationSession session) {
        _session = session;
        _sendTimer = 0;
    }
    
    public final void update(CommunicationSetupHandler handler, int elapsedTime) {
        processMessages(handler);
        
        if(_sendTimer >= SEND_INTERVAL) {
            sendStatus(handler);
            _sendTimer -= SEND_INTERVAL;
        } else {
            _sendTimer += elapsedTime;
        }
    }
    
    protected <T> T readMessage(Socket socket, CommunicationSetupHandler handler) {
        try {
            InputStream inputStream = socket.getInputStream();
            if(inputStream.available() > 0) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                return (T)objectInputStream.readObject();
            }
        } catch(ClassNotFoundException exception) {
            handler.handleSetupError(new NetworkException("Unable to decode message.", exception));
        } catch(ClassCastException exception) {
            handler.handleSetupError(new NetworkException("Received message with incompatible type.", exception));
        } catch(IOException exception) {
            handler.handleSetupError(new NetworkException("Unable to read message data.", exception));
        }
        
        return null;
    }
    
    protected <T> void writeMessage(Socket socket, T message, CommunicationSetupHandler handler) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch(IOException exception) {
            handler.handleSetupError(new NetworkException("Unable to send message data.", exception));
        }
    }
    
    @Override
    public abstract void close() throws IOException;
    
    protected abstract void processMessages(CommunicationSetupHandler handler);
    protected abstract void sendStatus(CommunicationSetupHandler handler);
    
    public CommunicationSession getSession() {
        return _session;
    }
}
