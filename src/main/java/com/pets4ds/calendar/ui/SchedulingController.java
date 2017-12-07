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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    private Optional<Integer> _selectedSlotIndex;
    
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
    
    @FXML
    private ProgressIndicator _progressIndicator;
    
    @FXML
    private Label _progressLabel;
    
    @FXML
    private TextFlow _resultTextFlow;
    
    @FXML
    private Text _resultIntroText;
    
    @FXML
    private Text _resultText;
    
    @FXML
    private Hyperlink _exportHyperlink;
    
    public SchedulingController(SchedulingManager schedulingManager, SchedulingSession session) {
        _schedulingManager = schedulingManager;
        _session = session;
        _localInputs = new TimeSlotAllocation[session.getTimeSlots().length];
        for(int i = 0; i < _localInputs.length; ++i)
            _localInputs[i] = TimeSlotAllocation.BUSY;
        
        _selectedSlotIndex = null;
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
        _exportHyperlink.setBorder(Border.EMPTY);
    }
    
    private void handleTimeSlotSelected(int slotIndex, boolean selected) {
        _localInputs[slotIndex] = TimeSlotAllocation.BUSY;
        if(selected)
            _localInputs[slotIndex] = TimeSlotAllocation.FREE;
        
        _timeSlotListView.getItems().clear();
        _timeSlotListView.getItems().addAll((Object[])_session.getTimeSlots());
    }
    
    public void handleSessionChanged(CommunicationSessionState sessionState) {
        Platform.runLater(() -> { updatePartyList(sessionState); });
    }
    
    public void handleSchedulingStarted() {
        Platform.runLater(() -> { updateProgressScreen(); });
    }
    
    public void handleSchedulingFinished(Optional<Integer> selectedSlotIndex) {
        _selectedSlotIndex = selectedSlotIndex;
        Platform.runLater(() -> { updateResultScreen(selectedSlotIndex); });
    }
    
    public TimeSlotAllocation[] getLocalInputs() {
        return _localInputs;
    }
    
    private void updatePartyList(CommunicationSessionState sessionState) {
        _resendInviteButton.setDisable(sessionState.getLocalRole() != PartyRole.INITIATOR);
        
        _partyListView.getItems().clear();
        for(int i = 0; i < sessionState.getParties().length; ++i)
            _partyListView.getItems().add(sessionState.getParties()[i]);
    }
    
    private void updateProgressScreen() {
        Parent root = _overlayPane.getParent();
        for(Node node : root.getChildrenUnmodifiable())
            node.setDisable(true);
        
        _overlayPane.setDisable(false);
        _overlayPane.setVisible(true);
    }
    
    private void updateResultScreen(Optional<Integer> selectedSlotIndex) {
        _progressLabel.setVisible(false);
        _resultTextFlow.setVisible(true);
        _progressIndicator.setProgress(1.0);
        
        if(selectedSlotIndex.isPresent()) {
            _resultText.setText(_session.getTimeSlots()[selectedSlotIndex.get()].toString());
        } else {
            _resultTextFlow.getChildren().clear();
            _resultTextFlow.getChildren().add(_resultIntroText);
            _resultIntroText.setText("Scheduling completed. No matching date was found.");
        }
    }
    
    @FXML
    private void handleReady(ActionEvent event) {
        _schedulingManager.setLocalReadyState(_session, _readyCheckBox.isSelected());
    }
    
    @FXML
    private void handleResendInvite(ActionEvent event) {
        _schedulingManager.resendInvite(_session);
    }
    
    @FXML
    private void handleExportResult(ActionEvent event) {
        // TODO
        
        _exportHyperlink.setVisited(false);
    }
}
