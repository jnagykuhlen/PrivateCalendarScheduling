/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class InvalidOutputException extends Exception {
    public InvalidOutputException(String message) {
        super(message);
    }
    
    public InvalidOutputException(String message, Throwable inner) {
        super(message, inner);
    }
}
