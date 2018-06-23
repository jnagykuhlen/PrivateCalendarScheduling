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
package com.pets4ds.calendar.circuit;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public final class WireBlock {
    public static Wire[] shiftLeft(Wire[] operand, int shift) {
        Wire[] result = new Wire[operand.length];
        for(int i = 0; i < operand.length; ++i)
            result[(i + shift) % operand.length] = operand[i];
        
        return result;
    }
    
    public static Wire[] shiftRight(Wire[] operand, int shift) {
        return shiftLeft(operand, -shift);
    }
    
    public static Wire[] expand(Wire bit, int numberOfBits) {
        Wire[] result = new Wire[numberOfBits];
        
        if(numberOfBits > 0) {
            result[0] = bit;
            for(int i = 1; i < numberOfBits; ++i)
                result[i] = Wire.ZERO;
        }
        
        return result;
    }
}
