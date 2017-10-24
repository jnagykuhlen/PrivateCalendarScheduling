/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit.primitives;

import com.pets4ds.calendar.circuit.CircuitBuilder;
import com.pets4ds.calendar.circuit.Wire;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class BitwiseAND extends BinaryBitwiseOperator {
    public BitwiseAND(CircuitBuilder builder) {
        super(builder);
    }

    @Override
    protected Wire[] apply(CircuitBuilder builder, Wire[] leftOperand, Wire[] rightOperand, int numberOfBits) {
        Wire[] result = new Wire[numberOfBits];
        for(int i = 0; i < numberOfBits; ++i)
            result[i] = builder.and(leftOperand[i], rightOperand[i]);
        
        return result;
    }
}
