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
public interface CommunicationSetupHandler {
    void handleSessionChanged(CommunicationSession session, CommunicationSessionState sessionState);
    void handleSessionDisconnected(CommunicationSession session);
    void handleSetupError(NetworkException exception);
}
