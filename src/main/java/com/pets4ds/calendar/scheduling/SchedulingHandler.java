/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.NetworkException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public interface SchedulingHandler {
    void sessionInviteReceived(SchedulingSession session);
    void sessionChanged(SchedulingSession session, CommunicationSessionState sessionState);
    void sessionDisconnected(SchedulingSession session);
    void networkError(NetworkException exception);
    
    void schedulingStarted(SchedulingSession session);
    void schedulingFinished(SchedulingSession session, Optional<Integer> selectedSlotIndex);
}
