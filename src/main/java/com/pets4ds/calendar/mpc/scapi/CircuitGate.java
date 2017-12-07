/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.mpc.scapi;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CircuitGate {
    private final GateType _type;
    private final int _gateId;
    private final int[] _inputIds;
    private final int _outputId;
    
    public CircuitGate(GateType type, int gateId, int[] inputIds, int outputId) {
        _type = type;
        _gateId = gateId;
        _inputIds = inputIds;
        _outputId = outputId;
    }
    
    public GateType getType() {
        return _type;
    }
    
    public int getGateId() {
        return _gateId;
    }
    
    public int[] getInputIds() {
        return _inputIds;
    }
    
    public int getOutputId() {
        return _outputId;
    }
}
