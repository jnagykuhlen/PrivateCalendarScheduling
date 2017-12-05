/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.scheduling.TimeSlot;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TimeSlotCell extends ListCell<TimeSlot> {
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private final CheckBox _checkBox;
    
    public TimeSlotCell() {
        _checkBox = new CheckBox();
    }
                    
    @Override
    protected void updateItem(TimeSlot timeSlot, boolean empty) {
        super.updateItem(timeSlot, empty);

        if(!empty && timeSlot != null) {
            String text = MessageFormat.format(
                "{0} from {1} to {2}",
                timeSlot.getStartDate().format(DATE_FORMATTER),
                timeSlot.getStartDate().format(TIME_FORMATTER),
                timeSlot.getEndDate().format(TIME_FORMATTER)
            );

            long additionalDays = ChronoUnit.DAYS.between(timeSlot.getStartDate().toLocalDate(), timeSlot.getEndDate().toLocalDate());
            if(additionalDays > 0)
                text += MessageFormat.format(" (+{0})", additionalDays);
            
            setBackground(StyleHelper.getHighlightBackground(_checkBox.isSelected()));
            setText(text);
            setGraphic(_checkBox);
            setContentDisplay(ContentDisplay.LEFT);
        } else {
            setBackground(null);
            setText(null);
            setGraphic(null);
        }
    }
    
    public CheckBox getCheckBox() {
        return _checkBox;
    }
}
