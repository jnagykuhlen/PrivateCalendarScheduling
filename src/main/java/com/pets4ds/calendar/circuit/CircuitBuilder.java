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
        if(!isValidWire(leftOperand) || !isValidWire(rightOperand))
            throw new IllegalArgumentException("Invalid wire.");
        
        if(leftOperand == Wire.ZERO || rightOperand == Wire.ZERO)
            return Wire.ZERO;
        
        if(leftOperand == Wire.ONE)
            return rightOperand;
        
        if(rightOperand == Wire.ONE)
            return leftOperand;
        
        Wire result = requestWire();
        addANDGate(_nextGateId++, leftOperand, rightOperand, result);
        return result;
    }
    
    public Wire xor(Wire leftOperand, Wire rightOperand) {
        if(!isValidWire(leftOperand) || !isValidWire(rightOperand))
            throw new IllegalArgumentException("Invalid wire.");
        
        if(leftOperand == Wire.ZERO)
            return rightOperand;
        
        if(rightOperand == Wire.ZERO)
            return leftOperand;
        
        if(leftOperand == Wire.ONE)
            return not(rightOperand);
        
        if(rightOperand == Wire.ONE)
            return not(leftOperand);
        
        Wire result = requestWire();
        addXORGate(_nextGateId++, leftOperand, rightOperand, result);
        return result;
    }
    
    public Wire not(Wire operand) {
        if(!isValidWire(operand))
            throw new IllegalArgumentException("Invalid wire.");
        
        if(operand == Wire.ZERO)
            return Wire.ONE;
        
        if(operand == Wire.ONE)
            return Wire.ZERO;
        
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
        if(isConstantWire(wire))
            throw new IllegalArgumentException("Constant wires are not allowed as output.");
        
        if(!isValidWire(wire))
            throw new IllegalArgumentException("Invalid wire.");
        
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
    
    private boolean isConstantWire(Wire wire) {
        return wire == Wire.ZERO || wire == Wire.ONE;
    }
    
    private boolean isValidWire(Wire wire) {
        return isConstantWire(wire) || (wire.getId() >= 0 && wire.getId() < _nextWireId);
    }
}
