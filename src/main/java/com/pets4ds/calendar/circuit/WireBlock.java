/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
