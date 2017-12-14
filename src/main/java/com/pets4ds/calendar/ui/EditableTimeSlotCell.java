/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
