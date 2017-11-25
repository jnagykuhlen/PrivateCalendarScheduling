/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket.commands;

import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import com.pets4ds.calendar.network.SessionCommand;
import java.io.Serializable;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class StateUpdateCommand implements SessionCommand {
    private int _revision;
    private Serializable _state;
    
    public StateUpdateCommand(int revision, Serializable state) {
        _revision = revision;
        _state = state;
    }
    
    @Override
    public void execute(CommunicationSession session, CommunicationSetupHandler handler) {
        session.setState(_revision, _state);
    }
}
