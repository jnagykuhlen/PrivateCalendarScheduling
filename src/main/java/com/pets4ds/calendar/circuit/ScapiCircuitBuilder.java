/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

import edu.biu.scapi.circuits.circuit.*;
import java.util.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public final class ScapiCircuitBuilder extends CircuitBuilder {
    private final int _numberOfParties;
    private final ArrayList<Gate> _gates;
    private final ArrayList<ArrayList<Integer>> _inputWires;
    private final ArrayList<ArrayList<Integer>> _outputWires;
    
    public ScapiCircuitBuilder(int numberOfParties) {
        _numberOfParties = numberOfParties;
        _gates = new ArrayList<>();
        _inputWires = new ArrayList<>(numberOfParties);
        _outputWires = new ArrayList<>(numberOfParties);
        
        for(int i = 0; i < numberOfParties; ++i) {
            _inputWires.add(new ArrayList<Integer>());
            _outputWires.add(new ArrayList<Integer>());
        }
    }
    
    @Override
    public void addANDGate(int id, Wire firstInputWire, Wire secondInputWire, Wire outputWire) {
         int[] inputIds = { firstInputWire.getId(), secondInputWire.getId() };
         int[] outputIds = { outputWire.getId() };
        _gates.add(new ANDGate(id, inputIds, outputIds));
    }

    @Override
    public void addXORGate(int id, Wire firstInputWire, Wire secondInputWire, Wire outputWire) {
        int[] inputIds = { firstInputWire.getId(), secondInputWire.getId() };
        int[] outputIds = { outputWire.getId() };
        _gates.add(new XORGate(id, inputIds, outputIds));
    }

    @Override
    public void addNOTGate(int id, Wire inputWire, Wire outputWire) {
        int[] outputIds = { outputWire.getId() };
        _gates.add(new NOTGate(id, inputWire.getId(), outputIds));
    }

    @Override
    public void makeInputWire(Wire wire, int partyId) {
        if(partyId < 0 || partyId >= _numberOfParties)
            throw new IndexOutOfBoundsException("Invalid party index.");
        
        _inputWires.get(partyId).add(wire.getId());
    }

    @Override
    public void makeOutputWire(Wire wire, int partyId) {
                if(partyId < 0 || partyId >= _numberOfParties)
            throw new IndexOutOfBoundsException("Invalid party index.");
        
        _outputWires.get(partyId).add(wire.getId());
    }
    
    public BooleanCircuit toBooleanCircuit() {
        Gate[] gates = new Gate[_gates.size()];
        for(int i = 0; i < _gates.size(); ++i)
            gates[i] = _gates.get(i);
        
        return new BooleanCircuit(gates, _outputWires, _inputWires);
    }
}
