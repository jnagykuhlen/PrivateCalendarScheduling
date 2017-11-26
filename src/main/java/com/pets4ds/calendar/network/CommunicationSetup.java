/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.*;
import java.util.Iterator;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public interface CommunicationSetup extends Closeable {
    void createSession(CommunicationSessionDescription sessionDescription) throws IOException;
    void joinSession(CommunicationSessionDescription sessionDescription) throws IOException;
    void leaveSession(CommunicationSessionDescription sessionDescription) throws IOException;
    void setLocalParty(CommunicationSessionDescription sessionDescription, CommunicationParty localParty);
    boolean isParticipating(CommunicationSessionDescription sessionDescription);
}
