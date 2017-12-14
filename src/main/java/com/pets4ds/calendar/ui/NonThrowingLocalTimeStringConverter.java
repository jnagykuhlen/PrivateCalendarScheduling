/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.util.converter.LocalTimeStringConverter;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class NonThrowingLocalTimeStringConverter extends LocalTimeStringConverter {
    public NonThrowingLocalTimeStringConverter(DateTimeFormatter formatter) {
        super(formatter, formatter);
    }
    
    @Override
    public LocalTime fromString(String value) {
        try {
            LocalTime result = super.fromString(value);
            if(result != null)
                return result;
        } catch(Exception exception) { }
        
        return LocalTime.MIN;
    }
}
