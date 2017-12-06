/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.network.CommunicationSession;
import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingSession extends CommunicationSession implements Serializable {
    private static final long serialVersionUID = 6286036120309027930L;
    
    private final SchedulingSchemeIdentifier _schedulingSchemeIdentifier;
    private final TimeSlot[] _timeSlots;
    
    public SchedulingSession(String name,
                             String descriptionText,
                             InetSocketAddress initiatorAddress,
                             SchedulingSchemeIdentifier schedulingSchemeIdentifier,
                             TimeSlot[] timeSlots) {
        super(name, descriptionText, initiatorAddress);
        _schedulingSchemeIdentifier = schedulingSchemeIdentifier;
        _timeSlots = timeSlots;
    }
    
    public SchedulingSchemeIdentifier getSchedulingSchemeIdentifier() {
        return _schedulingSchemeIdentifier;
    }
    
    public TimeSlot[] getTimeSlots() {
        return _timeSlots;
    }
}
