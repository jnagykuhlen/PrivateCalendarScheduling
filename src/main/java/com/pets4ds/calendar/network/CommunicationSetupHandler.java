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
public interface CommunicationSetupHandler {
    void handleSessionDiscovery(CommunicationSession session);
    void handleSetupStateChanged(Serializable state);
    void handleSetupFinished();
    void handleError(SessionSetupException exception);
}
