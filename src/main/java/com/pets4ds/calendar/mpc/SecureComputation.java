/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.mpc;

import com.pets4ds.calendar.circuit.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public interface SecureComputation {
    byte[] evaluate(CircuitGenerator circuitGenerator, int partyId, byte[] input) throws SecureComputationException;
}
