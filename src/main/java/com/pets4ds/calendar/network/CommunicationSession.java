/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CommunicationSession {
    private CommunicationSessionDescription _description;
    private PartyRole _localRole;
    private List<CommunicationParty> _parties;
    private Serializable _state;
    private int _stateRevision;
    
    public CommunicationSession(CommunicationSessionDescription description, PartyRole localRole) {
        _description = description;
        _localRole = localRole;
        _parties = new ArrayList<>();
        _state = null;
        _stateRevision = 0;
    }
    
    public CommunicationSessionDescription getDescription() {
        return _description;
    }
    
    public PartyRole getLocalRole() {
        return _localRole;
    }
    
    public List<CommunicationParty> getParties() {
        return _parties;
    }
    
    public Serializable getState() {
        return _state;
    }
    
    public int getStateRevision() {
        return _stateRevision;
    }
    
    public void setState(int revision, Serializable state) {
        _state = state;
        _stateRevision = revision;
    }
}
