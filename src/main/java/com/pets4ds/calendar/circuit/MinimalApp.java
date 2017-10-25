/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

import edu.biu.SCProtocols.gmw.*;
import edu.biu.scapi.circuits.circuit.BooleanCircuit;
import edu.biu.scapi.circuits.circuit.Wire;
import java.io.Console;
import java.io.*;
import java.util.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class MinimalApp {
    private static final String JAR_NAME = "PrivateCalendarScheduling-1.0-SNAPSHOT.jar";
    
    public static void main(String[] args) {
        boolean isCoordinator = args.length == 0;
        
        final int numberOfParties = 3;
        final int numberOfSlots = 5;
        
        byte[][] partyInputs = new byte[][]{
            { 0, 1, 1, 1, 1 },
            { 1, 1, 0, 1, 1 },
            { 1, 1, 0, 1, 0 },
            { 1, 1, 1, 1, 0 }
        };
        
        if(isCoordinator) {
            System.out.println("This party is the coordinator.");
            System.out.println("Parameters for circuit generation:");
            System.out.println("  n = " + numberOfParties);
            System.out.println("  m = " + numberOfSlots);
            
            ScapiCircuitBuilder builder = new ScapiCircuitBuilder(numberOfParties);
            (new FirstMatchSchedulingCircuitGenerator(numberOfParties, numberOfSlots)).generate(builder);
            builder.toBooleanCircuit().write("GeneratedCircuit.txt");
            
            System.out.println("Circuit generation successful.");
            
            writeInputFiles(partyInputs, numberOfParties, numberOfSlots);
            writeCommunicationFile(numberOfParties);
            writeRunScript(numberOfParties);
            
            System.out.println("Computing reference output...");
            
            try {
                BooleanCircuit circuit = new BooleanCircuit(new File("GeneratedCircuit.txt"));
                
                for(int partyId = 0; partyId < numberOfParties; ++partyId) {
                    Map<Integer, Wire> inputWires = new HashMap<>(numberOfSlots);
                    
                    int slotId = 0;
                    for(Integer wireId : circuit.getInputWireIndices(partyId + 1)) {
                        int boundedPartyId = partyId % partyInputs.length;
                        int boundedSlotId = slotId % partyInputs[boundedPartyId].length;
                        
                        inputWires.put(wireId, new Wire(partyInputs[boundedPartyId][boundedSlotId]));
                        slotId++;
                    }
                    
                    circuit.setInputs(inputWires, partyId + 1);
                }
                
                System.out.println("Inputs set.");
                
                Map<Integer, Wire> outputs = circuit.compute();
                
                System.out.print("Reference output: ");
                for(Integer wireId : circuit.getOutputWireIndices(1)) {
                    System.out.print(outputs.get(wireId).getValue());
                }
                System.out.println();
            } catch(Exception ex) {
                System.out.println("Cannot load inputs.");
                ex.printStackTrace();
            }
            
            System.out.println("Starting other parties...");
            
            try {
                for(int i = 1; i < numberOfParties; ++i) {
                    Runtime.getRuntime().exec("java -jar \"" + JAR_NAME + "\" " + i);
                }
                
                System.out.println("Other parties successfully started.");
            } catch(IOException ex) {
                System.out.println("Failed to start parties.");
                ex.printStackTrace();
            }
            
            System.out.println();
        }
        
        int partyId = 0;
        if(args.length > 0)
            partyId = Integer.parseInt(args[0]);
        
        long startTime = System.nanoTime();
        
        runSingleParty(partyId);
        
        long endTime = System.nanoTime();
        long totalTime =(endTime - startTime) / 1000000;
        
        System.out.println("Protocol execution took " + totalTime + " ms.");
        
        Console console = System.console();
        if(console != null) {
            System.out.println();
            System.out.println("Press Enter to exit.");
            console.readLine();
        }
    }
    
    private static void writeInputFiles(byte[][] partyInputs, int numberOfParties, int numberOfSlots) {
        System.out.println("Generating input files...");
        
        for(int partyId = 0; partyId < numberOfParties; ++partyId) {
            try(FileWriter writer = new FileWriter("Input" + partyId + ".txt")) {
                for (int slotId = 0; slotId < numberOfSlots; ++slotId) {
                    int boundedPartyId = partyId % partyInputs.length;
                    int boundedSlotId = slotId % partyInputs[boundedPartyId].length;
                    writer.write(Byte.toString(partyInputs[boundedPartyId][boundedSlotId]) + "\n");
                }
                
                System.out.println("Input file generation successful.");
            } catch(IOException ex) {
                System.out.println("Cannot write input files.");
                ex.printStackTrace();
            }
        }
    }
    
    private static void writeCommunicationFile(int numberOfParties) {
        System.out.println("Generating communication file...");
        
        try(FileWriter writer = new FileWriter("Parties.txt")) {
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
        } catch(IOException ex) {
            System.out.println("Cannot write communication file.");
            ex.printStackTrace();
        }
    }
    
    private static void writeRunScript(int numberOfParties) {
        // "java -jar \"" + JAR_NAME + "\" " + i
        
        System.out.println("Generating run script...");
        
        try(FileWriter writer = new FileWriter("RunProtocol.bat")) {
            for(int partyId = 0; partyId < numberOfParties; ++partyId) {
                writer.write("start \"Party " + partyId + "\" java -jar \"\"" + JAR_NAME + "\"\" " + partyId);
                if(partyId < numberOfParties - 1)
                    writer.write(" &\n");
            }
            
            System.out.println("Run script generation successful.");
        } catch(IOException ex) {
            System.out.println("Cannot write run script.");
            ex.printStackTrace();
        }
    }
    
    private static void runSingleParty(int partyId) {
        GmwProtocolInput input = new GmwProtocolInput(partyId, "GeneratedCircuit.txt", "Parties.txt", "Input" + partyId + ".txt", 1);
        GmwParty party = new GmwParty();
        
        System.out.println("--- Log for party " + partyId + " ---");
        System.out.println("Party created.");
        System.out.println("Computing...");
        
        party.start(input);
        party.run();
        
        GmwProtocolOutput output = (GmwProtocolOutput)party.getOutput();
        System.out.println("Output: " + Arrays.toString(output.getOutput()));
    }
    
    private static void runPartiesMultithreaded(int numberOfParties) {
        final GmwParty[] parties = new GmwParty[numberOfParties];
        for(int i = 0; i < numberOfParties; ++i) {
            parties[i] = new GmwParty();
        }
        
        System.out.println("Parties created.");
        
        Thread[] threads = new Thread[numberOfParties];
        for(int i = 0; i < numberOfParties; ++i) {
            final int partyId = i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    GmwProtocolInput input = new GmwProtocolInput(partyId, "Circuit.txt", "Parties.txt", "Input" + partyId + ".txt", 1);
                    parties[partyId].start(input);
                    parties[partyId].run();
                }
            };
            
            threads[i].start();
        }
        
        System.out.println("Computing...");
        
        for(int i = 0; i < numberOfParties; ++i) {
            try {
                threads[i].join();
                
                GmwProtocolOutput output = (GmwProtocolOutput)parties[i].getOutput();
                byte[] binaryOutput = output.getOutput();
                System.out.println("Output of party " + i + ": " + Arrays.toString(binaryOutput));
            } catch (InterruptedException ex) {
                System.out.println("Thread was interrupted.");
                ex.printStackTrace();
            }
        }
        
        System.out.println("Parties created.");
        System.out.println("Exiting...");
    }
}
