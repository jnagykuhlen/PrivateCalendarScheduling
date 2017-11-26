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
public class ParticipantStatusMessage implements Serializable {
    private final CommunicationParty _party;
    private final int _revision;
    
    public ParticipantStatusMessage(CommunicationParty party, int revision) {
        _party = party;
        _revision = revision;
    }
    
    public CommunicationParty getParty() {
        return _party;
    }
    
    public int getRevision() {
        return _revision;
    }
}
