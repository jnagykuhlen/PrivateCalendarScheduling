/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CommunicationSessionDescription implements Serializable {
    private String _name;
    private String _descriptionText;
    private InetSocketAddress _initiatorAddress;
    private Serializable _userData;
    private UUID _uuid;
    
    public CommunicationSessionDescription(String name, String descriptionText, InetSocketAddress initiatorAddress, Serializable userData) {
        _name = name;
        _descriptionText = descriptionText;
        _initiatorAddress = initiatorAddress;
        _userData = userData;
        _uuid = UUID.randomUUID();
    }
    
    public String getName() {
        return _name;
    }
    
    public String getDescriptionText() {
        return _descriptionText;
    }
    
    public InetSocketAddress getInitiatorAddress() {
        return _initiatorAddress;
    }
    
    public Serializable getUserData() {
        return _userData;
    }
    
    public UUID getUUID() {
        return _uuid;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this._uuid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CommunicationSessionDescription other = (CommunicationSessionDescription) obj;
        if (!Objects.equals(this._uuid, other._uuid)) {
            return false;
        }
        return true;
    }
}
