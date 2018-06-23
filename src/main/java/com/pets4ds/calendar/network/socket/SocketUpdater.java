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
    
    private final CommunicationSession _session;
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
    
    protected <T> T readMessage(Socket socket, CommunicationSetupHandler handler) throws IOException {
        try {
            InputStream inputStream = socket.getInputStream();
            if(inputStream.available() > 0) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                return (T)objectInputStream.readObject();
            }
        } catch(ClassNotFoundException exception) {
            handler.setupError(new NetworkException("Unable to decode message.", exception));
        } catch(ClassCastException exception) {
            handler.setupError(new NetworkException("Received message with incompatible type.", exception));
        }
        
        return null;
    }
    
    protected <T> void writeMessage(Socket socket, T message, CommunicationSetupHandler handler) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject((Object)message);
    }
    
    public CommunicationSession getSession() {
        return _session;
    }
    
    @Override
    public abstract void close() throws IOException;
    public abstract void setLocalParty(CommunicationSetupHandler handler, CommunicationParty localParty);
    public abstract boolean isConnected();
    
    protected abstract void processMessages(CommunicationSetupHandler handler);
    protected abstract void sendStatus(CommunicationSetupHandler handler);
}
