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
