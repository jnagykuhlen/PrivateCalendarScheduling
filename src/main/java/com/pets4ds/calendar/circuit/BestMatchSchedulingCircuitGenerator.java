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
public class BestMatchSchedulingCircuitGenerator extends SchedulingCircuitGenerator {
    public BestMatchSchedulingCircuitGenerator(int numberOfParties, int numberOfSlots) {
        super(numberOfParties, numberOfSlots);
    }

    @Override
    protected Wire[] generateScheduling(CircuitBuilder builder, Wire[][] partyInputWires) {
        final Addition addition = new Addition(builder);
        final Comparison comparison = new Comparison(builder);
        final Multiplex multiplex = new Multiplex(builder);
        
        final int countLimit = getNumberOfParties();
        final int requiredBits = Integer.SIZE - Integer.numberOfLeadingZeros(countLimit);
        
        final Wire zero = builder.xor(partyInputWires[0][0], partyInputWires[0][0]);
        final Wire one = builder.not(zero);
        
        Wire[][] counts = new Wire[getNumberOfSlots()][];
        for(int slotId = 0; slotId < getNumberOfSlots(); ++slotId)
            counts[slotId] = WireBlock.expand(partyInputWires[0][slotId], zero, requiredBits);
        
        for(int partyId = 1; partyId < getNumberOfParties(); ++partyId) {
            for(int slotId = 0; slotId < getNumberOfSlots(); ++slotId) {
                counts[slotId] = addition.apply(
                    counts[slotId],
                    WireBlock.expand(partyInputWires[partyId][slotId], zero, requiredBits));
            }
        }
        
        Wire[] result = new Wire[getNumberOfSlots()];
        Wire[] maxCount = counts[getNumberOfSlots() - 1];
        
        result[getNumberOfSlots() - 1] = one;
        for(int slotId = getNumberOfSlots() - 2; slotId >= 0; --slotId) {
            result[slotId] = comparison.greaterOrEqual(counts[slotId], maxCount);
            maxCount = multiplex.apply(maxCount, counts[slotId], result[slotId]);
        }
        
        return (new FilterFirst(builder)).apply(result);
    }
}
