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
public class FirstMatchSchedulingCircuitGenerator extends SchedulingCircuitGenerator {
    public FirstMatchSchedulingCircuitGenerator(int numberOfParties, int numberOfSlots) {
        super(numberOfParties, numberOfSlots);
    }

    @Override
    protected Wire[] generateScheduling(CircuitBuilder builder, Wire[][] partyInputWires) {
        Wire[] intermediateWires = (new BitwiseAND(builder)).applyAggregated(partyInputWires);
        return (new FilterFirst(builder)).apply(intermediateWires);
    }
}
