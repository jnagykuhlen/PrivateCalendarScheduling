/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class InitiateDialogSceneController implements Initializable {
    private boolean _isAccepted;
    private String _name;
    private String _description;
    
    @FXML
    private TextField _nameTextField;
    
    @FXML
    private TextArea _descriptionTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        _isAccepted = false;
        _name = null;
        _description = null;
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
        _description = _descriptionTextArea.getText().trim();
        
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public boolean isAccepted() {
        return _isAccepted;
    }
    
    public String getName() {
        return _name;
    }
    
    public String getDescription() {
        return _description;
    }
}
