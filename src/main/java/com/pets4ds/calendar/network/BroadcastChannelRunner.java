/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class BroadcastChannelRunner implements Runnable, Closeable {
    private BroadcastChannel _broadcastChannel;
    
    public BroadcastChannelRunner(BroadcastChannel broadcastChannel) {
        if(broadcastChannel == null)
            throw new IllegalArgumentException("Broadcast channel must not be null.");
        
        _broadcastChannel = broadcastChannel;
    }

    @Override
    public void run() {
        _broadcastChannel.start();
    }

    @Override
    public void close() throws IOException {
        _broadcastChannel.stop();
    }
    
    public static BroadcastChannel startInNewThread(BroadcastChannel broadcastChannel) {
        (new Thread(new BroadcastChannelRunner(broadcastChannel), "BroadcastThread")).start();
        return broadcastChannel;
    }
}
