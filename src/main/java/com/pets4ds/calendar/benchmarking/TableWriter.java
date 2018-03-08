/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
