/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.PartyRole;
import com.pets4ds.calendar.scheduling.SchedulingManager;
import com.pets4ds.calendar.scheduling.SchedulingSession;
import com.pets4ds.calendar.scheduling.TimeSlot;
import com.pets4ds.calendar.scheduling.TimeSlotAllocation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingController implements Initializable {
    private final SchedulingManager _schedulingManager;
    private final SchedulingSession _session;
    private final TimeSlotAllocation[] _localInputs;
    
    @FXML
    private Label _descriptionLabel;
    
    @FXML
    private ListView _partyListView;
    
    @FXML
    private ListView _timeSlotListView;
    
    @FXML
    private Button _resendInviteButton;
    
    @FXML
    private CheckBox _readyCheckBox;
    
    @FXML
    private Pane _overlayPane;
    
    public SchedulingController(SchedulingManager schedulingManager, SchedulingSession session) {
        _schedulingManager = schedulingManager;
        _session = session;
        _localInputs = new TimeSlotAllocation[session.getTimeSlots().length];
        for(int i = 0; i < _localInputs.length; ++i)
            _localInputs[i] = TimeSlotAllocation.BUSY;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        _descriptionLabel.setText(_session.getDescriptionText());
        
        _partyListView.setSelectionModel(new EmptySelectionModel());
        _partyListView.setCellFactory(new Callback<ListView<CommunicationParty>, ListCell<CommunicationParty>>() {
            @Override
            public ListCell<CommunicationParty> call(ListView<CommunicationParty> param) {
                return new PartyCell();
            }
        });
        
        _timeSlotListView.setSelectionModel(new EmptySelectionModel());
        _timeSlotListView.setCellFactory(new Callback<ListView<TimeSlot>, ListCell<TimeSlot>>() {
            @Override
            public ListCell<TimeSlot> call(ListView<TimeSlot> param) {
                TimeSlotCell cell = new TimeSlotCell();
                CheckBox checkBox = cell.getCheckBox();
                checkBox.setOnAction((event) -> { handleTimeSlotSelected(cell.getIndex(), checkBox.isSelected()); } );
                return cell;
            }
        });
        
        _timeSlotListView.getItems().addAll((Object[])_session.getTimeSlots());
    }
    
    private void handleTimeSlotSelected(int slotIndex, boolean selected) {
        _localInputs[slotIndex] = TimeSlotAllocation.BUSY;
        if(selected)
            _localInputs[slotIndex] = TimeSlotAllocation.FREE;
        
        _timeSlotListView.getItems().clear();
        _timeSlotListView.getItems().addAll((Object[])_session.getTimeSlots());
    }
    
    public void handleSessionChanged(CommunicationSessionState sessionState) {
        Platform.runLater(() -> { updateUI(sessionState); });
    }
    
    public void handleSchedulingStarted() {
        Parent root = _overlayPane.getParent();
        for(Node node : root.getChildrenUnmodifiable())
            node.setDisable(true);
        
        _overlayPane.setDisable(false);
        _overlayPane.setVisible(true);
    }
    
    public TimeSlotAllocation[] getLocalInputs() {
        return _localInputs;
    }
    
    private void updateUI(CommunicationSessionState sessionState) {
        _resendInviteButton.setDisable(sessionState.getLocalRole() != PartyRole.INITIATOR);
        
        _partyListView.getItems().clear();
        for(int i = 0; i < sessionState.getParties().length; ++i)
            _partyListView.getItems().add(sessionState.getParties()[i]);
    }
    
    @FXML
    private void handleReady(ActionEvent event) {
        _schedulingManager.setLocalReadyState(_session, _readyCheckBox.isSelected());
    }
    
    @FXML
    private void handleResendInvite(ActionEvent event) {
        _schedulingManager.resendInvite(_session);
    }
}
