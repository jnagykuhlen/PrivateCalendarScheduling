/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit.primitives;

import com.pets4ds.calendar.circuit.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class Multiplex {
    private CircuitBuilder _builder;
    
    public Multiplex(CircuitBuilder builder) {
        _builder = builder;
    }
    
    public Wire[] apply(Wire[] leftOperand, Wire[] rightOperand, Wire condition) {
        int numberOfBits = leftOperand.length;
        if(rightOperand.length != numberOfBits)
            throw new IllegalArgumentException("Bit sizes of inputs do not match.");
        
        Wire[] result = new Wire[numberOfBits];
        for(int i = 0; i < numberOfBits; ++i) {
            result[i] = _builder.xor(
                leftOperand[i],
                _builder.and(
                    condition,
                    _builder.xor(leftOperand[i], rightOperand[i])
                )
            );
        }
        
        return result;
    }
}
