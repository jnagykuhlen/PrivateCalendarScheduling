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
package com.pets4ds.calendar.benchmarking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.MessageFormat;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class PartyWorker implements Runnable {
    private final String _command;
    private final int _id;
    private Exception _exception;
    private long _setupTime;
    private long _protocolTime;
    
    public PartyWorker(String command, int id) {
        _command = command;
        _id = id;
        _exception = null;
        _setupTime = 0;
        _protocolTime = 0;
    }
    
    @Override
    public void run() {
        try {
            try(PrintWriter writer = new PrintWriter(MessageFormat.format("log-{0}.txt", _id))) {
                long startTime = System.nanoTime();
                Process process = Runtime.getRuntime().exec(_command);
            
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                    /*if(line.equals("----------end communication--------------")) {
                        long currentTime = System.nanoTime();
                        _setupTime = (currentTime - startTime) / 1000000;
                        startTime = currentTime;
                    }*/
                }
            
                process.waitFor();

                long endTime = System.nanoTime();
                _protocolTime = (endTime - startTime) / 1000000;
            }
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
