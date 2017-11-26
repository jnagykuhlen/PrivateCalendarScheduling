/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.BroadcastChannel;
import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSessionDescription;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.CommunicationSetup;
import com.pets4ds.calendar.network.PartyRole;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingController implements Initializable {
    private final CommunicationSetup _communicationSetup;
    private final BroadcastChannel _broadcastChannel;
    private final CommunicationSessionDescription _sessionDescription;
    private final CommunicationParty _localParty;
    
    @FXML
    private Label _descriptionLabel;
    
    @FXML
    private ListView _partyListView;
    
    @FXML
    private Button _resendInviteButton;
    
    @FXML
    private CheckBox _readyCheckBox;
    
    public SchedulingController(CommunicationSetup communicationSetup, BroadcastChannel broadcastChannel, CommunicationSessionDescription sessionDescription, CommunicationParty localParty) {
        _communicationSetup = communicationSetup;
        _broadcastChannel = broadcastChannel;
        _sessionDescription = sessionDescription;
        _localParty = localParty;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        _descriptionLabel.setText(_sessionDescription.getDescriptionText());
        
        _partyListView.setCellFactory(new Callback<ListView<CommunicationParty>, ListCell<CommunicationParty>>() {
            @Override
            public ListCell<CommunicationParty> call(ListView<CommunicationParty> param) {
                return new ListCell<CommunicationParty>() {
                    @Override
                    protected void updateItem(CommunicationParty party, boolean empty) {
                        super.updateItem(party, empty);
                        
                        if(party != null) {
                            Color color = Color.LIGHTSALMON;
                            if(party.isReady())
                                color = Color.LIGHTGREEN;
                            
                            Rectangle rectangle = new Rectangle(20, 20);
                            Stop[] stops = new Stop[] { new Stop(0, color), new Stop(1, Color.TRANSPARENT)};
                            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
                            rectangle.setFill(gradient);
                            
                            setText(party.getName());
                            setGraphic(rectangle);
                            setContentDisplay(ContentDisplay.LEFT);
                        }

                    }
                };
            }
        });
    }
    
    public void handleSessionChanged(CommunicationSessionState sessionState) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, MessageFormat.format("Communication setup changed: #parties = {0}", sessionState.getParties().length));
        
        Platform.runLater(() -> { updateUI(sessionState); });
    }
    
    private void updateUI(CommunicationSessionState sessionState) {
        _resendInviteButton.setDisable(sessionState.getLocalRole() != PartyRole.INITIATOR);
        
        _partyListView.getItems().clear();
        for(int i = 0; i < sessionState.getParties().length; ++i)
            _partyListView.getItems().add(sessionState.getParties()[i]);
    }
    
    @FXML
    private void handleReady(ActionEvent event) {
        CommunicationParty party = new CommunicationParty(_localParty.getName(), _localParty.getSetupState(), _readyCheckBox.isSelected());
        _communicationSetup.setLocalParty(_sessionDescription, party);
    }
    
    @FXML
    private void handleResendInvite(ActionEvent event) {
        _broadcastChannel.publish(_sessionDescription);
    }
}
