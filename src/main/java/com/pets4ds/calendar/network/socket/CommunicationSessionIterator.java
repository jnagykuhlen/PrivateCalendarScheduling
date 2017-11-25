/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationSession;
import java.util.Iterator;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CommunicationSessionIterator implements Iterator<CommunicationSession> {
    private Iterator<SocketUpdater> _innerIterator;
    
    public CommunicationSessionIterator(Iterator<SocketUpdater> innerIterator) {
        _innerIterator = innerIterator;
    }
    
    @Override
    public boolean hasNext() {
        return _innerIterator.hasNext();
    }

    @Override
    public CommunicationSession next() {
        return _innerIterator.next().getSession();
    }
    
}
