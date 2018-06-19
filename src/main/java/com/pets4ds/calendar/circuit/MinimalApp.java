/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

import edu.biu.SCProtocols.gmw.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class MinimalApp {
    public static void main(String[] args) {
        GmwParty[] parties = new GmwParty[3];
        for(int i = 0; i < parties.length; ++i)
            parties[i] = new GmwParty();
        
        System.out.println("Parties created.");
        System.out.println("Exiting...");
    }
}
