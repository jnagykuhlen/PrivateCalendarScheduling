package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSessionState;
import com.pets4ds.calendar.network.NetworkException;
import com.pets4ds.calendar.scheduling.SchedulingHandler;
import com.pets4ds.calendar.scheduling.SchedulingManager;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
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
            public void sessionInviteReceived(CommunicationSession session) {
                handleSessionInviteReceived(session);
            }

            @Override
            public void sessionChanged(CommunicationSession session, CommunicationSessionState sessionState) {
                handleSessionChanged(session, sessionState);
            }

            @Override
            public void sessionDisconnected(CommunicationSession session) {
                handleSessionDisconnected(session);
            }

            @Override
            public void networkError(NetworkException exception) {
                handleNetworkError(exception);
            }

            @Override
            public void schedulingStarted(CommunicationSession session) {
                handleSchedulingStarted(session);
            }

            @Override
            public void schedulingFinished(CommunicationSession session) {
                handleSchedulingFinished(session);
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
                _schedulingManager.publishLocalPartyState(session);
                
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
    
    private void handleSchedulingStarted(CommunicationSession session) {
        // TODO
    }
    
    private void handleSchedulingFinished(CommunicationSession session) {
        // TODO
    }

    private void handleSessionDisconnected(CommunicationSession session) {
        Platform.runLater(() -> { disconnectSession(session); });
    }
    
    private void disconnectSession(CommunicationSession session) {
        _schedulingTabPane.getTabs().remove(_schedulingTabs.get(session).getTab());
        _schedulingTabs.remove(session);
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Disconnected");
        alert.setHeaderText(null);
        alert.setContentText(MessageFormat.format("Scheduling session \"{0}\" was closed by the initiator or the connection was lost.", session.getName()));
        
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        
        alert.showAndWait();
    }
    
    private void handleSessionChanged(CommunicationSession session, CommunicationSessionState sessionState) {
        _schedulingTabs.get(session).getController().handleSessionChanged(sessionState);
    }
    
    private void handleNetworkError(NetworkException exception) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "A network error occurred.", exception);
    }

    private void handleSessionInviteReceived(CommunicationSession session) {
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
    
    private void joinSession(CommunicationSession session) {
        try {
            _schedulingManager.acceptInvite(session);
            showSchedulingSessionTab(session);
            _schedulingManager.publishLocalPartyState(session);
            
            Logger.getLogger(getClass().getName()).log(
                Level.INFO,
                MessageFormat.format("Successfully joined scheduling session \"{0}\".", session.getName())
            );
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to join scheduling.", exception);
        }
    }
    
    private void ignoreSession(CommunicationSession session) {
        _schedulingManager.ignoreInvite(session);
        
        Logger.getLogger(getClass().getName()).log(
                Level.INFO,
                MessageFormat.format("Ignored scheduling session \"{0}\".", session.getName())
            );
    }
    
    private void leaveSession(CommunicationSession session) {
        try {
            _schedulingManager.leaveSchedulingSession(session);
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to leave scheduling.", exception);
        }
    }
    
    private void showInvitationAlert(CommunicationSession session) {
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
    
    private void showSchedulingSessionTab(CommunicationSession session) {
        try {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/fxml/SchedulingTabScene.fxml"));
            tabLoader.setControllerFactory((Class<?> type) -> new SchedulingController(_schedulingManager, session));
            
            Parent tabRoot = tabLoader.load();
            SchedulingController schedulingController = tabLoader.getController();
            
            Tab tab = new Tab(session.getName());
            tab.setContent(tabRoot);
            tab.setOnClosed((event) -> { leaveSession(session); });
            
            _schedulingTabPane.getTabs().add(tab);
            _schedulingTabs.put(session, new TabInfo(schedulingController, tab));
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to show scheduling window.", exception);
        }
    }
}
