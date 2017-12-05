/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.Serializable;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CommunicationParty implements Serializable {
    private static final long serialVersionUID = -8251759593566480560L;
    
    public static final CommunicationParty UNINITIALIZED = new CommunicationParty(null, null, false);
    
    private final String _name;
    private final Serializable _setupState;
    private final boolean _isReady;
    
    public CommunicationParty(String name, Serializable setupState, boolean isReady) {
        _name = name;
        _setupState = setupState;
        _isReady = isReady;
    }
    
    public String getName() {
        return _name;
    }
    
    public Serializable getSetupState() {
        return _setupState;
    }
    
    public boolean isReady() {
        return _isReady;
    }
}
