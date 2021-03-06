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

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    public static TimeSlotAllocation[] matchCalendar(File file, TimeSlot[] timeSlots) throws IOException {
        TimeSlotAllocation[] result = new TimeSlotAllocation[timeSlots.length];
        for(int i = 0; i < result.length; ++i)
            result[i] = TimeSlotAllocation.FREE;
        
        Map<LocalDate, List<IndexedTimeSlot>> timeSlotsByDay = new HashMap<>();
        
        for(int i = 0; i < timeSlots.length; ++i) {
            LocalDate startDay = timeSlots[i].getStartDate().toLocalDate();
            LocalDate endDay = timeSlots[i].getEndDate().toLocalDate();
            
            for(LocalDate day = startDay; !day.isAfter(endDay); day = day.plusDays(1)) {
                List<IndexedTimeSlot> daysTimeSlots = timeSlotsByDay.get(day);
                if(daysTimeSlots == null) {
                    daysTimeSlots = new ArrayList<>(1);
                    timeSlotsByDay.put(day, daysTimeSlots);
                }
                
                daysTimeSlots.add(new IndexedTimeSlot(timeSlots[i], i));
            }
        }
        
        ICalendar calendar = Biweekly.parse(file).first();
        for(VEvent event : calendar.getEvents()) {
            try {
                TimeSlot eventSlot = new TimeSlot(
                    LocalDateTime.ofInstant(event.getDateStart().getValue().toInstant(), ZoneId.systemDefault()),
                    LocalDateTime.ofInstant(event.getDateEnd().getValue().toInstant(), ZoneId.systemDefault())
                );
                
                LocalDate eventStartDay = eventSlot.getStartDate().toLocalDate();
                LocalDate eventEndDay = eventSlot.getEndDate().toLocalDate();
                
               for(LocalDate day = eventStartDay; !day.isAfter(eventEndDay); day = day.plusDays(1)) {
                   List<IndexedTimeSlot> daysTimeSlots = timeSlotsByDay.get(day);
                   if(daysTimeSlots != null) {
                       for(IndexedTimeSlot timeSlot : daysTimeSlots) {
                           if(timeSlot.getTimeSlot().overlaps(eventSlot))
                               result[timeSlot.getIndex()] = TimeSlotAllocation.BUSY;
                       }
                   }
               }
            } catch(Exception exception) { }
        }
        
        return result;
    }
}
