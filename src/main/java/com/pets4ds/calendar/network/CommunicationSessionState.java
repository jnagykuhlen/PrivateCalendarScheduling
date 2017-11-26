/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CommunicationSessionState {
    private final PartyRole _localRole;
    private final CommunicationParty[] _parties;
    private final int _localPartyIndex;
    
    public CommunicationSessionState(PartyRole localRole, CommunicationParty[] parties, int localPartyIndex) {
        _localRole = localRole;
        _parties = parties;
        _localPartyIndex = localPartyIndex;
    }
    
    public PartyRole getLocalRole() {
        return _localRole;
    }
    
    public CommunicationParty[] getParties() {
        return _parties;
    }
    
    public int getLocalPartyIndex() {
        return _localPartyIndex;
    }
}
