/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network.socket;

import java.net.Socket;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class ClientConnection {
    private final Socket _socket;
    
    public ClientConnection(Socket socket) {
        _socket = socket;
    }
    
    public Socket getSocket() {
        return _socket;
    }
}
