/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.circuit;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class Wire {
    public final static Wire ZERO = new Wire(-1);
    public final static Wire ONE = new Wire(-2);
    
    private final int _id;

    public Wire(int id) {
        _id = id;
    }
    
    public int getId() {
        return _id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + _id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Wire other = (Wire) obj;
        if (this._id != other._id) {
            return false;
        }
        return true;
    }
}
