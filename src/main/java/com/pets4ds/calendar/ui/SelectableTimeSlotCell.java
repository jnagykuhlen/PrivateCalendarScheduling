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

import com.pets4ds.calendar.scheduling.TimeSlot;
import com.pets4ds.calendar.scheduling.TimeSlotAllocation;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SelectableTimeSlotCell extends ListCell<TimeSlot> {
    private final CheckBox _checkBox;
    private final TimeSlotAllocation[] _timeSlotAllocations;
    
    public SelectableTimeSlotCell(TimeSlotAllocation[] timeSlotAllocations) {
        _checkBox = new CheckBox();
        _timeSlotAllocations = timeSlotAllocations;
    }
    
    @Override
    protected void updateItem(TimeSlot timeSlot, boolean empty) {
        super.updateItem(timeSlot, empty);

        if(!empty && timeSlot != null) {
            int index = getIndex();
            if(index >= 0 && index < _timeSlotAllocations.length)
                _checkBox.setSelected(_timeSlotAllocations[index] == TimeSlotAllocation.FREE);
            
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
