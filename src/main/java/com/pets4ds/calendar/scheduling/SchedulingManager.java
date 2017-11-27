/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import com.pets4ds.calendar.network.BroadcastChannel;
import com.pets4ds.calendar.network.BroadcastChannelRunner;
import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSetup;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingManager implements Closeable {
    private final CommunicationSetup _communicationSetup;
    private final BroadcastChannel _broadcastChannel;
    private String _localPartyName;
    private int _nextSetupPort;
    
    public SchedulingManager() {
        _communicationSetup = null;
        _broadcastChannel = null;
        _localPartyName = "User" + (new Random()).nextInt(1000000);
        
    }
    
    public SchedulingManager(CommunicationSetup communicationSetup, BroadcastChannel broadcastChannel, int firstSetupPort) {
        _communicationSetup = communicationSetup;
        _broadcastChannel = BroadcastChannelRunner.startInNewThread(broadcastChannel);
        _nextSetupPort = firstSetupPort;
    }
    
    @Override
    public void close() throws IOException {
        // _broadcastChannel.stop();
        // _communicationSetup.close();
    }
    
    public void createSchedulingSession(String name, String descriptionText) throws IOException {
        InetSocketAddress localAddress = new InetSocketAddress(_nextSetupPort);
        try {
            localAddress = new InetSocketAddress(InetAddress.getLocalHost(), _nextSetupPort);
        } catch(UnknownHostException exception) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    "Unable to query local IP address.",
                    exception
                );
        }
        
        _nextSetupPort++;
        
        CommunicationSession session = new CommunicationSession(
            name,
            descriptionText,
            localAddress,
            null // TODO: Send information on scheduling scheme
        );
        
        _communicationSetup.createSession(session);
        _broadcastChannel.publish(session);
    }
    
    public void joinSchedulingSession(CommunicationSession session) throws IOException {
        _communicationSetup.joinSession(session);
    }
    
    public String getLocalPartyName() {
        return _localPartyName;
    }
    
    public void setLocalPartyName(String localPartyName) {
        _localPartyName = localPartyName;
    }
}
