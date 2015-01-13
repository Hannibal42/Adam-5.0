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

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import org.json.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.chart.*; //Make this more specific
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;

/**
 * Main class for image output generation.
 *
 * @author Simon
 */
public class ImageGenerator {

    static Logger log = Logger.getLogger(ImageGenerator.class.getName());
    private final JSONArray survey;
    private final JSONArray answerTypes;
    private final Scene scene;
    private String formatName;
    private Integer imageHeight;
    private Integer imageWidth;
    private URI stylesheetURI;

    /**
     * Constructs a new ImageGenerator with a given scene and image output format. Default
     * Constructor.
     *
     * @param formatName One of three basic formats .png, .jpeg or .gif
     * @param scene The scene where the image is printed in
     */
    ImageGenerator(String surveyPath, String answerTypesPath, String stylesheetPath) {
	survey = JSONStuff.importJSONArray(surveyPath);
	answerTypes = JSONStuff.importJSONArray(answerTypesPath);//TODO: Make this more dynamic
	this.setStylesheet(stylesheetPath); //TODO: Make this more dynamic
	imageHeight = Formats.DINA4_HEIGHT;
	imageWidth = Formats.DINA4_WIDTH;
	this.scene = new Scene(new Group());
	formatName = "png";
    }

    //TODO:Remove

    /**
     * Constructs a new ImageGenerator with a given scene and image output format. Only for quick
     * testing.
     *
     * @param formatName One of three basic formats .png, .jpeg or .gif
     * @param scene The scene where the image is printed in
     */
    ImageGenerator(Scene scene, String formatName) {
	survey = JSONStuff.importJSONArray(System.getProperty("user.dir") + "/survey.json");
	answerTypes = JSONStuff.importJSONArray(System.getProperty("user.dir")
		+ "/answerTypes.json");
	this.setStylesheet("C:\\Users\\Simon\\Desktop\\Adam 5.0\\data\\ChartStyle.css");
	imageHeight = Formats.DINA4_HEIGHT;
	imageWidth = Formats.DINA4_WIDTH;
	this.scene = scene;
	this.formatName = formatName;
    }

    //TODO: Remove

    /**
     * Constructs a new ImageGenerator with a given scene and the default format .png Only for quick
     * testing.
     *
     * @param scene The scene where the image is printed in
     */
    ImageGenerator(Scene scene) {
	survey = JSONStuff.importJSONArray(System.getProperty("user.dir") + "/survey.json");
	answerTypes = JSONStuff.importJSONArray(System.getProperty("user.dir")
		+ "/answerTypes.json");
	this.setStylesheet("C:\\Users\\Simon\\Desktop\\Adam 5.0\\data\\ChartStyle.css");
	imageHeight = Formats.DINA4_HEIGHT;
	imageWidth = Formats.DINA4_WIDTH;
	formatName = "png";
	this.scene = scene;
    }

    /**
     * Gets the value of the formatName.
     *
     * @return
     */
    public String getFormatName() {
	return formatName;
    }

    /**
     *
     * @return The Width of the output image.
     */
    public Integer getImageWidth() {
	return this.imageWidth;
    }

    /**
     *
     * @return The height of the output image.
     */
    public Integer getImageHeight() {
	return this.imageHeight;
    }

    /**
     * Sets the value of the formatName.
     *
     * @param formatName
     */
    public void setFormatName(String formatName) {
	this.formatName = formatName;
    }

    /**
     * Sets the height of the output image. Use the static member of Formats for this.
     *
     * @param height
     */
    public void setImageHeight(Integer height) {
	this.imageHeight = height;
    }

    /**
     * Sets the width of the output image. Use the static members of Formats for this.
     *
     * @param width
     */
    public void setImageWidth(Integer width) {
	this.imageWidth = width;
    }

    /**
     * Sets the cascading style sheet for the diagrams
     *
     * @param filePath
     */
    public final void setStylesheet(String filePath) {
	File file = new File(filePath);
	this.stylesheetURI = file.toURI();
    }

    /**
     * Main method for generating an image out of a report with it results.
     *
     * @param resultReport The report with it results
     * @return returns true if the generation and saving of the image was successful, false
     * otherwise
     */
    public Scene generateImage(JSONArray resultReport) {
	log.info("Image generation has started");

	ArrayList<Chart> chartList = generateCharts(resultReport);
	ArrayList<GridPane> gridPaneList = makeLayout(chartList, 3, 2);

	VBox vbox = new VBox();
	vbox.getChildren().addAll(gridPaneList);
	vbox.setPrefHeight(imageHeight * gridPaneList.size());

	ScrollPane scrollPane = new ScrollPane();
	scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
	scrollPane.setContent(vbox);

	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	int width = gd.getDisplayMode().getWidth();
	int height = gd.getDisplayMode().getHeight();

	scrollPane.setVmax(100.0);
	scrollPane.setPrefSize(width * 0.65, height * 0.8); //TODO

	((Group) scene.getRoot()).getChildren().add(scrollPane);
	scene.getStylesheets().add(this.stylesheetURI.toString());
	
	//The Observer for resizing the scrollpane when the window changes.
	scene.widthProperty().addListener(new ChangeListener<Number>() {
	    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		scrollPane.setPrefWidth(newSceneWidth.doubleValue());
	    }
	});
	
	//The Observer for resizing the scrollpane when the window changes.
	scene.heightProperty().addListener(new ChangeListener<Number>() {
	    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		scrollPane.setPrefHeight(newSceneHeight.doubleValue());
	    }
	});

	log.info("End of image generation");
	this.printToPDF("test", vbox);
	return scene;
    }

    /**
     * Puts the charts in chartList into GridPanes as specified by chartsPerColumn and chartsPerRow.
     *
     * @param chartList The ArrayList with the charts.
     * @param chartsPerColumn Number of charts that are in one column.
     * @param chartsPerRow Number of charts that are in one row.
     * @return The list of GridPanes that were generated.
     */
    private ArrayList<GridPane> makeLayout(ArrayList<Chart> chartList, Integer chartsPerColumn, Integer chartsPerRow) {
	ArrayList<GridPane> gridPaneList = new ArrayList<>();
	GridPane gridPane = new GridPane();
	int rowIndex = 0, columnIndex = 0;
	Iterator<Chart> iter = chartList.iterator();

	//int i = 0;//TODO: Remove 
	while (iter.hasNext()) {
	    //System.out.println("Run: "+ i + " columnIndex: " + columnIndex + " rowIndex: " + rowIndex);//TODO: Remove 
	    //i++;//TODO: Remove 

	    Chart chart = iter.next();
	    calculateChartSize(chart, chartsPerColumn, chartsPerRow);
	    gridPane.add(chart, columnIndex, rowIndex);

	    columnIndex++;

	    //Case: Row is full go to next row
	    if (columnIndex >= chartsPerRow) {
		columnIndex = 0;
		rowIndex++;
	    }

	    //Case: Page is full start a new GridPane
	    if (rowIndex >= chartsPerColumn) {
		//The Layout for the gridPane
		gridPane.setHgap(imageWidth * 0.10);
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setPrefWidth(imageWidth);
		gridPane.setPrefHeight(imageHeight);

		gridPaneList.add(gridPane);
		gridPane = new GridPane();
		rowIndex = 0;
	    }
	}

	//This needs to be check, or the last page can be empty
	if (rowIndex != 0 || columnIndex != 0) {
	    gridPaneList.add(gridPane);
	}

	return gridPaneList;
    }

    /**
     * Generates a list of Charts out of a Report. Right now it can only create Cake- and
     * Bardiagrams.
     *
     * @param resultReport The JSON representation of a report.
     * @return
     */
    private ArrayList<Chart> generateCharts(JSONArray resultReport) {
	ArrayList<Chart> chartList = new ArrayList<>();

	for (int i = 0; i < resultReport.length(); i++) {
	    JSONObject currentQuestion = null;
	    String chartName = null;
	    Chart chart;

	    try {
		currentQuestion = resultReport.getJSONObject(i);
		chartName = currentQuestion.getString("view");
	    } catch (JSONException | NullPointerException e) {
		log.fatal(e);
	    }

	    if (chartName != null) {
		if(isValidQuestion(currentQuestion)){
		    switch (chartName) {
			case ("bardiagram"):
			    chart = generateBarChart(currentQuestion);
			    break;
			case ("cakediagram"):
			    chart = generatePieChart(currentQuestion);
			    break;
			default:
			    chart = generateBarChart(currentQuestion);
			    break;
		    }
		    chartList.add(chart);
		} else {
		    log.debug("Not a valid Question :" + currentQuestion);
		}
	    } else {
		log.debug("ChartName was null of " + currentQuestion);
	    }
	}

	return chartList;
    }

       /**
     * Takes a snapshot of the Pane and prints it to a pdf.
     *
     * @return True if no IOException occurred
     */
    private boolean printToPDF(String fileName, Pane pane) {
	Group root = new Group();
	Scene printScene = new Scene(root);
	printScene.getStylesheets().add(this.stylesheetURI.toString());
	
	GridPane gridPane;
	try{
	    ObservableList<Node> panes = pane.getChildren();
	    System.out.println(panes.size());
	    gridPane = (GridPane)panes.get(1);
	}catch(Exception e){
	    log.error(e);
	    return false;
	}
	

	((Group) printScene.getRoot()).getChildren().addAll(gridPane);
	WritableImage image = printScene.snapshot(null);
	
	File outFile = new File(fileName + "." + "pdf");
	PDDocument doc = new PDDocument();
	PDPage page = new PDPage();
	doc.addPage(page);
	
	try{
	    PDPageContentStream contentStream = new PDPageContentStream(doc,page,true, false);
	    
	    BufferedImage bufImage = SwingFXUtils.fromFXImage(image, null);
	    PDPixelMap pixelMap = new PDPixelMap(doc,bufImage);
	    Dimension dim = new Dimension((int)page.getMediaBox().getWidth(),(int)page.getMediaBox().getHeight());
	    
	    contentStream.drawXObject(pixelMap, 0, 0, dim.width, dim.height);
	    contentStream.close();
	    doc.save(outFile);
	    return true;
	}catch(IOException | COSVisitorException e){
	    log.error(e);
	    return false;
	}
	finally{
	    pane.getChildren().add(gridPane);
	}
    } 
    
    /**
     * Takes a snapshot of the Pane and prints it to a file.
     *
     * @return True if no IOException occurred
     */
    private boolean printToFile(String fileName, Pane pane) {
	Group root = new Group();
	Scene printScene = new Scene(root);

	printScene.getStylesheets().add(this.stylesheetURI.toString());

	((Group) printScene.getRoot()).getChildren().addAll(pane);
	WritableImage image = printScene.snapshot(null);
	File outFile = new File(fileName + "." + formatName);

	try {
	    ImageIO.write(SwingFXUtils.fromFXImage(image, null), formatName, outFile);
	    return true;
	} catch (IOException e) {
	    log.error(e);
	    return false;
	}
    }

    /**
     * Generates a bar chart
     *
     * @param question A question JSONObject of a report
     * @return
     */
    private BarChart generateBarChart(JSONObject question) {

	XYChart.Series data;
	BarChart<Number, String> chart;

	JSONObject surveyQuestion;
	JSONObject answerType;
	JSONObject result;

	try {

	    surveyQuestion = getSurveyQuestion(question.getString("question"));
	    try {
		answerType = getAnswerType(surveyQuestion.getString("type"));
	    } catch (NullPointerException e) {
		log.error("The answerType of " + question + " wasnt generated due to a Nullpointer");
		log.debug("", e);
		return null;
	    }

	    result = question.getJSONObject("result");
	    data = generateDataBarChart(result, answerType);

	    //Axis
	    CategoryAxis yAxis = new CategoryAxis();
	    yAxis.setAnimated(false); //Needs to be set, otherwise the labels are not printed to a file in the end.
	    NumberAxis xAxis = new NumberAxis();
	    xAxis.setLabel("Votes"); //TODO: Multi Language support.
	    xAxis.setAnimated(false); //Needs to be set, otherwise the labels are not printed to a file in the end.

	    chart = new BarChart<>(xAxis, yAxis);
	    chart.getData().addAll(data);
	    chart.setTitle(surveyQuestion.getString("text"));
	    chart.setLegendVisible(false);
	} catch (JSONException e) {
	    log.error(e);
	    return null;
	}

	return chart;
    }

    /**
     * Creates the data series needed for creating a bar chart.
     *
     * @param result The result JSONObject you wish to transform
     * @param answerType The answer type JSONObject of the question
     * @return
     */
    private XYChart.Series generateDataBarChart(JSONObject result, JSONObject answerType) {
	XYChart.Series series = new XYChart.Series();

	try {
	    JSONObject answers;
	    String[] fieldNames;

	    try {
		answers = answerType.getJSONObject("answers");
		fieldNames = JSONObject.getNames(answers);
	    } catch (NullPointerException e) {
		log.error("Missing JSONObject in result:" + result + " answerType: " + answerType);
		log.debug("", e);
		return series;
	    }

	    for (int i = 1; i < fieldNames.length; i++) { //i initialized with 1 to ignore the empty string result
		String answer = answers.getString(fieldNames[i]);
		Integer value;

		try {
		    value = result.getInt(fieldNames[i]);
		} catch (JSONException e) {
		    value = 0;
		}

		series.getData().add(new XYChart.Data(value, answer));
	    }
	} catch (JSONException e) {
	    log.error(e);
	}
	return series;
    }

    /**
     * Generates a pie chart.
     *
     * @param question A question JSONObject of a report
     * @return
     */
    private PieChart generatePieChart(JSONObject question) { //TODO: Build in some logging for error handling.
	ObservableList<PieChart.Data> data;
	PieChart chart;

	JSONObject surveyQuestion;
	JSONObject answerType;
	JSONObject result;

	try {
	    surveyQuestion = getSurveyQuestion(question.getString("question"));
	    try {
		answerType = getAnswerType(surveyQuestion.getString("type"));
	    } catch (NullPointerException e) {
		log.error("The answerType of " + question + " wasnt generated due to a NullPointer");
		log.debug("", e);
		return null;
	    }

	    result = question.getJSONObject("result");
	    data = generateDataPieChart(result, answerType);

	    chart = new PieChart(data);
	    chart.setTitle(surveyQuestion.getString("text"));
	} catch (JSONException e) {
	    log.error(e);
	    return null;
	}

	return chart;
    }

    /**
     * Creates the data list for a pie chart.
     *
     * @param result The result JSONObject you wish to transform
     * @param answerType The answer type JSONObject of the question
     * @return
     */
    private ObservableList<PieChart.Data> generateDataPieChart(JSONObject result, JSONObject answerType) {
	ArrayList<PieChart.Data> list = new ArrayList<>();

	try {
	    JSONObject answers;
	    String[] fieldNames;

	    try {
		answers = answerType.getJSONObject("answers");
		fieldNames = JSONObject.getNames(result);
	    } catch (NullPointerException e) {
		log.error("Missing JSONObject in result:" + result + " answerType: " + answerType);
		log.debug("", e);
		return FXCollections.observableArrayList(list);
	    }

	    for (int i = 1; i < fieldNames.length; i++) {//i is 1 at the start to ignore the empty String result in the answers
		String answer = answers.getString(fieldNames[i]);
		Integer value = result.getInt(fieldNames[i]);
		list.add(new PieChart.Data(answer, value));
	    }
	} catch (JSONException e) {
	    log.error(e);
	}

	return FXCollections.observableArrayList(list);
    }

    /**
     * Looks up the JSONObject answer type in answerTypes
     *
     * @param answerType The answer type
     * @return The JSONObject for the answer type, or null if the type was not found
     */
    private JSONObject getAnswerType(String answerType) {
	return JSONStuff.getSpecificObject(answerTypes, "type", answerType);
    }

    /**
     * Looks up the JSONObject question in survey
     *
     * @param questionName The question
     * @return The JSONObject for the question, or null if the question was not found
     */
    private JSONObject getSurveyQuestion(String questionName) {
	return JSONStuff.getSpecificObject(survey, "name", questionName);
    }

    /**
     * validates if a question has all required fields, a valid answerType and a valid survey
     * question.
     *
     * @param question
     * @return
     */
    private boolean isValidQuestion(JSONObject question) {

	String questionId;

	if (question == null) {
	    return false;
	}

	try {
	    questionId = question.getString("question");
	} catch (JSONException e) {
	    return false;
	}
	try {
	    question.getString("view");
	} catch (JSONException e) {
	    return false;
	}
	return isValidSurveyQuestion(questionId);
    }

    /**
     *
     * @param questionId
     * @return
     */
    private boolean isValidSurveyQuestion(String questionId) {

	JSONObject surveyQuestion = getSurveyQuestion(questionId);
	String questionType;

	if (surveyQuestion == null) {
	    return false;
	}

	try {
	    questionType = surveyQuestion.getString("type");
	} catch (JSONException e) {
	    return false;
	}

	return isValidAnswerType(questionType);
    }

    /**
     *
     * @param questionType
     * @return
     */
    private boolean isValidAnswerType(String questionType) {

	JSONObject answerType = getAnswerType(questionType);
	return answerType != null;
    }

    /**
     * Calculates the right size of the chart for the given parameters.
     *
     * @param chart
     * @param chartsPerRow
     * @param chartsPerColumn
     */
    private void calculateChartSize(Chart chart, Integer chartsPerColumn, Integer chartsPerRow) {
	Double prefHeight, prefWidth;
	prefHeight = (imageHeight / chartsPerColumn) * 0.95;
	prefWidth = (imageWidth / chartsPerRow) * 0.95;
	chart.setPrefSize(prefWidth, prefHeight);
    }
}
