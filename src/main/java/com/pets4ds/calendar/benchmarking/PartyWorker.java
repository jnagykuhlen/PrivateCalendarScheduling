/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.benchmarking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class PartyWorker implements Runnable {
    private final String _command;
    private Exception _exception;
    private long _setupTime;
    private long _protocolTime;
    
    public PartyWorker(String command) {
        _command = command;
        _exception = null;
        _setupTime = 0;
        _protocolTime = 0;
    }
    
    @Override
    public void run() {
        try {
            long startTime = System.nanoTime();
            
            Process process = Runtime.getRuntime().exec(_command);
            
            /*
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // System.out.println("  >>" + line);
                if(line.equals("----------end communication--------------")) {
                    long currentTime = System.nanoTime();
                    _setupTime = (currentTime - startTime) / 1000000;
                    startTime = currentTime;
                }
            }
            */
            
            process.waitFor();
            
            long endTime = System.nanoTime();
            _protocolTime = (endTime - startTime) / 1000000;
        } catch(Exception exception) {
            _exception = exception;
        }
    }
    
    public Exception getException() {
        return _exception;
    }
    
    public long getSetupTime() {
        return _setupTime;
    }
    
    public long getProtocolTime() {
        return _protocolTime;
    }
}
