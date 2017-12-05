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
public enum SchedulingSchemeIdentifier {
    FIRST_MATCH("First Match", new FirstMatchSchedulingScheme()),
    BEST_MATCH("Best Match", new BestMatchSchedulingScheme());
    
    private final String _fullName;
    private final SchedulingScheme _schedulingScheme;
    
    SchedulingSchemeIdentifier(String fullName, SchedulingScheme schedulingScheme) {
        _fullName = fullName;
        _schedulingScheme = schedulingScheme;
    }
    
    public String getFullName() {
        return _fullName;
    }
    
    public SchedulingScheme getSchedulingScheme() {
        return _schedulingScheme;
    }
    
    @Override
    public String toString() {
        return _fullName;
    }
}
