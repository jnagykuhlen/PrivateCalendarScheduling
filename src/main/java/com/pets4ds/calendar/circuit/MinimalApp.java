/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

import edu.biu.SCProtocols.gmw.*;
import com.pets4ds.calendar.circuit.*;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class MinimalApp {
    public static void main(String[] args) {
        boolean isCoordinator = args.length == 0;
        
        final int numberOfParties = 3;
        final int numberOfSlots = 5;
        
        if(isCoordinator) {
            System.out.println("This party is the coordinator.");
            System.out.println("Parameters for circuit generation:");
            System.out.println("  n = " + numberOfParties);
            System.out.println("  m = " + numberOfSlots);
            
            ScapiCircuitBuilder builder = new ScapiCircuitBuilder(numberOfParties);
            (new FirstMatchSchedulingCircuitGenerator(numberOfParties, numberOfSlots)).generate(builder);
            builder.toBooleanCircuit().write("GeneratedCircuit.txt");
            
            System.out.println("Circuit generation successful.");
            System.out.println("Starting other parties...");
            
            try {
                for(int i = 1; i < numberOfParties; ++i) {
                    Runtime.getRuntime().exec("java -jar \"PrivateCalendarScheduling-1.0-SNAPSHOT.jar\" " + i);
                }
                
                System.out.println("Other parties successfully started.");
            } catch(IOException ex) {
                System.out.println("Failed to start parties.");
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
    }
    
    private static void runSingleParty(int partyId) {
        GmwProtocolInput input = new GmwProtocolInput(partyId, "GeneratedCircuit.txt", "resources/Parties.txt", "resources/Input" + (partyId % 3) + ".txt", 99);
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
                    GmwProtocolInput input = new GmwProtocolInput(partyId, "resources/Circuit.txt", "resources/Parties.txt", "resources/Input" + partyId + ".txt", 2);
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
            }
        }
        
        System.out.println("Parties created.");
        System.out.println("Exiting...");
    }
}
