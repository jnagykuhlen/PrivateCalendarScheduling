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
