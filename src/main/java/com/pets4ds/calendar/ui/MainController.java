package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.CommunicationSession;
import com.pets4ds.calendar.network.CommunicationSetupHandler;
import com.pets4ds.calendar.network.SessionSetupException;
import com.pets4ds.calendar.network.socket.SocketCommunication;
import java.io.Closeable;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable, Closeable, CommunicationSetupHandler {
    private SocketCommunication _communication;
    private Handler _loggingHandler;
    private Stage _loggingStage;
    
    @FXML
    private TabPane _schedulingTabPane;
    
    @FXML
    private Label _placeholderLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BooleanBinding binding = Bindings.isEmpty(_schedulingTabPane.getTabs());
        _placeholderLabel.visibleProperty().bind(binding);
        _placeholderLabel.managedProperty().bind(binding);
        
        _communication = new SocketCommunication(this);
        
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
        _communication.close();
        
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
                String appointmentName = dialogController.getName();
                String appointmentDescription = dialogController.getDescription();
                
                FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/fxml/SchedulingTabScene.fxml"));
                Parent tabRoot = tabLoader.load();
                SchedulingController schedulingController = tabLoader.getController();
                
                Tab tab = new Tab(appointmentName);
                tab.setContent(tabRoot);
                
                schedulingController.setDescription(appointmentDescription);
                _schedulingTabPane.getTabs().add(tab);
                
                _communication.createSession(new CommunicationSession(appointmentName, appointmentDescription, null));
                
                Logger.getLogger(getClass().getName()).log(Level.INFO, MessageFormat.format("Successfully initiated scheduling session \"{0}\".", appointmentName));
            }
        } catch(IOException exception) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Unable to initiate scheduling.", exception);
        }
        
        System.out.println("Initiate scheduling...");
    }
    
    @FXML
    private void handleShowLog(ActionEvent event) {
        _loggingStage.show();
    }

    @Override
    public void handleSessionDiscovery(CommunicationSession session) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Received invitation to session " + session.getName() + " (" + session.getDescription() + ").");
    }

    @Override
    public void handleSetupStateChanged(Serializable state) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Communication setup changed.");
    }

    @Override
    public void handleSetupFinished() {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Communication setup completed.");
    }

    @Override
    public void handleError(SessionSetupException exception) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "An error occurred during communication setup.", exception);
    }
}
