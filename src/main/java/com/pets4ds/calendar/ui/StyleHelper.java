/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pets4ds.calendar.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class StyleHelper {
    private final static Color COLOR_DISABLED = new Color(0.0, 0.0, 0.0, 0.1);
    private final static double BACKGROUND_OPACITY = 0.5;
    
    private final static Background BACKGROUND_UNSELECTED = createBackground(Color.LIGHTSALMON.deriveColor(0.0, 1.0, 1.0, BACKGROUND_OPACITY));
    private final static Background BACKGROUND_SELECTED = createBackground(Color.LIGHTGREEN.deriveColor(0.0, 1.0, 1.0, BACKGROUND_OPACITY));
    private final static Node CHECK_SYMBOL_UNSELECTED = createCheckSymbol(COLOR_DISABLED);
    private final static Node CHECK_SYMBOL_SELECTED = createCheckSymbol(Color.LIMEGREEN);
    
    public static Background getHighlightBackground(boolean isSelected) {
        if(isSelected)
            return BACKGROUND_SELECTED;
        return BACKGROUND_UNSELECTED;
    }
    
    public static Node createCheckSymbol(boolean isSelected) {
        if(isSelected)
            return createCheckSymbol(Color.LIMEGREEN);
        return createCheckSymbol(COLOR_DISABLED);
    }
    
    private static Background createBackground(Color color) {
        Stop[] stops = new Stop[] { new Stop(0, color), new Stop(1, Color.TRANSPARENT)};
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        return new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY));
    }
    
    private static Node createCheckSymbol(Color color) {
        Text text = new Text("\u2713");
        text.setFill(color);
        return text;
    }
}
