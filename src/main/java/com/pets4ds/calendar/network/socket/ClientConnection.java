/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import com.pets4ds.calendar.network.CommunicationParty;
import java.net.Socket;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ClientConnection {
    private final Socket _socket;
    private CommunicationParty _party;
    private int _revision;
    
    public ClientConnection(Socket socket) {
        _socket = socket;
        _party = CommunicationParty.UNINITIALIZED;
        _revision = 0;
    }
    
    public Socket getSocket() {
        return _socket;
    }
    
    public CommunicationParty getParty() {
        return _party;
    }
    
    public void setParty(CommunicationParty party) {
        _party = party;
    }
    
    public int getRevision() {
        return _revision;
    }
    
    public void setRevision(int revision) {
        _revision = revision;
    }
}
