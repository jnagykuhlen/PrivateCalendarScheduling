/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationParty;
import java.io.Serializable;
import java.text.MessageFormat;
import java.time.Instant;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ParticipantStatusMessage implements Serializable {
    private final CommunicationParty _party;
    private final int _revision;
    private final long _timeStamp;
    
    public ParticipantStatusMessage(CommunicationParty party, int revision) {
        _party = party;
        _revision = revision;
        _timeStamp = Instant.now().toEpochMilli();
    }
    
    public CommunicationParty getParty() {
        return new CommunicationParty(
            MessageFormat.format("{0} [{1}ms]", _party.getName(), (Instant.now().toEpochMilli() - _timeStamp)),
            _party.getSetupState(),
            _party.isReady()
        );
    }
    
    public int getRevision() {
        return _revision;
    }
}
