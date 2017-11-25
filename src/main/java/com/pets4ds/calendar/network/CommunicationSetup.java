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
    CommunicationSession createSession(CommunicationSessionDescription sessionDescription, CommunicationParty localParty) throws IOException;
    CommunicationSession joinSession(CommunicationSessionDescription sessionDescription, CommunicationParty localParty) throws IOException;
    void leaveSession(CommunicationSession session) throws IOException;
    Iterator<CommunicationSession> getSessions();
    CommunicationSession getSession(CommunicationSessionDescription sessionDescription);
}
