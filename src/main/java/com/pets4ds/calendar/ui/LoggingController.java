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
