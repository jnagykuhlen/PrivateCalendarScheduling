/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.scheduling;

import biweekly.ICalendar;
import biweekly.component.VEvent;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public final class CalendarCompatibility {
    public static void writeEventToFile(File file, SchedulingSession session, TimeSlot timeSlot) throws IOException {
        Date currentDate = Date.from(Instant.now());
        
        VEvent event = new VEvent();
        event.setSummary(session.getName());
        event.setDescription(session.getDescriptionText());
        event.setCreated(currentDate);
        event.setLastModified(currentDate);
        event.setDateStart(Date.from(timeSlot.getStartDate().atZone(ZoneId.systemDefault()).toInstant()));
        event.setDateEnd(Date.from(timeSlot.getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
        event.setUid(session.getUUID().toString());
        
        ICalendar calendar = new ICalendar();
        calendar.addEvent(event);
        calendar.write(file);
    }
}
