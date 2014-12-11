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

import org.json.*;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

/**
 * Main class for report handling, can construct a report from a JSON file and has all functionality
 * for evaluating and saving the report.
 */
public class Report {

    static Logger log = Logger.getLogger(Report.class.getName());
    private final String tableName;
    private JSONArray report = null;
    private Boolean result = false;

    /**
     * Basic Report constructor, reads a prepared report from a JSON file.
     * 
     * @param reportPath Path to the JSON file
     * @param tableName the table you want to operate on.
     */
    public Report(String reportPath, String tableName) {
	this.tableName = tableName;
	this.report = JSONStuff.importJSONArray(reportPath);
    }

    /**
     * Evaluates the questions that are defined in the report. Should be used before any other
     * function call is made. Sets the result field true.
     */
    public void createResults() {
	JSONObject currentQuestion;

	for (int i = 0; i < report.length(); i++) {
	    try {
		currentQuestion = report.getJSONObject(i); //getJSONObject is reference but the doc says its by value
		currentQuestion.put("result", getQuestionResult(currentQuestion));
	    } catch (Exception e) {
		log.error("Could not create results for:" + report, e);
	    }
	}
	result = true;
    }

    /**
     * A function to evaluate one question in a report, used in createResults()
     *
     * @param question A single questions of a report
     * @return A JSONObject holding all the results.
     * @throws JSONException if no new JSONObject can be created
     */
    private JSONObject getQuestionResult(JSONObject question) throws JSONException {
	DBController ct = DBController.getInstance();
	ct.initDBConnection();

	String sqlQuery = "SELECT t1." + question.getString("question") + ",COUNT(t2." 
		+ question.getString("question") + ") ";
	sqlQuery += "FROM " + tableName + " t1 ";
	sqlQuery += "INNER JOIN " + tableName + " t2 ON t1.rowid=t2.rowid ";
	sqlQuery += "GROUP BY t1." + question.getString("question") + " ";
	sqlQuery += "ORDER BY t1." + question.getString("question") + " ASC;";

	/* Exampel Query
	 SELECT b1.F1,COUNT(b2.F1) FROM b3CSV b1
	 INNER JOIN b3CSV b2 ON b1.rowid = b2.rowid
	 GROUP BY b1.F1
	 ORDER BY b1.F1 ASC
	 */
	
	JSONObject resultJSON = new JSONObject();
	try {
	    Statement stmt = ct.getStatement();
	    ResultSet resultSet = stmt.executeQuery(sqlQuery);

	    while (resultSet.next()) {
		resultJSON.put(resultSet.getString(1), resultSet.getInt(2));
	    }
	} catch (SQLException e) {
	    log.error(e);
	}

	return resultJSON;
    }

    /**
     * @return The value of the report field.
     */
    public JSONArray getReport() {
	return this.report;
    }

    /**
     * Writes the report to a JSON file.
     *
     * @param path Path to the output directory
     * @param fileName the name of the new JSON file
     */
    public void writeReportToFile(String path, String fileName) {
	try {
	    File file = new File(path + "/" + fileName);
	    file.delete();
	    file.createNewFile();

	    PrintWriter writer = new PrintWriter(file);
	    writer.println(this.report.toString(1));
	    writer.close();
	} catch (IOException | JSONException e) {
	    log.error(e);
	}
    }

    /**
     * Writes the report to a JSON file.
     *
     * @param file The file you want to save to.
     */
    public void writeReportToFile(File file) {
	try {
	    file.delete();
	    file.createNewFile();

	    PrintWriter writer = new PrintWriter(file);
	    writer.println(this.report.toString(1));
	    writer.close();
	} catch (IOException | JSONException e) {
	    log.error(e);
	}
    }

    /**
     * @return The value of the result field.
     */
    public Boolean hasResult() {
	return this.result;
    }

    /**
     * Gives back the result JSONObject of a given question, createResults() must be called before
     * this function is used.
     *
     * @param question the question
     * @return The JSONObject holding the results for the question.
     */
    public JSONObject getResult(String question) {
	JSONObject curQuestion = null;
	String questionName = null;

	for (int i = 0; i < report.length(); i++) {

	    try {
		curQuestion = report.getJSONObject(i);
		questionName = curQuestion.getString("question");
	    } catch (JSONException | NullPointerException e) {
		log.error(e);
	    }

	    if (question.equals(questionName)) {
		try {
		    return curQuestion.getJSONObject("result");
		} catch (JSONException e) {
		    log.error(e);
		}
	    }

	}
	return null;
    }

    /**
     * Generates a preview image of the diagrams
     * @param surveyPath The path to the survey JSON File
     * @param answerTypesPath The path to the answerTypes JSON File
     * @param stylesheetPath The path to the style sheet 
     * @return The preview scene with diagrams and stuff.
     */
    public Scene generateImage(String surveyPath, String answerTypesPath, String stylesheetPath) {
	Group root = new Group();
	Scene scene = new Scene(root);
	GridPane gridPane = new GridPane();
	root.getChildren().add(gridPane);
	ImageGenerator gen = new ImageGenerator(scene,surveyPath,answerTypesPath,stylesheetPath);
	gen.generateImage(report);
	return scene;
    }
}
