/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import javafx.scene.control.Tab;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TabInfo {
    private final SchedulingController _controller;
    private final Tab _tab;
    
    public TabInfo(SchedulingController controller, Tab tab) {
        _controller = controller;
        _tab = tab;
    }
    
    public SchedulingController getController() {
        return _controller;
    }
    
    public Tab getTab() {
        return _tab;
    }
}
