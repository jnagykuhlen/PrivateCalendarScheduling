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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class BenchmarkingApp {
    private static final String JAR_NAME = "PrivateCalendarScheduling-1.0-SNAPSHOT.jar";
    private static final String CIRCUIT_FILENAME = "Circuit.txt";
    private static final String COMMUNICATION_FILENAME = "Parties.txt";
    private static final String INPUT_FILENAME = "Input{0}.txt";
    private static final String RESULT_FILENAME = "benchmark-results/Results_{0}_{1}.txt";
    private static final int MIN_PORT = 9000;
    private static final int MAX_PORT = 62000;
    private static final int PORT_STEP = 100;
    private static final int SLEEP_TIME = 100;
    
    private static int _currentPort = MIN_PORT;
    
    public static void main(String[] args) {
        try {
            if(args.length == 3) {
                final byte[][] inputs = new byte[][]{
                    { 0, 1, 1, 1, 1 },
                    { 1, 1, 0, 1, 1 },
                    { 1, 1, 0, 1, 0 },
                    { 1, 1, 1, 1, 0 }
                };

                final int[] partyNumbers = parseNumberList(args[0]); // { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20 };
                final int[] slotNumbers = parseNumberList(args[1]); // { 10, 100, 1000 };
                final SchedulingSchemeIdentifier schedulingScheme = SchedulingSchemeIdentifier.valueOf(args[2]); // { SchedulingSchemeIdentifier.FIRST_MATCH, SchedulingSchemeIdentifier.BEST_MATCH };

                final String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                try(TableWriter writer = new TableWriter(MessageFormat.format(RESULT_FILENAME, schedulingScheme.name(), timestamp), slotNumbers.length + 1, 7)) {
                    String[] headerCells = new String[slotNumbers.length + 1];
                    headerCells[0] = "n";
                    for(int i = 0; i < slotNumbers.length; ++i)
                        headerCells[i + 1] = "m = " + slotNumbers[i];

                    writer.writeCells((Object[])headerCells);
                    writer.writeSeparator();
                    writer.flush();

                    for(int numberOfParties : partyNumbers) {
                        String[] cells = new String[slotNumbers.length + 1];
                        cells[0] = Integer.toString(numberOfParties);

                        for(int slotId = 0; slotId < slotNumbers.length; ++slotId) {
                            long result = benchmark(schedulingScheme, numberOfParties, slotNumbers[slotId], inputs);
                            cells[slotId + 1] = Long.toString(result);
                            sleep();
                        }

                        writer.writeCells((Object[])cells);
                        writer.flush();
                    }
                } catch(IOException exception) {
                    System.out.println("Failed to write results file.");
                    exception.printStackTrace();
                }

                Console console = System.console();
                if(console != null) {
                    System.out.println();
                    System.out.println("Press Enter to exit.");
                    console.readLine();
                }
            } else if(args.length == 2) {
                int partyId = Integer.parseInt(args[0]);
                int inputId = Integer.parseInt(args[1]);
                runProtocol(partyId, inputId);
                System.out.println("EXIT");
            } else {
                System.out.println("Invalid number of arguments.");
                System.out.println("Usage: <List of values for n> <List of values for m> <Scheduling scheme>");
            }
        } catch(Exception exception) {
            System.out.println("An error occurred.");
            exception.printStackTrace();
        }
    }
    
    private static long benchmark(SchedulingSchemeIdentifier schedulingScheme, int numberOfParties, int numberOfSlots, byte[][] inputs) {
        long maxProtocolTime = 0;
        
        System.out.println(MessageFormat.format("--- Measuring for n = {0}, m = {1}, {2} ---", numberOfParties, numberOfSlots, schedulingScheme.getFullName()));
        
        System.out.println("Writing communication file...");
        writeCommunicationFile(numberOfParties);
        System.out.println("Communication file written successfully.");
        
        System.out.println("  Writing input files...");
        writeInputFiles(inputs, numberOfSlots);
        System.out.println("  Input files written successfully.");
        
        System.out.println("  Writing circuit file...");
        writeCircuitFile(schedulingScheme, numberOfParties, numberOfSlots);
        System.out.println("  Circuit file written successfully.");
        
        System.out.println("  Starting parties...");
        try {
            PartyWorker[] partyWorkers = new PartyWorker[numberOfParties];
            Thread[] partyThreads = new Thread[numberOfParties];
            
            for(int i = 0; i < numberOfParties; ++i) {
                partyWorkers[i] = new PartyWorker(MessageFormat.format("java -jar \"{0}\" {1} {2}", JAR_NAME, i, i % inputs.length), i);
                partyThreads[i] = new Thread(partyWorkers[i]);
                partyThreads[i].start();
            }
            
            System.out.println("  Parties started successfully.");
            System.out.println("  Waiting for termination...");
            
            for(int i = 0; i < numberOfParties; ++i) {
                partyThreads[i].join();
                if(partyWorkers[i].getException() != null)
                    throw partyWorkers[i].getException();
                
                long protocolTime = partyWorkers[i].getProtocolTime();
                if(protocolTime > maxProtocolTime)
                    maxProtocolTime = protocolTime;
            }
            
            System.out.println("  Parties terminated.");
            System.out.println("  Protocol took " + maxProtocolTime + " ms.");
        } catch(Exception exception) {
            System.out.println("  Failed to run parties.");
            exception.printStackTrace();
        }
        
        System.out.println("--- Measuring finished ---");
        System.out.println();
        
        return maxProtocolTime;
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
            
            for(int partyId = 0; partyId < numberOfParties; ++partyId) {
                writer.write("party_" + partyId + "_port = " + _currentPort + "\n");
                _currentPort += PORT_STEP;
                if(_currentPort > MAX_PORT)
                    _currentPort = MIN_PORT;
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
    
    private static void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch(InterruptedException exception) {
            System.out.println("Cannot sleep.");
            exception.printStackTrace();
        }
    }
    
    private static int[] parseNumberList(String text) {
        text = text.trim();
        if(text.startsWith("{") && text.endsWith("}")) {
            String[] parts = text.substring(1, text.length() - 1).split(",");
            int[] numbers = new int[parts.length];
            for(int i = 0; i < parts.length; ++i)
                numbers[i] = Integer.parseInt(parts[i].trim());
            
            return numbers;
        }
        
        return new int[] { Integer.parseInt(text) };
    }
}
