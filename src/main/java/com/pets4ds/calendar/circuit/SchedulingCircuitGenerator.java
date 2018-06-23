/*
 * MIT License
 *
 * Copyright (c) 2018 Jonas Nagy-Kuhlen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    
    @Override
    public int getNumberOfParties() {
        return _numberOfParties;
    }
    
    public int getNumberOfSlots() {
        return _numberOfSlots;
    }
}
