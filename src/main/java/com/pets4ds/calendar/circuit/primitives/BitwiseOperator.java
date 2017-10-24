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
public abstract class BitwiseOperator {
    private final CircuitBuilder _builder;
    
    public BitwiseOperator(CircuitBuilder builder) {
        _builder = builder;
    }
    
    protected CircuitBuilder getBuilder() {
        return _builder;
    }
}
