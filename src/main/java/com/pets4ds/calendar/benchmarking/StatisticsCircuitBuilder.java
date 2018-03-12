/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.benchmarking;

import com.pets4ds.calendar.circuit.CircuitBuilder;
import com.pets4ds.calendar.circuit.Wire;
import java.util.ArrayList;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class StatisticsCircuitBuilder extends CircuitBuilder {
    private ArrayList<Integer> _wireDepths;
    private int _maxDepth;
    private int _numberOfAndGates;
    
    public StatisticsCircuitBuilder() {
        _wireDepths = new ArrayList<>();
        _maxDepth = 0;
        _numberOfAndGates = 0;
    }
    
    @Override
    protected void addANDGate(int id, Wire leftOperand, Wire rightOperand, Wire result) {
        ensureWire(leftOperand.getId());
        ensureWire(rightOperand.getId());
        createWire(result.getId());
        
        final int leftDepth  = getWireDepth(leftOperand.getId());
        final int rightDepth = getWireDepth(rightOperand.getId());
        setWireDepth(result.getId(), Math.max(leftDepth, rightDepth) + 1);
        
        _numberOfAndGates++;
    }

    @Override
    protected void addXORGate(int id, Wire leftOperand, Wire rightOperand, Wire result) {
        ensureWire(leftOperand.getId());
        ensureWire(rightOperand.getId());
        createWire(result.getId());
        
        final int leftDepth  = getWireDepth(leftOperand.getId());
        final int rightDepth = getWireDepth(rightOperand.getId());
        setWireDepth(result.getId(), Math.max(leftDepth, rightDepth));
    }

    @Override
    protected void addNOTGate(int id, Wire operand, Wire result) {
        ensureWire(operand.getId());
        createWire(result.getId());
        setWireDepth(result.getId(), getWireDepth(operand.getId()));
    }

    @Override
    protected void makeInputWire(Wire wire, int partyId) {
        createWire(wire.getId());
    }

    @Override
    protected void makeOutputWire(Wire wire, int partyId) {
        ensureWire(wire.getId());
        _maxDepth = Math.max(_maxDepth, getWireDepth(wire.getId()));
    }
    
    private void createWire(int id) {
        final int size = id + 1;
        if(_wireDepths.size() < size) {
            _wireDepths.ensureCapacity(size);
            while(_wireDepths.size() < size)
                _wireDepths.add(0);
        }
    }
    
    private void ensureWire(int id) {
        if(id < 0 || id >= _wireDepths.size())
            throw new IllegalArgumentException("A wire with this ID does not exist.");
    }
    
    private int getWireDepth(int id) {
        return _wireDepths.get(id);
    }
    
    private void setWireDepth(int id, int depth) {
        _wireDepths.set(id, depth);
    }
    
    public int getMaxDepth() {
        return _maxDepth;
    }
    
    public int getNumberOfANDGates() {
        return _numberOfAndGates;
    }
}
