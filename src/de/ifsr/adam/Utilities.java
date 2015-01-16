/*
 * The MIT License
 *
 * Copyright 2015 Simon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.ifsr.adam;

import java.io.File;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * some static functions.
 * @author Simon
 */
public class Utilities {

    /**
     *  Checks if the file ends with .JSON or .json
     * @param filePath
     * @return
     */
    public static Boolean isJSONFile(String filePath) {
	return filePath.endsWith(".JSON") || filePath.endsWith(".json");
    }

    /**
     * Checks if there is a directory at the end of the path.
     * @param dirPath
     * @return
     */
    public static Boolean isDirectoryPath(String dirPath) {
	File file = new File(dirPath);
	return file.isDirectory();
    }

    public static ObservableList<String> getAllJSONFiles(String folderPath) {
	ArrayList<String> fileNames = new ArrayList<>();
	File folder = new File(folderPath);
	if (folder.isDirectory()) {
	    File[] files = folder.listFiles();
	    for (File file : files) {
		if (isJSONFile(file.getAbsolutePath())) {
		    fileNames.add(file.getName());
		}
	    }
	}
	return FXCollections.observableList(fileNames);
    }

    /**
     * Checks if there is a file at the end of the path.
     * @param filePath
     * @return
     */
    public static Boolean isFilePath(String filePath) {
	File file = new File(filePath);
	return file.isFile();
    }

    /**
     * Checks if the file ends with .CSV or .csv
     * @param filePath
     * @return
     */
    public static Boolean isCSVFile(String filePath) {
	return filePath.endsWith(".CSV") || filePath.endsWith(".csv");
    }

    public static ObservableList<String> getTableNames() {
	ObservableList<String> tableNames;
	tableNames = FXCollections.observableList(DBController.getInstance().getTableNames());
	return tableNames;
    }
    
}
