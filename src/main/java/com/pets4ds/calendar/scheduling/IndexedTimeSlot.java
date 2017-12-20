/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class IndexedTimeSlot {
    private final TimeSlot _timeSlot;
    private final int _index;
    
    public IndexedTimeSlot(TimeSlot timeSlot, int index) {
        _timeSlot = timeSlot;
        _index = index;
    }
    
    public TimeSlot getTimeSlot() {
        return _timeSlot;
    }
    
    public int getIndex() {
        return _index;
    }
}
