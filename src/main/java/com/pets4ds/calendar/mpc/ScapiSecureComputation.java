/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.mpc;

import com.pets4ds.calendar.circuit.CircuitGenerator;
import com.pets4ds.calendar.circuit.ScapiCircuitBuilder;
import edu.biu.SCProtocols.gmw.GmwParty;
import edu.biu.SCProtocols.gmw.GmwProtocolInput;
import edu.biu.SCProtocols.gmw.GmwProtocolOutput;
import java.net.*;
import java.io.*;
import java.nio.file.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ScapiSecureComputation implements SecureComputation {
    private static final String DEFAULT_CACHE_DIRECTORY = "scapi-cache/";
    private static final String COMMUNICATION_FILE_NAME = "Parties.txt";
    private static final String INPUT_FILE_NAME = "Input.txt";
    private static final String CIRCUIT_FILE_NAME = "Circuit.txt";
    
    private InetSocketAddress[] _partyAddresses;
    private String _cacheDirectory;
    
    public ScapiSecureComputation(InetSocketAddress[] partyAddresses) {
        this(partyAddresses, DEFAULT_CACHE_DIRECTORY);
    }
    
    public ScapiSecureComputation(InetSocketAddress[] partyAddresses, String cacheDirectory) {
        _partyAddresses = partyAddresses;
        _cacheDirectory = cacheDirectory;
    }
    
    @Override
    public byte[] evaluate(CircuitGenerator circuitGenerator, int partyId, byte[] input) throws SecureComputationException {
        if(circuitGenerator.getNumberOfParties() != _partyAddresses.length)
            throw new IllegalArgumentException("Number of parties in the circuit does not match the number of specified addresses.");
        
        for (int i = 0; i < input.length; ++i) {
            if(input[i] > 1)
                throw new IllegalArgumentException("Input values must be either 0 or 1.");
        }
        
        createCacheDirectory();
        writeCommunicationFile();
        writeInputFile(input);
        writeCircuitFile(circuitGenerator);
        
        GmwProtocolInput protocolInput = new GmwProtocolInput(
            partyId,
            getCircuitFilePath(),
            getCommunicationFilePath(),
            getInputFilePath(),
            1
        );
        
        GmwParty party = new GmwParty();
        party.start(protocolInput);
        party.run();
        
        GmwProtocolOutput output = (GmwProtocolOutput)party.getOutput();
        return output.getOutput();
    }
    
    private void createCacheDirectory() throws SecureComputationException {
        try {
            Files.createDirectories(Paths.get(_cacheDirectory));
        } catch(IOException ex) {
            throw new SecureComputationException("Unable to create cache directory.", ex);
        }
    }
    
    private void writeCommunicationFile() throws SecureComputationException {
        try(FileWriter writer = new FileWriter(getCommunicationFilePath())) {
            for(int partyId = 0; partyId < _partyAddresses.length; ++partyId)
                writer.write("party_" + partyId + "_ip = " + _partyAddresses[partyId].getAddress().getHostAddress() + "\n");
            
            for(int partyId = 0; partyId < _partyAddresses.length; ++partyId)
                writer.write("party_" + partyId + "_port = " + _partyAddresses[partyId].getPort() + "\n");
        } catch(IOException ex) {
            throw new SecureComputationException("Unable to write communication file.", ex);
        }
    }
    
    private void writeInputFile(byte[] input) throws SecureComputationException {
        try(FileWriter writer = new FileWriter(getInputFilePath())) {
            for (int i = 0; i < input.length; ++i)
                writer.write(Byte.toString(input[i]) + "\n");
        } catch(IOException ex) {
            throw new SecureComputationException("Unable to write input file.", ex);
        }
    }
    
    private void writeCircuitFile(CircuitGenerator circuitGenerator) throws SecureComputationException {
        ScapiCircuitBuilder builder = new ScapiCircuitBuilder(circuitGenerator.getNumberOfParties());
        circuitGenerator.generate(builder);
        builder.toBooleanCircuit().write(getCircuitFilePath());
    }
    
    private String getCommunicationFilePath() {
        return _cacheDirectory + COMMUNICATION_FILE_NAME;
    }
    
    private String getInputFilePath() {
        return _cacheDirectory + INPUT_FILE_NAME;
    }
    
    private String getCircuitFilePath() {
        return _cacheDirectory + CIRCUIT_FILE_NAME;
    }
}
