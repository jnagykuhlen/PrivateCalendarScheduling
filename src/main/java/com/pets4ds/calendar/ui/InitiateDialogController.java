/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.scheduling.SchedulingSchemeIdentifier;
import com.pets4ds.calendar.scheduling.TimeSlot;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TimeSlotSelectionScene.fxml"));
            Parent dialogRoot = loader.load();
            TimeSlotSelectionController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(dialogRoot));
            stage.setTitle("Select Time Slot");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.initOwner(_tabPane.getScene().getWindow());
            stage.showAndWait();
            
            if(controller.isAccepted()) {
                _timeSlotListView.getItems().add(controller.getTimeSlot());
            }
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to show time slot selection window.", exception);
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
