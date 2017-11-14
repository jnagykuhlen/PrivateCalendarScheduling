/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public interface CommunicationSetup {
    void createSession(CommunicationSession session);
    void joinSession(CommunicationSession session);
    void leaveSession();
    CommunicationSession getCurrentSession();
    
    Serializable getSetupState();
    void setSetupState(Serializable state);
}
