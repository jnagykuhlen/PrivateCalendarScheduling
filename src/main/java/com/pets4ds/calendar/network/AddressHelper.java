/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class AddressHelper {
    private final static int MIN_RANDOM_PORT = 5000;
    private final static int MAX_RANDOM_PORT = 65534;
    
    private final int _minPort;
    private final int _maxPort;
    private int _nextPort;
    private Random _random;
    
    public AddressHelper(int minPort, int maxPort) {
        _minPort = minPort;
        _maxPort = maxPort;
        _nextPort = minPort;
        _random = new Random();
    }
    
    public InetSocketAddress getNextLocalAddress() {
        InetSocketAddress localAddress = getLocalAddressFromPort(_nextPort);
        
        _nextPort++;
        if(_nextPort > _maxPort)
            _nextPort = _minPort;
        
        return localAddress;
    }
    
    public InetSocketAddress getRandomLocalAddress() {
        return getLocalAddressFromPort(_random.nextInt(MAX_RANDOM_PORT - MIN_RANDOM_PORT) + MIN_RANDOM_PORT);
    }
    
    public InetSocketAddress getLocalAddressFromPort(int port) {
        InetSocketAddress localAddress = new InetSocketAddress(port);
        try {
            localAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
        } catch(UnknownHostException exception) {
            Logger.getLogger(getClass().getName()).log(
                Level.WARNING,
                "Unable to query local IP address.",
                exception
            );
        }
        
        return localAddress;
    }
}
