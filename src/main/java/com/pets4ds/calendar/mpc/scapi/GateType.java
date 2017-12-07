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
public enum GateType {
    AND("0001"),
    XOR("0110"),
    NOT("10");
    
    private final String _truthTable;
    
    GateType(String truthTable) {
        _truthTable = truthTable;
    }
    
    public String getTruthTable() {
        return _truthTable;
    }
}
