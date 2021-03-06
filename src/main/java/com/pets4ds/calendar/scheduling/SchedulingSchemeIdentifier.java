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
package com.pets4ds.calendar.scheduling;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public enum SchedulingSchemeIdentifier {
    FIRST_MATCH("First Match", new FirstMatchSchedulingScheme()),
    BEST_MATCH("Best Match", new BestMatchSchedulingScheme());
    
    private final String _fullName;
    private final SchedulingScheme _schedulingScheme;
    
    SchedulingSchemeIdentifier(String fullName, SchedulingScheme schedulingScheme) {
        _fullName = fullName;
        _schedulingScheme = schedulingScheme;
    }
    
    public String getFullName() {
        return _fullName;
    }
    
    public SchedulingScheme getSchedulingScheme() {
        return _schedulingScheme;
    }
    
    @Override
    public String toString() {
        return _fullName;
    }
}
