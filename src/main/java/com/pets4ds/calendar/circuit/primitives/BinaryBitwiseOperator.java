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
package com.pets4ds.calendar.circuit.primitives;

import com.pets4ds.calendar.circuit.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public abstract class BinaryBitwiseOperator {
    private CircuitBuilder _builder;
    
    public BinaryBitwiseOperator(CircuitBuilder builder) {
        _builder = builder;
    }
    
    public Wire[] apply(Wire[] leftOperand, Wire[] rightOperand) {
        int numberOfBits = leftOperand.length;
        if(rightOperand.length != numberOfBits)
            throw new IllegalArgumentException("Bit sizes of inputs do not match.");
        
        return apply(_builder, leftOperand, rightOperand, numberOfBits);
    }
    
    protected abstract Wire[] apply(CircuitBuilder builder, Wire[] leftOperand, Wire[] rightOperand, int numberOfBits);
    
    public Wire[] applyAggregated(Wire[]... operands) {
        if(operands.length <= 0)
            throw new IllegalArgumentException("Aggregation requires at least one operand.");
        
        if(operands.length == 1)
            return operands[0];
        
        int numberOfReducedOperands = (operands.length + 1) / 2;
        Wire[][] reducedOperands = new Wire[numberOfReducedOperands][];
        
        for(int i = 0; i < reducedOperands.length; ++i) {
            int operandIndex = 2 * i;
            if(operandIndex + 1 < operands.length) {
                reducedOperands[i] = apply(operands[operandIndex], operands[operandIndex + 1]);
            } else {
                reducedOperands[i] = operands[operandIndex];
            }
        }
        
        return applyAggregated(reducedOperands);
    }
}
