/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

import com.pets4ds.calendar.circuit.primitives.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public abstract class SchedulingCircuitGenerator implements CircuitGenerator {
    private final int _numberOfParties;
    private final int _numberOfSlots;
    
    public SchedulingCircuitGenerator(int numberOfParties, int numberOfSlots) {
        _numberOfParties = numberOfParties;
        _numberOfSlots = numberOfSlots;
    }
    
    @Override
    public void generate(CircuitBuilder builder) {
        Wire[][] partyInputWires = new Wire[_numberOfParties][_numberOfSlots];
        for(int partyId = 0; partyId < _numberOfParties; ++partyId) {
            for(int slotId = 0; slotId < _numberOfSlots; ++slotId) {
                partyInputWires[partyId][slotId] = builder.input(partyId);
            }
        }
        
        Wire[] outputWires = generateScheduling(builder, partyInputWires);
        
        for(int partyId = 0; partyId < _numberOfParties; ++partyId) {
            for(int slotId = 0; slotId < outputWires.length; ++slotId) {
                builder.output(outputWires[slotId], partyId);
            }
        }
    }
    
    protected abstract Wire[] generateScheduling(CircuitBuilder builder, Wire[][] partyInputWires);
    
    public int getNumberOfParties() {
        return _numberOfParties;
    }
    
    public int getNumberOfSlots() {
        return _numberOfSlots;
    }
}
