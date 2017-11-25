package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.CommunicationSessionDescription;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import com.pets4ds.calendar.network.*;
import com.pets4ds.calendar.network.socket.DatagramBroadcastChannel;
import com.pets4ds.calendar.network.socket.SocketCommunicationSetup;
import com.pets4ds.calendar.scheduling.SchedulingManager;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable, Closeable, CommunicationSetupHandler, BroadcastHandler {
    private static final int MAX_NUMBER_OF_OPEN_INVITATIONS = 3;
    
    private SchedulingManager _schedulingManager;
    private Handler _loggingHandler;
    private Stage _loggingStage;
    private Set<CommunicationSessionDescription> _openInvitations; // TODO: Refactor into SchedulingManager class
    
    @FXML
    private TabPane _schedulingTabPane;
    
    @FXML
    private Label _placeholderLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding binding = Bindings.isEmpty(_schedulingTabPane.getTabs());
        _placeholderLabel.visibleProperty().bind(binding);
        _placeholderLabel.managedProperty().bind(binding);
        
        CommunicationSetup communicationSetup = new SocketCommunicationSetup(this);
        BroadcastChannel broadcastChannel = new DatagramBroadcastChannel(this, 23934);
        _schedulingManager = new SchedulingManager(communicationSetup, broadcastChannel, 23940);
        _openInvitations = new HashSet<>();
        
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
            InitiateDialogSceneController dialogController = dialogLoader.getController();

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
                String sessionDescriptionText = dialogController.getDescription();
                
                CommunicationSession session = _schedulingManager.createSchedulingSession(sessionName, sessionDescriptionText);
                showSchedulingSessionTab(session);
                
                Logger.getLogger(getClass().getName()).log(
                    Level.INFO,
                    MessageFormat.format("Successfully initiated scheduling session \"{0}\".", sessionName)
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
    
    @Override
    public void handleSetupStateChanged(Serializable state) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Communication setup changed.");
    }

    @Override
    public void handleSetupFinished() {
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Communication setup completed.");
    }
    
    @Override
    public void handleSetupError(NetworkException exception) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "A network error occurred during communication setup.", exception);
    }

    @Override
    public void handleBroadcastError(NetworkException exception) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "A network error occurred during broadcasting.", exception);
    }

    @Override
    public void handleBroadcastReceived(Serializable serializable) {
        try {
            CommunicationSessionDescription sessionDescription = (CommunicationSessionDescription)serializable;
            
            Logger.getLogger(getClass().getName()).log(
                Level.INFO,
                MessageFormat.format(
                    "Successfully received invitation to session \"{0}\" at {1}.",
                    sessionDescription.getName(),
                    sessionDescription.getInitiatorAddress()
                )
            );
            
            if(_schedulingManager.getSession(sessionDescription) == null) {
                synchronized(_openInvitations) {
                    if(!_openInvitations.contains(sessionDescription) && _openInvitations.size() < MAX_NUMBER_OF_OPEN_INVITATIONS) {
                        _openInvitations.add(sessionDescription);
                        Platform.runLater(() -> { showInvitationAlert(sessionDescription); });
                    }
                }
            }
        } catch(ClassCastException exception) {
            Logger.getLogger(getClass().getName()).log(
                Level.WARNING,
                MessageFormat.format("Received invalid broadcast of type {0}.", serializable.getClass().getName())
            );
        }
    }
    
    private void showInvitationAlert(CommunicationSessionDescription sessionDescription) {
        ButtonType participateButton = new ButtonType("Participate", ButtonBar.ButtonData.YES);
        ButtonType ignoreButton = new ButtonType("Ignore", ButtonBar.ButtonData.NO);
        
        Alert alert = new Alert(
            AlertType.CONFIRMATION,
            sessionDescription.getDescriptionText(),
            participateButton,
            ignoreButton
        );
        
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        alert.setTitle(sessionDescription.getName());
        alert.setHeaderText(MessageFormat.format("You received an invitation to the scheduling session \"{0}\". Would you like to participate?", sessionDescription.getName()));
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.isPresent() && result.get() == participateButton) {
            CommunicationSession session = _schedulingManager.joinSchedulingSession(sessionDescription);
            showSchedulingSessionTab(session);
            
            Logger.getLogger(getClass().getName()).log(
                Level.INFO,
                MessageFormat.format("Successfully joined scheduling session \"{0}\".", session.getDescription().getName())
            );
        }
        
        synchronized(_openInvitations) {
            _openInvitations.remove(sessionDescription);
        }
    }
    
    private void showSchedulingSessionTab(CommunicationSession session) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/fxml/SchedulingTabScene.fxml"));
            Parent tabRoot = tabLoader.load();
            SchedulingController schedulingController = tabLoader.getController();

            Tab tab = new Tab(session.getDescription().getName());
            tab.setContent(tabRoot);

            schedulingController.setDescription(session.getDescription().getDescriptionText());
            _schedulingTabPane.getTabs().add(tab);
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to show scheduling window.", exception);
        }
    }
}
