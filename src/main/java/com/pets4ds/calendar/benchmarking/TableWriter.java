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
package com.pets4ds.calendar.benchmarking;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Jonas Nagy-Kuhlen <jonas.nagy-kuhlen@rwth-aachen.de>
 */
public class TableWriter implements Closeable {
    private final PrintWriter _writer;
    private final int _numberOfColumns;
    private final String _cellFormat;
    
    public TableWriter(String filePath, int numberOfColumns, int cellWidth) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        _writer = new PrintWriter(file, "UTF-8");
        _numberOfColumns = numberOfColumns;
        _cellFormat = "%" + cellWidth + "s";
    }
    
    public void writeCells(Object... values) {
        for(int i = 0; i < _numberOfColumns; ++i) {
            Object item = "";
            if(i < values.length)
                item = values[i];
            
            _writer.print(String.format(_cellFormat, item));
            if(i < _numberOfColumns - 1)
                _writer.print('|');
        }
        
        _writer.println();
    }
    
    public void writeSeparator() {
        final String line = String.format(_cellFormat, "").replace(' ', '-');
        for(int i = 0; i < _numberOfColumns; ++i) {
            _writer.print(line);
            if(i < _numberOfColumns - 1)
                _writer.print('+');
        }
        
        _writer.println();
    }
    
    public void flush() {
        _writer.flush();
    }
    
    @Override
    public void close() throws IOException {
        _writer.close();
    }
    
}
