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
