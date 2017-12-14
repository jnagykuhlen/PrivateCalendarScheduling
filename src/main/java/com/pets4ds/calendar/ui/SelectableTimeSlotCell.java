/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.scheduling.TimeSlot;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SelectableTimeSlotCell extends ListCell<TimeSlot> {
    private final CheckBox _checkBox;
    
    public SelectableTimeSlotCell() {
        _checkBox = new CheckBox();
    }
                    
    @Override
    protected void updateItem(TimeSlot timeSlot, boolean empty) {
        super.updateItem(timeSlot, empty);

        if(!empty && timeSlot != null) {
            setBackground(StyleHelper.getHighlightBackground(_checkBox.isSelected()));
            setText(timeSlot.toString());
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
