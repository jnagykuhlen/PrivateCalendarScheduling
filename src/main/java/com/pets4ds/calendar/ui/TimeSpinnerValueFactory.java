/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.SpinnerValueFactory;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TimeSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {
    private final static int MINUTES_INTERVAL = 30;
    
    public TimeSpinnerValueFactory(LocalTime value) {
        setConverter(new NonThrowingLocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm")));
        setValue(value);
    }
    
    @Override
    public void decrement(int steps) {
        increment(-steps);
    }

    @Override
    public void increment(int steps) {
        LocalTime value = getValue();
        if (value != null) {
            value = LocalTime.of(value.getHour(), (value.getMinute() / MINUTES_INTERVAL) * MINUTES_INTERVAL);
            setValue(value.plusMinutes(steps * MINUTES_INTERVAL));
        }
    }
    
}
