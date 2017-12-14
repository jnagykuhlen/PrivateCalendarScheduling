package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.NetworkException;
import com.pets4ds.calendar.scheduling.SchedulingException;
import com.pets4ds.calendar.scheduling.SchedulingHandler;
import com.pets4ds.calendar.scheduling.SchedulingManager;
import com.pets4ds.calendar.scheduling.SchedulingSchemeIdentifier;
import com.pets4ds.calendar.scheduling.SchedulingSession;
import com.pets4ds.calendar.scheduling.SchedulingState;
import com.pets4ds.calendar.scheduling.TimeSlot;
import com.pets4ds.calendar.scheduling.TimeSlotAllocation;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Border;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable, Closeable {
    private final SchedulingManager _schedulingManager;
    private final Map<CommunicationSession, TabInfo> _schedulingTabs;
    
    private Handler _loggingHandler;
    private Stage _loggingStage;
    
    @FXML
    private TabPane _schedulingTabPane;
    
    @FXML
    private Label _placeholderLabel;
    
    @FXML
    private Text _nameText;
    
    @FXML
    private Hyperlink _changeNameHyperlink;
    
    public MainController() {
        SchedulingHandler schedulingHandler = new SchedulingHandler() {
            @Override
            public void sessionInviteReceived(SchedulingSession session) {
                handleSessionInviteReceived(session);
            }

            @Override
            public void sessionChanged(SchedulingSession session, CommunicationSessionState sessionState) {
                handleSessionChanged(session, sessionState);
            }

            @Override
            public void sessionDisconnected(SchedulingSession session) {
                handleSessionDisconnected(session);
            }

            @Override
            public void networkError(NetworkException exception) {
                handleNetworkError(exception);
            }

            @Override
            public void schedulingStarted(SchedulingSession session) {
                handleSchedulingStarted(session);
            }
            
            @Override
            public void schedulingFailed(SchedulingSession session, SchedulingException exception) {
                handleSchedulingFailed(session, exception);
            }

            @Override
            public void schedulingFinished(SchedulingSession session, Optional<Integer> selectedSlotIndex) {
                handleSchedulingFinished(session, selectedSlotIndex);
            }
            
            @Override
            public TimeSlotAllocation[] getLocalInput(SchedulingSession session) {
                return handleGetLocalInput(session);
            }
        };
        
        _schedulingManager = new SchedulingManager(schedulingHandler, 23940, 24000, 23934);
        _schedulingTabs = new HashMap<>();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding binding = Bindings.isEmpty(_schedulingTabPane.getTabs());
        _placeholderLabel.visibleProperty().bind(binding);
        _placeholderLabel.managedProperty().bind(binding);
        
        _nameText.setText(_schedulingManager.getLocalName());
        _changeNameHyperlink.setBorder(Border.EMPTY);
        
        try {
            FXMLLoader loggingLoader = new FXMLLoader(getClass().getResource("/fxml/LoggingScene.fxml"));
            Parent loggingRoot = loggingLoader.load();
            LoggingController loggingController = loggingLoader.getController();

            _loggingStage = new Stage();
            _loggingStage.setScene(new Scene(loggingRoot));
            _loggingStage.setTitle("Log");
            _loggingStage.setMinWidth(480);
            _loggingStage.setMinHeight(320);
            
            _loggingHandler = (Handler)loggingController;
            Logger.getLogger("").addHandler(_loggingHandler);
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to open logging window.", exception);
        }
    }
    
    @Override
    public void close() {
        try {
            _schedulingManager.close();
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Unable to close scheduling manager.", exception);
        }
        
        if(_loggingHandler != null)
            Logger.getLogger("").removeHandler(_loggingHandler);
        
        _loggingStage.close();
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void handleInitiateScheduling(ActionEvent event) {
        try {
            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/fxml/InitiateDialogScene.fxml"));
            Parent dialogRoot = dialogLoader.load();
            InitiateDialogController dialogController = dialogLoader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(dialogRoot));
            stage.setTitle("Appointment Information");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.initOwner(_schedulingTabPane.getScene().getWindow());
            stage.showAndWait();
            
            if(dialogController.isAccepted()) {
                String sessionName = dialogController.getName();
                String sessionDescriptionText = dialogController.getDescriptionText();
                SchedulingSchemeIdentifier schedulingScheme = dialogController.getSchedulingScheme();
                TimeSlot[] timeSlots = dialogController.getTimeSlots();
                
                SchedulingSession session = _schedulingManager.createSchedulingSession(sessionName, sessionDescriptionText, schedulingScheme, timeSlots);
                showSchedulingSessionTab(session);
                _schedulingManager.publishLocalPartyState(session);
                
                Logger.getLogger(getClass().getName()).log(
                    Level.INFO,
                    MessageFormat.format(
                        "Successfully initiated scheduling session \"{0}\". Participating as \"{1}\" at {2}.",
                        sessionName,
                        _schedulingManager.getLocalName(),
                        _schedulingManager.getLocalSchedulingAddress(session)
                    )
                );
            }
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to initiate scheduling.", exception);
        }
    }
    
    @FXML
    private void handleShowLog(ActionEvent event) {
        _loggingStage.show();
    }
    
    @FXML
    private void handleChangeName(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(_schedulingManager.getLocalName());
        dialog.setTitle("Select Name");
        dialog.setHeaderText(null);
        dialog.setContentText("Name:");
        
        BooleanBinding invalidBinding = Bindings.createBooleanBinding(
            () -> { return dialog.getEditor().getText().trim().isEmpty(); },
            dialog.getEditor().textProperty()
        );
        
        Button buttonOk = (Button)dialog.getDialogPane().lookupButton(ButtonType.OK);
        buttonOk.setText("OK");
        buttonOk.disableProperty().bind(invalidBinding);
        
        Button buttonCancel = (Button)dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        buttonCancel.setText("Cancel");
        
        Stage stage = (Stage)dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            String localPartyName = result.get();
            _schedulingManager.setLocalName(localPartyName);
            _nameText.setText(localPartyName);
        }
        
        _changeNameHyperlink.setVisited(false);
    }
    
    private void handleSchedulingStarted(SchedulingSession session) {
        Logger.getLogger(getClass().getName()).log(
            Level.INFO,
            MessageFormat.format(
                "Started scheduling of session \"{0}\".",
                session.getName()
            )
        );
        
        _schedulingTabs.get(session).getController().handleSchedulingStarted();
    }
    
    private void handleSchedulingFinished(SchedulingSession session, Optional<Integer> selectedSlotIndex) {
        String additionalMessage = "No matching slot was found.";
        if(selectedSlotIndex.isPresent())
            additionalMessage = MessageFormat.format("The time slot with index {0} was selected.", selectedSlotIndex.get());
        
        Logger.getLogger(getClass().getName()).log(
            Level.INFO,
            MessageFormat.format(
                "Successfully finished scheduling of session \"{0}\". {1}",
                session.getName(),
                additionalMessage
            )
        );
        
        _schedulingTabs.get(session).getController().handleSchedulingFinished(selectedSlotIndex);
    }
    
    private void handleSchedulingFailed(SchedulingSession session, SchedulingException exception) {
        Logger.getLogger(getClass().getName()).log(
            Level.SEVERE,
            MessageFormat.format(
                "Scheduling of session \"{0}\" failed.",
                session.getName()
            ),
            exception
        );
        
        leaveSession(session);
        
        String title = "Scheduling Failed";
        String descriptionText = MessageFormat.format(
            "Scheduling computation failed for session \"{0}\". See log for further information.",
            session.getName()
        );
        
        Platform.runLater(() -> { disconnectSession(session, title, descriptionText); });
    }
    
    private TimeSlotAllocation[] handleGetLocalInput(CommunicationSession session) {
        return _schedulingTabs.get(session).getController().getLocalInputs();
    }

    private void handleSessionDisconnected(CommunicationSession session) {
        Logger.getLogger(getClass().getName()).log(
            Level.INFO,
            MessageFormat.format(
                "Lost connection to session \"{0}\".",
                session.getName()
            )
        );
        
        if(_schedulingManager.getSchedulingState((SchedulingSession)session) == SchedulingState.SETUP) {
            String title = "Disconnected";
            String descriptionText = MessageFormat.format(
                "Scheduling session \"{0}\" was closed by the initiator or the connection was lost.",
                session.getName()
            );

            Platform.runLater(() -> { disconnectSession(session, title, descriptionText); });
        }
    }
    
    private void disconnectSession(CommunicationSession session, String title, String descriptionText) {
        _schedulingTabPane.getTabs().remove(_schedulingTabs.get(session).getTab());
        _schedulingTabs.remove(session);
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(descriptionText);
        
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        alert.showAndWait();
    }
    
    private void handleSessionChanged(SchedulingSession session, CommunicationSessionState sessionState) {
        _schedulingTabs.get(session).getController().handleSessionChanged(sessionState);
    }
    
    private void handleNetworkError(NetworkException exception) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "A network error occurred.", exception);
    }

    private void handleSessionInviteReceived(SchedulingSession session) {
        Logger.getLogger(getClass().getName()).log(
            Level.INFO,
            MessageFormat.format(
                "Successfully received invitation to session \"{0}\" at {1}.",
                session.getName(),
                session.getInitiatorAddress()
            )
        );
        
        Platform.runLater(() -> { showInvitationAlert(session); });
    }
    
    private void joinSession(SchedulingSession session) {
        try {
            _schedulingManager.acceptInvite(session);
            showSchedulingSessionTab(session);
            _schedulingManager.publishLocalPartyState(session);
            
            Logger.getLogger(getClass().getName()).log(
                Level.INFO,
                MessageFormat.format(
                    "Successfully joined scheduling session \"{0}\". Participating as \"{1}\" at {2}.",
                    session.getName(),
                    _schedulingManager.getLocalName(),
                    _schedulingManager.getLocalSchedulingAddress(session)
                )
            );
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to join scheduling.", exception);
        }
    }
    
    private void ignoreSession(SchedulingSession session) {
        _schedulingManager.ignoreInvite(session);
        
        Logger.getLogger(getClass().getName()).log(
            Level.INFO,
            MessageFormat.format("Ignored scheduling session \"{0}\".", session.getName())
        );
    }
    
    private void leaveSession(SchedulingSession session) {
        try {
            if(_schedulingManager.isParticipating(session))
                _schedulingManager.leaveSchedulingSession(session);
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to leave scheduling.", exception);
        }
    }
    
    private void showInvitationAlert(SchedulingSession session) {
        ButtonType participateButton = new ButtonType("Participate", ButtonBar.ButtonData.YES);
        ButtonType ignoreButton = new ButtonType("Ignore", ButtonBar.ButtonData.NO);
        
        Alert alert = new Alert(
            AlertType.CONFIRMATION,
            session.getDescriptionText(),
            participateButton,
            ignoreButton
        );
        
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        alert.setTitle(session.getName());
        alert.setHeaderText(MessageFormat.format("You received an invitation to the scheduling session \"{0}\". Would you like to participate?", session.getName()));
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == participateButton) {
            joinSession(session);
        } else {
            ignoreSession(session);
        }
    }
    
    private void showSchedulingSessionTab(SchedulingSession session) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/fxml/SchedulingTabScene.fxml"));
            tabLoader.setControllerFactory((Class<?> type) -> new SchedulingController(_schedulingManager, session));
            
            Parent tabRoot = tabLoader.load();
            SchedulingController schedulingController = tabLoader.getController();
            
            Tab tab = new Tab(MessageFormat.format("{0} [{1}]", session.getName(), session.getSchedulingSchemeIdentifier()));
            tab.setContent(tabRoot);
            tab.setOnClosed((event) -> { leaveSession(session); });
            
            _schedulingTabPane.getTabs().add(tab);
            _schedulingTabs.put(session, new TabInfo(schedulingController, tab));
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to show scheduling window.", exception);
        }
    }
}
