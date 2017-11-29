/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.network.CommunicationSessionState;
import java.net.InetSocketAddress;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SessionInfo {
    private final InetSocketAddress _localSchedulingAddress;
    private boolean _isReady;
    
    public SessionInfo(InetSocketAddress localSchedulingAddress) {
        _localSchedulingAddress = localSchedulingAddress;
        _isReady = false;
    }
    
    public InetSocketAddress getLocalSchedulingAddress() {
        return _localSchedulingAddress;
    }
    
    public boolean getReadyState() {
        return _isReady;
    }
    
    public void setReadyState(boolean isReady) {
        _isReady = isReady;
    }
}
