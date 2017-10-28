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
public class Addition extends BinaryBitwiseOperator {
    public Addition(CircuitBuilder builder) {
        super(builder);
    }

    @Override
    protected Wire[] apply(CircuitBuilder builder, Wire[] leftOperand, Wire[] rightOperand, int numberOfBits) {
        if(numberOfBits == 0)
            return leftOperand;
        
        Wire[] result = new Wire[numberOfBits];
        Wire carryover = builder.and(leftOperand[0], rightOperand[0]);
        
        for(int i = 0; i < numberOfBits; ++i) {
            result[i] = builder.xor(leftOperand[i], rightOperand[i]);
            if(i > 0)
                result[i] = builder.xor(result[i], carryover);
            
            if(i < numberOfBits - 1) {
                carryover = builder.xor(
                    carryover,
                    builder.and(
                        builder.xor(carryover, leftOperand[i]),
                        builder.xor(carryover, rightOperand[i])
                    )
                );
            }
        }
        
        return result;
    }
}
