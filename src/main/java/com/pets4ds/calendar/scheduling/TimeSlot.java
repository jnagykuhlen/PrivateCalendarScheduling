/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
