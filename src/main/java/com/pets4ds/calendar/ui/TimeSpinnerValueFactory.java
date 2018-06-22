/*
 * MIT License
 *
 * Copyright (c) 2018 Jonas Nagy-Kuhlen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
