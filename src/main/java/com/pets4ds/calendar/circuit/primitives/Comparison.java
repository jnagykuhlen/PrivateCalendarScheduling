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
public class Comparison {
    private CircuitBuilder _builder;
    
    public Comparison(CircuitBuilder builder) {
        _builder = builder;
    }
    
    public Wire greaterThan(Wire[] leftOperand, Wire[] rightOperand) {
        int numberOfBits = leftOperand.length;
        if(rightOperand.length != numberOfBits)
            throw new IllegalArgumentException("Bit sizes of inputs do not match.");
        
        if(numberOfBits == 0)
            throw new IllegalArgumentException("Cannot compare integers of bit length 0.");
        
        Wire result = _builder.xor(leftOperand[0], _builder.and(leftOperand[0], rightOperand[0]));
        for(int i = 1; i < numberOfBits; ++i) {
            result = _builder.xor(
                leftOperand[i],
                _builder.and(
                    _builder.xor(leftOperand[i], result),
                    _builder.xor(rightOperand[i], result)
                )
            );
        }
        
        return result;
    }
    
    public Wire lessThan(Wire[] leftOperand, Wire[] rightOperand) {
        return greaterThan(rightOperand, leftOperand);
    }
    
    public Wire greaterOrEqual(Wire[] leftOperand, Wire[] rightOperand) {
        return _builder.not(lessThan(leftOperand, rightOperand));
    }
    
    public Wire lessOrEqual(Wire[] leftOperand, Wire[] rightOperand) {
        return _builder.not(greaterThan(leftOperand, rightOperand));
    }
}
