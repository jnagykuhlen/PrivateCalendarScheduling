/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.benchmarking;

import com.pets4ds.calendar.mpc.scapi.ScapiCircuitBuilder;
import com.pets4ds.calendar.scheduling.SchedulingSchemeIdentifier;
import edu.biu.SCProtocols.gmw.GmwParty;
import edu.biu.SCProtocols.gmw.GmwProtocolInput;
import edu.biu.SCProtocols.gmw.GmwProtocolOutput;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class BenchmarkingApp {
    private static final String JAR_NAME = "PrivateCalendarScheduling-1.0-SNAPSHOT.jar";
    private static final String CIRCUIT_FILENAME = "Circuit.txt";
    private static final String COMMUNICATION_FILENAME = "Parties.txt";
    private static final String INPUT_FILENAME = "Input{0}.txt";
    
    public static void main(String[] args) {
        final boolean isCoordinator = args.length == 0;
        
        if(isCoordinator) {
            final byte[][] inputs = new byte[][]{
                { 0, 1, 1, 1, 1 },
                { 1, 1, 0, 1, 1 },
                { 1, 1, 0, 1, 0 },
                { 1, 1, 1, 1, 0 }
            };
        
            final int[] slotNumbers = { 10, 100 }; //{ 10, 100, 1000 };
            final int[] partyNumbers = { 2, 4 }; //{ 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 };
            final int maxNumberOfParties = 20;
            
            System.out.println("Writing communication file...");
            writeCommunicationFile(maxNumberOfParties);
            System.out.println("Communication file written successfully.");
            
            System.out.println();
            
            for(SchedulingSchemeIdentifier schedulingScheme : SchedulingSchemeIdentifier.values()) {
                for(int numberOfParties : partyNumbers) {
                    for(int numberOfSlots : slotNumbers) {
                        benchmark(schedulingScheme, numberOfParties, numberOfSlots, inputs);
                    }
                }
            }
            
            Console console = System.console();
            if(console != null) {
                System.out.println();
                System.out.println("Press Enter to exit.");
                console.readLine();
            }
        } else {
            if(args.length == 2) {
                int partyId = Integer.parseInt(args[0]);
                int inputId = Integer.parseInt(args[1]);
                runProtocol(partyId, inputId);
            }
        }
    }
    
    private static void benchmark(SchedulingSchemeIdentifier schedulingScheme, int numberOfParties, int numberOfSlots, byte[][] inputs) {
        System.out.println(MessageFormat.format("--- Measuring for n = {0}, m = {1}, {2} ---", numberOfParties, numberOfSlots, schedulingScheme.getFullName()));
        
        System.out.println("  Writing input files...");
        writeInputFiles(inputs, numberOfSlots);
        System.out.println("  Input files written successfully.");
        
        System.out.println("  Writing circuit file...");
        writeCircuitFile(schedulingScheme, numberOfParties, numberOfSlots);
        System.out.println("  Circuit file written successfully.");
        
        System.out.println("  Starting parties...");
        try {
            long startTime = System.nanoTime();
            
            Process[] partyProcesses = new Process[numberOfParties];
            for(int i = 0; i < numberOfParties; ++i)
                partyProcesses[i] = Runtime.getRuntime().exec(MessageFormat.format("java -jar \"{0}\" {1} {2}", JAR_NAME, i, i % inputs.length));
            
            System.out.println("  Parties started successfully.");
            System.out.println("  Waiting for termination...");
            
            for(int i = 0; i < numberOfParties; ++i)
                partyProcesses[i].waitFor();
        
            long endTime = System.nanoTime();
            long totalTime =(endTime - startTime) / 1000000;
            
            System.out.println("  Parties terminated. Execution took " + totalTime + " ms.");
        } catch(Exception exception) {
            System.out.println("  Failed to run parties.");
            exception.printStackTrace();
        }
        
        System.out.println("--- Measuring finished ---");
        System.out.println();
    }
    
    private static void writeCircuitFile(SchedulingSchemeIdentifier schedulingScheme, int numberOfParties, int numberOfSlots) {
        ScapiCircuitBuilder builder = new ScapiCircuitBuilder(numberOfParties);
        schedulingScheme.getSchedulingScheme().createCircuitGenerator(numberOfParties, numberOfSlots).generate(builder);
            
        try {
            builder.writeCircuitFile(CIRCUIT_FILENAME);
            System.out.println("Circuit generation successful.");
        } catch(IOException exception) {
            System.out.println("Cannot write circuit file.");
            exception.printStackTrace();
        }
    }
    
    private static void writeInputFiles(byte[][] partyInputs, int numberOfSlots) {
        System.out.println("Generating input files...");
        
        for(int inputId = 0; inputId < partyInputs.length; ++inputId) {
            try(FileWriter writer = new FileWriter(getInputFilename(inputId))) {
                for (int slotId = 0; slotId < numberOfSlots; ++slotId) {
                    writer.write(Byte.toString(partyInputs[inputId][slotId % partyInputs[inputId].length]) + "\n");
                }
                
                System.out.println("Input file generation successful.");
            } catch(IOException exception) {
                System.out.println("Cannot write input files.");
                exception.printStackTrace();
            }
        }
    }
    
    private static void writeCommunicationFile(int numberOfParties) {
        System.out.println("Generating communication file...");
        
        try(FileWriter writer = new FileWriter(COMMUNICATION_FILENAME)) {
            for(int partyId = 0; partyId < numberOfParties; ++partyId) {
                writer.write("party_" + partyId + "_ip = 127.0.0.1\n");
            }
            
            final int startPort = 8000;
            final int portsPerParty = 100;
            
            for(int partyId = 0; partyId < numberOfParties; ++partyId) {
                int port = startPort + partyId * portsPerParty;
                writer.write("party_" + partyId + "_port = " + port + "\n");
            }
            
            System.out.println("Communication file generation successful.");
        } catch(IOException exception) {
            System.out.println("Cannot write communication file.");
            exception.printStackTrace();
        }
    }
    
    private static byte[] runProtocol(int partyId, int inputId) {
        GmwProtocolInput input = new GmwProtocolInput(partyId, CIRCUIT_FILENAME, COMMUNICATION_FILENAME, getInputFilename(inputId), 1);
        GmwParty party = new GmwParty();
        
        party.start(input);
        party.run();
        
        GmwProtocolOutput output = (GmwProtocolOutput)party.getOutput();
        return output.getOutput();
    }
    
    private static String getInputFilename(int inputId) {
        return MessageFormat.format(INPUT_FILENAME, inputId);
    }
}
