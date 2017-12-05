/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.circuit.CircuitGenerator;
import com.pets4ds.calendar.circuit.FirstMatchSchedulingCircuitGenerator;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class FirstMatchSchedulingScheme extends SchedulingScheme {
    @Override
    public CircuitGenerator createCircuitGenerator(int numberOfParties, int numberOfTimeslots) {
        return new FirstMatchSchedulingCircuitGenerator(numberOfParties, numberOfTimeslots);
    }
    
}
