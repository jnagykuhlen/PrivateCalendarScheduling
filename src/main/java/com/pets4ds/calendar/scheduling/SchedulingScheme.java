/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.circuit.CircuitGenerator;
import java.util.Optional;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public abstract class SchedulingScheme {
    public abstract CircuitGenerator createCircuitGenerator(int numberOfParties, int numberOfTimeslots);
    
    public Optional<Integer> getSelectedSlotIndex(byte[] output) throws InvalidOutputException {
        Optional<Integer> result = Optional.empty();
        
        for(int i = 0; i < output.length; ++i) {
            if(output[i] != 0) {
                if(result.isPresent()) {
                    throw new InvalidOutputException("More than one output bit is set.");
                } else {
                    result = Optional.of(i);
                }
            }
        }
        
        return result;
    }
}
