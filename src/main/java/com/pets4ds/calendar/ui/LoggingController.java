/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class LoggingController extends Handler implements Initializable {
    @FXML
    private TextArea _logTextArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setFormatter(new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord logRecord) {
        Date date = new Date(logRecord.getMillis());
        String formattedDate = (new SimpleDateFormat("HH:mm:ss")).format(date);
        String formattedMessage = MessageFormat.format(
            "[{0} {1} ({2})] {3} \n",
            formattedDate,
            logRecord.getLoggerName(),
            logRecord.getLevel(),
            logRecord.getMessage()
        );
        
        Platform.runLater(() -> {
            _logTextArea.appendText(formattedMessage);
        });
    }

    @Override
    public void flush() { }

    @Override
    public void close() throws SecurityException { }
    
    @FXML
    private void handleClearAll(ActionEvent event) {
        _logTextArea.clear();
    }
}
