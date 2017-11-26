/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationParty;
import java.io.Serializable;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class InitiatorStatusMessage implements Serializable {
    private final CommunicationParty[] _parties;
    private final int _partyIndex;
    private final int _revision;
    
    public InitiatorStatusMessage(CommunicationParty[] parties, int partyIndex, int revision) {
        _parties = parties;
        _partyIndex = partyIndex;
        _revision = revision;
    }
    
    public CommunicationParty[] getParties() {
        return _parties;
    }
    
    public int getPartyIndex() {
        return _partyIndex;
    }
    
    public int getRevision() {
        return _revision;
    }
}
