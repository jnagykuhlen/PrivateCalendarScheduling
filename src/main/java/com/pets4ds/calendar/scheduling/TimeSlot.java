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
package com.pets4ds.calendar.scheduling;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TimeSlot implements Serializable {
    private static final long serialVersionUID = -6959102698676055550L;
    
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private LocalDateTime _start;
    private LocalDateTime _end;
    
    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        if(start == null || end == null)
            throw new IllegalArgumentException("Start and end time must not be null.");
        
        if(start.isAfter(end))
            throw new IllegalArgumentException("End time must be after start time.");
        
        _start = start;
        _end = end;
    }
    
    public boolean overlaps(TimeSlot other) {
        return getStartDate().isBefore(other.getEndDate()) && other.getStartDate().isBefore(getEndDate());
    }
    
    public LocalDateTime getStartDate() {
        return _start;
    }
    
    public LocalDateTime getEndDate() {
        return _end;
    }
    
    @Override
    public String toString() {
        String result = MessageFormat.format(
            "{0} from {1} to {2}",
            _start.format(DATE_FORMATTER),
            _start.format(TIME_FORMATTER),
            _end.format(TIME_FORMATTER)
        );

        long additionalDays = ChronoUnit.DAYS.between(_start.toLocalDate(), _end.toLocalDate());
        if(additionalDays > 0)
            result += MessageFormat.format(" (+{0})", additionalDays);

        return result;
    }
}
