/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.scheduling.SchedulingSchemeIdentifier;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class InitiateDialogController implements Initializable {
    private boolean _isAccepted;
    private String _name;
    private String _descriptionText;
    private SchedulingSchemeIdentifier _schedulingScheme;
    
    @FXML
    private Button _acceptButton;
    
    @FXML
    private TextField _nameTextField;
    
    @FXML
    private TextArea _descriptionTextArea;
    
    @FXML
    private ChoiceBox _schedulingSchemeChoiceBox;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        _isAccepted = false;
        _name = null;
        _descriptionText = null;
        
        BooleanBinding invalidBinding = Bindings.createBooleanBinding(
            () -> { return _nameTextField.getText().trim().isEmpty(); },
            _nameTextField.textProperty()
        );
        _acceptButton.disableProperty().bind(invalidBinding);
        
        _schedulingSchemeChoiceBox.getItems().addAll((Object[])SchedulingSchemeIdentifier.values());
        _schedulingSchemeChoiceBox.getSelectionModel().select(0);
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleAccept(ActionEvent event) {
        _isAccepted = true;
        _name = _nameTextField.getText().trim();
        _descriptionText = _descriptionTextArea.getText().trim();
        _schedulingScheme = (SchedulingSchemeIdentifier)_schedulingSchemeChoiceBox.getSelectionModel().getSelectedItem();
        
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public boolean isAccepted() {
        return _isAccepted;
    }
    
    public String getName() {
        return _name;
    }
    
    public String getDescriptionText() {
        return _descriptionText;
    }
    
    public SchedulingSchemeIdentifier getSchedulingScheme() {
        return _schedulingScheme;
    }
}
