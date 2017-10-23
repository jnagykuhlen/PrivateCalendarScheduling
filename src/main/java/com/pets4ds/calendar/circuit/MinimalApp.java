/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

import edu.biu.SCProtocols.gmw.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class MinimalApp {
    public static void main(String[] args) {
        /*
        try {
            Files.createDirectories(Paths.get("circuitcache"));
            
            try(PrintWriter writer = new PrintWriter("scapicache/filename.txt")) {
                out.println( text );
            }
        } catch(IOException exception) {
            System.out.println("An exception occurred: " + exception.getMessage());
        }
        */
        
        int partyId = 0;
        if(args.length > 0)
            partyId = Integer.parseInt(args[0]);
        
        runSingleParty(partyId);
        
        System.out.println();
        System.out.println("Press Enter to exit.");
        
        try {
            System.in.read();
        } catch (IOException ex) { }
    }
    
    private static void runSingleParty(int partyId) {
        GmwProtocolInput input = new GmwProtocolInput(partyId, "resources/Circuit.txt", "resources/Parties.txt", "resources/Input" + partyId + ".txt", 2);
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