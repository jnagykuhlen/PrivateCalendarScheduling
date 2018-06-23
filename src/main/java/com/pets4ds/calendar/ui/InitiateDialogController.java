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

import com.pets4ds.calendar.scheduling.SchedulingSchemeIdentifier;
import com.pets4ds.calendar.scheduling.TimeSlot;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class InitiateDialogController implements Initializable {
    private boolean _isAccepted;
    private Stage _timeSlotSelectionStage;
    private TimeSlotSelectionController _timeSlotSelectionController;
    
    @FXML
    private TabPane _tabPane;
    
    @FXML
    private Button _acceptButton;
    
    @FXML
    private TextField _nameTextField;
    
    @FXML
    private TextArea _descriptionTextArea;
    
    @FXML
    private ChoiceBox _schedulingSchemeChoiceBox;
    
    @FXML
    private Label _placeholderLabel;
    
    @FXML
    private ListView _timeSlotListView;
    
    public InitiateDialogController() {
        _isAccepted = false;
        _timeSlotSelectionStage = null;
        _timeSlotSelectionController = null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding emptyNameBinding = Bindings.createBooleanBinding(
            () -> { return _nameTextField.getText().trim().isEmpty(); },
            _nameTextField.textProperty()
        );
        
        BooleanBinding emptyTimeSlotsBinding = Bindings.isEmpty(_timeSlotListView.getItems());
        
        _acceptButton.disableProperty().bind(Bindings.or(emptyNameBinding, emptyTimeSlotsBinding));
        
        _placeholderLabel.visibleProperty().bind(emptyTimeSlotsBinding);
        _placeholderLabel.managedProperty().bind(emptyTimeSlotsBinding);
        
        _schedulingSchemeChoiceBox.getItems().addAll((Object[])SchedulingSchemeIdentifier.values());
        _schedulingSchemeChoiceBox.getSelectionModel().select(0);
        
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(event -> handleAddTimeSlot(event));
        
        _timeSlotListView.setContextMenu(new ContextMenu(addMenuItem));
        _timeSlotListView.setCellFactory(new Callback<ListView<TimeSlot>, ListCell<TimeSlot>>() {
            @Override
            public ListCell<TimeSlot> call(ListView<TimeSlot> param) {
                return new EditableTimeSlotCell(_timeSlotListView, event -> handleAddTimeSlot(event));
            }
        });
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
    
    @FXML
    private void handleNextTab(ActionEvent event) {
        _tabPane.getSelectionModel().selectNext();
    }
    
    @FXML
    private void handlePreviousTab(ActionEvent event) {
        _tabPane.getSelectionModel().selectPrevious();
    }
    
    private void handleAddTimeSlot(ActionEvent event) {
        if(_timeSlotSelectionStage == null || _timeSlotSelectionController == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TimeSlotSelectionScene.fxml"));
                Parent dialogRoot = loader.load();
                _timeSlotSelectionController = loader.getController();

                _timeSlotSelectionStage = new Stage();
                _timeSlotSelectionStage.setScene(new Scene(dialogRoot));
                _timeSlotSelectionStage.setTitle("Select Time Slot");
                _timeSlotSelectionStage.setResizable(false);
                _timeSlotSelectionStage.initModality(Modality.APPLICATION_MODAL);
                _timeSlotSelectionStage.initStyle(StageStyle.UTILITY);
                _timeSlotSelectionStage.initOwner(_tabPane.getScene().getWindow());
            } catch(IOException exception) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to show time slot selection window.", exception);
            }
        }
        
        if(_timeSlotSelectionStage != null && _timeSlotSelectionController != null) {
            _timeSlotSelectionStage.showAndWait();

            if(_timeSlotSelectionController.isAccepted())
                _timeSlotListView.getItems().add(_timeSlotSelectionController.getTimeSlot());
        }
    }
    
    public boolean isAccepted() {
        return _isAccepted;
    }
    
    public String getName() {
        return _nameTextField.getText().trim();
    }
    
    public String getDescriptionText() {
        return _descriptionTextArea.getText().trim();
    }
    
    public SchedulingSchemeIdentifier getSchedulingScheme() {
        return (SchedulingSchemeIdentifier)_schedulingSchemeChoiceBox.getSelectionModel().getSelectedItem();
    }
    
    public TimeSlot[] getTimeSlots() {
        return (TimeSlot[])_timeSlotListView.getItems().toArray(new TimeSlot[_timeSlotListView.getItems().size()]);
    }
}
