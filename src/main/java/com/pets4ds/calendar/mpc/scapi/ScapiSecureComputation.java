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
package com.pets4ds.calendar.mpc.scapi;

import com.pets4ds.calendar.circuit.CircuitGenerator;
import com.pets4ds.calendar.mpc.SecureComputation;
import com.pets4ds.calendar.mpc.SecureComputationException;
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
        writeCircuitFile(circuitGenerator);
        writeCommunicationFile();
        writeInputFile(input);
        
        GmwProtocolInput protocolInput = new GmwProtocolInput(
            partyId,
            getCircuitFilePath(),
            getCommunicationFilePath(),
            getInputFilePath(),
            1
        );
        
        System.out.println("Created protocol input.");
        GmwParty party = new GmwParty();
        System.out.println("Created GMW party.");
        party.start(protocolInput);
        System.out.println("Created GMW party.");
        party.run();
        System.out.println("Run GMW party.");
        
        GmwProtocolOutput output = (GmwProtocolOutput)party.getOutput();
        
        System.out.println("Extracted protocol output.");
        
        return output.getOutput();
    }
    
    private void createCacheDirectory() throws SecureComputationException {
        try {
            Files.createDirectories(Paths.get(_cacheDirectory));
        } catch(IOException exception) {
            throw new SecureComputationException("Unable to create cache directory.", exception);
        }
    }
    
    private void writeCommunicationFile() throws SecureComputationException {
        try(FileWriter writer = new FileWriter(getCommunicationFilePath())) {
            for(int partyId = 0; partyId < _partyAddresses.length; ++partyId)
                writer.write("party_" + partyId + "_ip = " + _partyAddresses[partyId].getAddress().getHostAddress() + "\n");
            
            for(int partyId = 0; partyId < _partyAddresses.length; ++partyId)
                writer.write("party_" + partyId + "_port = " + _partyAddresses[partyId].getPort() + "\n");
        } catch(IOException exception) {
            throw new SecureComputationException("Unable to write communication file.", exception);
        }
    }
    
    private void writeInputFile(byte[] input) throws SecureComputationException {
        try(FileWriter writer = new FileWriter(getInputFilePath())) {
            for (int i = 0; i < input.length; ++i)
                writer.write(Byte.toString(input[i]) + "\n");
        } catch(IOException exception) {
            throw new SecureComputationException("Unable to write input file.", exception);
        }
    }
    
    private void writeCircuitFile(CircuitGenerator circuitGenerator) throws SecureComputationException {
        try {
            ScapiCircuitBuilder builder = new ScapiCircuitBuilder(circuitGenerator.getNumberOfParties());
            circuitGenerator.generate(builder);
            builder.writeCircuitFile(getCircuitFilePath());
        } catch(IOException exception) {
            throw new SecureComputationException("Unable to write circuit file.", exception);
        }
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
