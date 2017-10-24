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
public abstract class CircuitBuilder {
    private int _nextWireId;
    private int _nextGateId;
    
    public CircuitBuilder() {
        _nextWireId = 0;
        _nextGateId = 0;
    }
    
    protected abstract void addANDGate(int id, Wire leftOperand, Wire rightOperand, Wire result);
    protected abstract void addXORGate(int id, Wire leftOperand, Wire rightOperand, Wire result);
    protected abstract void addNOTGate(int id, Wire operand, Wire result);
    protected abstract void makeInputWire(Wire wire, int partyId);
    protected abstract void makeOutputWire(Wire wire, int partyId);
    
    public Wire and(Wire leftOperand, Wire rightOperand) {
        Wire result = requestWire();
        addANDGate(_nextGateId++, leftOperand, rightOperand, result);
        return result;
    }
    
    public Wire xor(Wire leftOperand, Wire rightOperand) {
        Wire result = requestWire();
        addXORGate(_nextGateId++, leftOperand, rightOperand, result);
        return result;
    }
    
    public Wire not(Wire operand) {
        Wire result = requestWire();
        addNOTGate(_nextGateId++, operand, result);
        return result;
    }
    
    public Wire input(int partyId) {
        Wire result = requestWire();
        makeInputWire(result, partyId);
        return result;
    }
    
    public void output(Wire wire, int partyId) {
        makeOutputWire(wire, partyId);
    }
    
    private Wire requestWire() {
        return new Wire(_nextWireId++);
    }
    
    public int getNumberOfWires() {
        return _nextWireId;
    }
    
    public int getNumberOfGates() {
        return _nextGateId;
    }
}
