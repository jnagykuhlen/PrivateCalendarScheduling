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
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingController implements Initializable {
    
    @FXML
    private Label _descriptionLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void handleStartScheduling(ActionEvent event) {
        System.out.println("Starting scheduling...");
    }
    
    @FXML
    private void handleResendInvite(ActionEvent event) {
        System.out.println("Resend invite...");
    }
    
    public String getDescription() {
        return _descriptionLabel.getText();
    }
    
    public void setDescription(String description) {
        _descriptionLabel.setText(description);
    }
}
