/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit.primitives;

import com.pets4ds.calendar.circuit.WireBlock;
import com.pets4ds.calendar.circuit.CircuitBuilder;
import com.pets4ds.calendar.circuit.Wire;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ConstantMultiply extends UnaryBitwiseOperator {
    private int _factor;
    
    public ConstantMultiply(CircuitBuilder builder, int factor) {
        super(builder);
        
        if(factor <= 0)
            throw new IllegalArgumentException("Constant factor must be positive.");
        
        _factor = factor;
    }

    @Override
    protected Wire[] apply(CircuitBuilder builder, Wire[] operand) {
        final Addition addition = new Addition(builder);
                
        Wire[] sum = null;
        
        for(int i = 0; i < Integer.SIZE; ++i) {
            if(((_factor >> i) & 1) > 0) {
                Wire[] addend = WireBlock.shiftLeft(operand, i);
                if(sum == null) {
                    sum = addend;
                } else {
                    sum = addition.apply(sum, addend);
                }
            }
        }
        
        return sum;
    }
}
