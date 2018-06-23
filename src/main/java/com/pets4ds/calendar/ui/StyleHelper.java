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
