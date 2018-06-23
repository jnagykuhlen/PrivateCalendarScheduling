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
