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

import com.pets4ds.calendar.network.*;
import java.io.*;
import java.net.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class DatagramBroadcastChannel implements BroadcastChannel {
    private final int MAX_PACKET_SIZE = 10000;
    
    private BroadcastHandler _handler;
    private DatagramSocket _socket;
    private int _port;
    private volatile boolean _isActive;
    
    public DatagramBroadcastChannel(BroadcastHandler handler, int port) {
        _handler = handler;
        _socket = null;
        _port = port;
        _isActive = false;
    }
    
    @Override
    public void start() {
        try {
            _socket = new DatagramSocket(null);
            _socket.setReuseAddress(true);
            _socket.setBroadcast(true);
            _socket.bind(new InetSocketAddress("0.0.0.0", _port));
            
            _isActive = true;
            
            while (_isActive) {
                byte[] packetBuffer = new byte[MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(packetBuffer, MAX_PACKET_SIZE);
                
                _socket.receive(packet);

                ByteArrayInputStream binaryInputStream = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                ObjectInputStream objectInputStream = new ObjectInputStream(binaryInputStream);

                try {
                    _handler.broadcastReceived((Serializable)objectInputStream.readObject());
                } catch(Exception exception) {
                    _handler.broadcastError(new NetworkException("Unable to decode broadcast information..", exception));
                }
            }
        } catch (IOException exception) {
            if(_isActive)
                _handler.broadcastError(new NetworkException("Failed to receive broadcast.", exception));
        } catch(Exception exception) {
            _handler.broadcastError(new NetworkException("Failed to initialize broadcast socket.", exception));
        }
    }
    
    @Override
    public synchronized void stop() {
        if(_isActive) {
            _isActive = false;
            _socket.close();
            _socket = null;
        }
    }
    
    @Override
    public synchronized void publish(Serializable info) {
        if(_isActive) {
            try {
                ByteArrayOutputStream binaryOutputSream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputSream = new ObjectOutputStream(binaryOutputSream);
                objectOutputSream.writeObject(info);
                byte[] packetBuffer = binaryOutputSream.toByteArray();
                
                DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length, InetAddress.getByName("255.255.255.255"), _port);
                _socket.send(packet);
            } catch(Exception exception) {
                _handler.broadcastError(new NetworkException("Failed to send broadcast.", exception));
            }
        }
    }
}
