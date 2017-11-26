/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class AddressHelper {
    private final int _minPort;
    private final int _maxPort;
    private int _nextPort;
    
    public AddressHelper(int minPort, int maxPort) {
        _minPort = minPort;
        _maxPort = maxPort;
        _nextPort = minPort;
    }
    
    public InetSocketAddress getNextLocalAddress() {
        InetSocketAddress localAddress = new InetSocketAddress(_nextPort);
        try {
            localAddress = new InetSocketAddress(InetAddress.getLocalHost(), _nextPort);
        } catch(UnknownHostException exception) {
            Logger.getLogger(getClass().getName()).log(
                Level.WARNING,
                "Unable to query local IP address.",
                exception
            );
        }
        
        _nextPort++;
        if(_nextPort > _maxPort)
            _nextPort = _minPort;
        
        return localAddress;
    }
}
