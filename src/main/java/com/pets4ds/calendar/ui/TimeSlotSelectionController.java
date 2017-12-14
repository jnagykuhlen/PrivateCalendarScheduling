/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.scheduling.TimeSlot;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TimeSlotSelectionController implements Initializable {
    @FXML
    private DatePicker _startDatePicker;
    
    @FXML
    private Spinner _startTimeSpinner;
    
    @FXML
    private DatePicker _endDatePicker;
    
    @FXML
    private Spinner _endTimeSpinner;
    
    @FXML
    private Button _acceptButton;
    
    private boolean _isAccepted;
    
    public TimeSlotSelectionController() {
        _isAccepted = false;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LocalDate currentDate = LocalDate.now();
        _startDatePicker.setValue(currentDate);
        _startTimeSpinner.setValueFactory(new TimeSpinnerValueFactory(LocalTime.of(12, 0)));
        _endDatePicker.setValue(currentDate);
        _endTimeSpinner.setValueFactory(new TimeSpinnerValueFactory(LocalTime.of(13, 0)));
        
        BooleanBinding invalidBinding = Bindings.createBooleanBinding(
            () -> { return !getEndTime().isAfter(getStartTime()); },
            _startDatePicker.valueProperty(),
            _startTimeSpinner.valueProperty(),
            _endDatePicker.valueProperty(),
            _endTimeSpinner.valueProperty()
        );
        
        _acceptButton.disableProperty().bind(invalidBinding);
        
        _startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> handleStartDateChanged(newValue));
        _startTimeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> handleStartTimeChanged((LocalTime)newValue));
    }
    
    private void handleStartDateChanged(LocalDate newValue) {
        if(_endDatePicker.getValue().isBefore(newValue))
            _endDatePicker.setValue(newValue);
    }
    
    private void handleStartTimeChanged(LocalTime newValue) {
        if(_endDatePicker.getValue().equals(_startDatePicker.getValue())) {
            if(((LocalTime)_endTimeSpinner.getValue()).isBefore(newValue))
                _endTimeSpinner.getValueFactory().setValue(newValue);
        }
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleAccept(ActionEvent event) {
        _isAccepted = true;
        
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public boolean isAccepted() {
        return _isAccepted;
    }
    
    public TimeSlot getTimeSlot() {
        return new TimeSlot(getStartTime(), getEndTime());
    }
    
    private LocalDateTime getStartTime() {
        return LocalDateTime.of(_startDatePicker.getValue(), (LocalTime)_startTimeSpinner.getValue());
    }
    
    private LocalDateTime getEndTime() {
        return LocalDateTime.of(_endDatePicker.getValue(), (LocalTime)_endTimeSpinner.getValue());
    }
}
