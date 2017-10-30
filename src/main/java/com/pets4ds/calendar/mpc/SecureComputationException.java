/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.mpc;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SecureComputationException extends Exception {
    public SecureComputationException(String message) {
        super(message);
    }
    
    public SecureComputationException(String message, Throwable inner) {
        super(message, inner);
    }
}
