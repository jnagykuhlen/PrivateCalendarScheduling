/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.mpc.scapi;

import com.pets4ds.calendar.circuit.CircuitBuilder;
import com.pets4ds.calendar.circuit.Wire;
import edu.biu.scapi.circuits.circuit.ANDGate;
import edu.biu.scapi.circuits.circuit.BooleanCircuit;
import edu.biu.scapi.circuits.circuit.Gate;
import edu.biu.scapi.circuits.circuit.NOTGate;
import edu.biu.scapi.circuits.circuit.XORGate;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public final class ScapiCircuitBuilder extends CircuitBuilder {
    private final int _numberOfParties;
    private final ArrayList<CircuitGate> _gates;
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
        _gates.add(new CircuitGate(GateType.AND, id, inputIds, outputWire.getId()));
    }

    @Override
    public void addXORGate(int id, Wire firstInputWire, Wire secondInputWire, Wire outputWire) {
        int[] inputIds = { firstInputWire.getId(), secondInputWire.getId() };
        _gates.add(new CircuitGate(GateType.XOR, id, inputIds, outputWire.getId()));
    }

    @Override
    public void addNOTGate(int id, Wire inputWire, Wire outputWire) {
        int[] inputIds = { inputWire.getId() };
        _gates.add(new CircuitGate(GateType.NOT, id, inputIds, outputWire.getId()));
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
        for(int i = 0; i < _gates.size(); ++i) {
            CircuitGate sourceGate = _gates.get(i);
            int gateId = sourceGate.getGateId();
            int[] inputIds = sourceGate.getInputIds();
            int[] outputIds = { sourceGate.getOutputId() };
            
            switch(sourceGate.getType()) {
                case AND:
                    gates[i] = new ANDGate(gateId, inputIds, outputIds);
                    break;
                case XOR:
                    gates[i] = new XORGate(gateId, inputIds, outputIds);
                    break;
                case NOT:
                    gates[i] = new NOTGate(gateId, inputIds[0], outputIds);
                    break;
            }
        }
        
        return new BooleanCircuit(gates, _outputWires, _inputWires);
    }
    
    public void writeCircuitFile(String filePath) throws IOException {
        try(PrintWriter writer = new PrintWriter(filePath, "UTF-8")) {
            writer.println(_gates.size());
            writer.println(_numberOfParties);
            writer.println();

            for (int i = 0; i < _numberOfParties; ++i) {
                int numberOfInputs = _inputWires.get(i).size();
                
                writer.println((i + 1) + " " + numberOfInputs);
                for (int j = 0; j < numberOfInputs; ++j)
                    writer.println(_inputWires.get(i).get(j));
                
                writer.println();
            }
            
            for (int i = 0; i < _numberOfParties; ++i) {
                int numberOfOutputs = _outputWires.get(i).size();
                
                writer.println((i + 1) + " " + numberOfOutputs);
                for (int j = 0; j < numberOfOutputs; j++)
                    writer.println(_outputWires.get(i).get(j));
                
                writer.println();
            }
            
            for (CircuitGate gate : _gates) {
                writer.print(gate.getInputIds().length + " 1 ");
                
                for (int j = 0; j < gate.getInputIds().length; ++j)
                    writer.print(gate.getInputIds()[j] + " ");
                
                writer.println(gate.getOutputId() + " " + gate.getType().getTruthTable());
            }
        }
    }
}
