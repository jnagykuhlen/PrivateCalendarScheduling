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
public abstract class BinaryBitwiseOperator extends BitwiseOperator {
    public BinaryBitwiseOperator(CircuitBuilder builder) {
        super(builder);
    }
    
    public Wire[] apply(Wire[] leftOperand, Wire[] rightOperand) {
        int numberOfBits = leftOperand.length;
        if(rightOperand.length != numberOfBits)
            throw new IllegalArgumentException("Bit sizes of inputs do not match.");
        
        return apply(getBuilder(), leftOperand, rightOperand, numberOfBits);
    }
    
    protected abstract Wire[] apply(CircuitBuilder builder, Wire[] leftOperand, Wire[] rightOperand, int numberOfBits);
    
    public static Wire[] buildAggregateCircuit(BinaryBitwiseOperator operator, Wire[]... operands) {
        if(operands.length <= 0)
            throw new IllegalArgumentException("Aggregation requires at least one operand.");
        
        if(operands.length == 1)
            return operands[0];
        
        int numberOfReducedOperands = (operands.length + 1) / 2;
        Wire[][] reducedOperands = new Wire[numberOfReducedOperands][];
        
        for(int i = 0; i < reducedOperands.length; ++i) {
            int operandIndex = 2 * i;
            if(operandIndex + 1 < operands.length) {
                reducedOperands[i] = operator.apply(operands[operandIndex], operands[operandIndex + 1]);
            } else {
                reducedOperands[i] = operands[operandIndex];
            }
        }
        
        return buildAggregateCircuit(operator, reducedOperands);
    }
}
