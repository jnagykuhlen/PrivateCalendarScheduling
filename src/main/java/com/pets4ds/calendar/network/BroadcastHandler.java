/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.network;

import java.io.Serializable;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public interface BroadcastHandler {
    void broadcastReceived(Serializable serializable);
    void broadcastError(NetworkException exception);
}
