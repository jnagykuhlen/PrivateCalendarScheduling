/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import com.pets4ds.calendar.network.CommunicationParty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class PartyCell extends ListCell<CommunicationParty> {
    @Override
    protected void updateItem(CommunicationParty party, boolean empty) {
        super.updateItem(party, empty);

        if(!empty && party != null) {
            setText(party.getName());
            setGraphic(StyleHelper.createCheckSymbol(party.isReady()));
            setContentDisplay(ContentDisplay.LEFT);
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}
