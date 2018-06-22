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

import com.pets4ds.calendar.network.CommunicationParty;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.PartyRole;
import com.pets4ds.calendar.scheduling.CalendarCompatibility;
import com.pets4ds.calendar.scheduling.SchedulingManager;
import com.pets4ds.calendar.scheduling.SchedulingSession;
import com.pets4ds.calendar.scheduling.TimeSlot;
import com.pets4ds.calendar.scheduling.TimeSlotAllocation;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.stage.FileChooser;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class SchedulingController implements Initializable {
    private final static String RESULT_DIRECTORY = "scheduling-results/";
    
    private final SchedulingManager _schedulingManager;
    private final SchedulingSession _session;
    private final TimeSlotAllocation[] _localTimeSlotAllocations;
    private int _localPartyIndex;
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
        _localTimeSlotAllocations = new TimeSlotAllocation[session.getTimeSlots().length];
        for(int i = 0; i < _localTimeSlotAllocations.length; ++i)
            _localTimeSlotAllocations[i] = TimeSlotAllocation.BUSY;
        
        _localPartyIndex = -1;
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
                SelectableTimeSlotCell cell = new SelectableTimeSlotCell(_localTimeSlotAllocations);
                CheckBox checkBox = cell.getCheckBox();
                checkBox.setOnAction(event -> handleTimeSlotSelected(cell.getIndex(), checkBox.isSelected()));
                return cell;
            }
        });
        
        _timeSlotListView.getItems().addAll((Object[])_session.getTimeSlots());
        _exportHyperlink.setBorder(Border.EMPTY);
    }
    
    private void handleTimeSlotSelected(int slotIndex, boolean selected) {
        _localTimeSlotAllocations[slotIndex] = TimeSlotAllocation.BUSY;
        if(selected)
            _localTimeSlotAllocations[slotIndex] = TimeSlotAllocation.FREE;
        
        _timeSlotListView.getItems().clear();
        _timeSlotListView.getItems().addAll((Object[])_session.getTimeSlots());
    }
    
    public void handleSessionChanged(CommunicationSessionState sessionState) {
        _localPartyIndex = sessionState.getLocalPartyIndex();
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
        return _localTimeSlotAllocations;
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
    private void handleImportCalendar(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import iCalendar File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("iCalendar Files", "*.ics", "*.ical", "*.ifb", "*.icalendar"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(_overlayPane.getScene().getWindow());
        if(file != null) {
            try {
                TimeSlotAllocation[] matches = CalendarCompatibility.matchCalendar(file, _session.getTimeSlots());
                System.arraycopy(matches, 0, _localTimeSlotAllocations, 0, _localTimeSlotAllocations.length);
                
                _timeSlotListView.getItems().clear();
                _timeSlotListView.getItems().addAll((Object[])_session.getTimeSlots());
                
                Logger.getLogger(getClass().getName()).log(Level.INFO, "Successfully imported calendar.");
            } catch(IOException exception) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to import calendar.", exception);
            }
        }
    }
    
    @FXML
    private void handleExportResult(ActionEvent event) {
        if(_selectedSlotIndex.isPresent() && _selectedSlotIndex.get() != null) {
            TimeSlot timeSlot = _session.getTimeSlots()[_selectedSlotIndex.get()];
            try {
                String filePath = RESULT_DIRECTORY + MessageFormat.format("{0}-{1}-{2}.ics", _session.getName(), _session.getUUID(), _localPartyIndex);
                File file = new File(filePath);
                Files.createDirectories(Paths.get(RESULT_DIRECTORY));
                CalendarCompatibility.writeEventToFile(file, _session, timeSlot);
                Desktop.getDesktop().open(file);
            } catch(IOException exception) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to export result. Cannot write iCalendar file.", exception);
            }
        } else {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to export result. Invalid computation output.");
        }
        
        _exportHyperlink.setVisited(false);
    }
}
