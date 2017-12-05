/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TimeSlot implements Serializable {
    private static final long serialVersionUID = -6959102698676055550L;

    private LocalDateTime _start;
    private LocalDateTime _end;
    
    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        if(start == null || end == null)
            throw new IllegalArgumentException("Start and end time must not be null.");
        
        if(start.isAfter(end))
            throw new IllegalArgumentException("End time must be after start time.");
        
        _start = start;
        _end = end;
    }
    
    public LocalDateTime getStartDate() {
        return _start;
    }
    
    public LocalDateTime getEndDate() {
        return _end;
    }
}
