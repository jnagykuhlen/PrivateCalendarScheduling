/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.*;
import java.io.*;
import java.net.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class BroadcastManager implements Runnable, Closeable {
    private final int MAX_PACKET_SIZE = 10000;
    
    private CommunicationSetupHandler _handler;
    private DatagramSocket _socket;
    private int _port;
    private volatile boolean _isActive;
    
    public BroadcastManager(CommunicationSetupHandler handler, int port) {
        _handler = handler;
        _socket = null;
        _port = port;
        _isActive = false;
    }
    
    @Override
    public void run() {
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
                    CommunicationSession session = (CommunicationSession)objectInputStream.readObject();
                    _handler.handleSessionDiscovery(session);
                } catch(Exception exception) {
                    _handler.handleError(new SessionSetupException("Unable to decode received session from broadcast.", exception));
                }
            }
        } catch (IOException exception) {
            if(_isActive)
                _handler.handleError(new SessionSetupException("Failed to receive broadcast.", exception));
        } catch(Exception exception) {
            _handler.handleError(new SessionSetupException("Failed to initialize broadcast socket.", exception));
        }
    }
    
    public synchronized void publishSession(CommunicationSession session) {
        if(_isActive) {
            try {
                ByteArrayOutputStream binaryOutputSream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputSream = new ObjectOutputStream(binaryOutputSream);
                objectOutputSream.writeObject(session);
                byte[] packetBuffer = binaryOutputSream.toByteArray();
                
                DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length, InetAddress.getByName("255.255.255.255"), _port);
                _socket.send(packet);
            } catch(Exception exception) {
                _handler.handleError(new SessionSetupException("Failed to send broadcast.", exception));
            }
        }
    }
    
    @Override
    public synchronized void close() {
        if(_isActive) {
            _isActive = false;
            _socket.close();
        }
    }
}
