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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class EditableTimeSlotCell extends ListCell<TimeSlot> {
    private final ListView _listView;
    private final EventHandler<ActionEvent> _addEventHandler;
    
    public EditableTimeSlotCell(ListView listView, EventHandler<ActionEvent> addEventHandler) {
        _listView = listView;
        _addEventHandler = addEventHandler;
    }
                    
    @Override
    protected void updateItem(TimeSlot timeSlot, boolean empty) {
        super.updateItem(timeSlot, empty);

        MenuItem addItem = new MenuItem("Add");
        addItem.setOnAction(_addEventHandler);
        
        ContextMenu contextMenu = new ContextMenu(addItem);
        
        if(!empty && timeSlot != null) {
            MenuItem removeItem = new MenuItem("Remove");
            removeItem.setOnAction(event -> _listView.getItems().remove(timeSlot));
            contextMenu.getItems().add(removeItem);
            
            setText(timeSlot.toString());
        } else {
            setText(null);
        }
        
        setContextMenu(contextMenu);
    }
}
