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
package com.pets4ds.calendar.network;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class CommunicationSession implements Serializable {
    private static final long serialVersionUID = -4473389164060835118L;
    
    private final String _name;
    private final String _descriptionText;
    private final InetSocketAddress _initiatorAddress;
    private final UUID _uuid;
    
    public CommunicationSession(String name, String descriptionText, InetSocketAddress initiatorAddress) {
        _name = name;
        _descriptionText = descriptionText;
        _initiatorAddress = initiatorAddress;
        _uuid = UUID.randomUUID();
    }
    
    public String getName() {
        return _name;
    }
    
    public String getDescriptionText() {
        return _descriptionText;
    }
    
    public InetSocketAddress getInitiatorAddress() {
        return _initiatorAddress;
    }
    
    public UUID getUUID() {
        return _uuid;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this._uuid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CommunicationSession other = (CommunicationSession) obj;
        if (!Objects.equals(this._uuid, other._uuid)) {
            return false;
        }
        return true;
    }
}
