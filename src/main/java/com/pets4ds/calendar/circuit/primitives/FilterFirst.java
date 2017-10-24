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
public class FilterFirst extends UnaryBitwiseOperator {
    public FilterFirst(CircuitBuilder builder) {
        super(builder);
    }

    @Override
    protected Wire[] apply(CircuitBuilder builder, Wire[] operand) {
        if(operand.length <= 1)
            return operand;
        
        Wire[] result = new Wire[operand.length];
        Wire condition = builder.not(operand[0]);
        
        result[0] = operand[0];
        
        for(int i = 1; i < operand.length; ++i) {
            result[i] = builder.and(operand[i], condition);
            condition = builder.and(condition, builder.not(operand[i]));
        }
        
        return result;
    }
}
