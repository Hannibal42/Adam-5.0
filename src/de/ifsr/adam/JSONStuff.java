/*
 * The MIT License
 *
 * Copyright 2014 Simon.
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Contains static methods for JSON handling that are not specific to the class that uses them.
 *
 * @author Simon
 */
public class JSONStuff {

    static Logger log = Logger.getLogger(JSONStuff.class.getName());
    /*
     * The standard Encoding is UTF 8
     */
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Imports a JSONArray from a JSON file.
     *
     * @param filePath The file path to the JSONArray you want to import.
     * @return
     */
    public final static JSONArray importJSONArray(String filePath) {
	Path path = Paths.get(filePath);
	String jsonStr = new String();
	JSONArray result = null;
	try (final Scanner scanner = new Scanner(path, ENCODING.name())) {
	    while (scanner.hasNext()) {
		jsonStr += scanner.nextLine();
	    }
	    result = new JSONArray(jsonStr);
	} catch (IOException e) {
	    JSONStuff.log.error("Failed to find JSON File at: " + filePath, e);
	} catch (JSONException e) {
	    JSONStuff.log.error("Not a valid JSON file at:" + filePath, e);
	}
	return result;
    }

    /**
     * Searches a JSONArray for an JSONObject with a specific key and keyValue.
     *
     * @param array The JSONArray you want to search
     * @param key The name of the JSONObject field that is searched
     * @param keyValue The Value of JSONObject field that is searched
     * @return returns the first JSONObject that has the field and the right value, returns null if
     * no JSONobject is found
     */
    public static final JSONObject getSpecificObject(JSONArray array, String key, String keyValue) {
	for (int i = 0; i < array.length(); i++) {
	    try {
		JSONObject currentObject = array.getJSONObject(i);
		if (currentObject.getString(key).equals(keyValue)) {
		    return currentObject;
		}
	    } catch (JSONException e) {
		JSONStuff.log.error(e);
	    }
	}
	return null;
    }

}
