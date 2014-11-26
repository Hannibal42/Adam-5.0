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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import org.json.*;
import org.apache.log4j.Logger;
        
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.chart.*; //Make this more specific
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javax.imageio.ImageIO;



/**
 * Main class for image output generation.
 * @author Simon
 */
public class ImageGenerator {
    
    private final static Charset ENCODING = StandardCharsets.UTF_8; 
    static Logger log = Logger.getLogger(ImageGenerator.class.getName());
    private final JSONArray survey;
    private final JSONArray answerTypes; 
    private final Scene scene;
    private String formatName;
    private Integer imageHeight;
    private Integer imageWidth;
    private String stylesheetURI;
    
    /**
     * Constructs a new ImageGenerator with a given scene and image output format.
     * @param formatName One of three basic formats .png, .jpeg or .gif
     * @param scene The scene where the image is printed in
     */
    ImageGenerator(Scene scene, String formatName){
        this.formatName = formatName;
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
        imageHeight = Formats.DINA4_HEIGHT;
        imageWidth = Formats.DINA4_WIDTH;
        this.scene = scene;
    }
    
    /**
     * Constructs a new ImageGenerator with a given scene and the default format .png 
     * @param scene The scene where the image is printed in
     */
    ImageGenerator(Scene scene){
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
        imageHeight = Formats.DINA4_HEIGHT;
        imageWidth = Formats.DINA4_WIDTH;
        formatName = "png";
        this.scene = scene;
    } 
    
    /**
     * Imports a JSONArray from a JSON file.
     * @param filePath The file path to the JSONArray you want to import.
     * @return 
     */
    public final JSONArray importJSONArray(String filePath){ //TODO: Make this private.
        Path path = Paths.get(filePath);
        String jsonStr = new String();
        JSONArray result = null; 
        
        try (Scanner scanner = new Scanner(path,ENCODING.name())){
            
            while(scanner.hasNext()){
                jsonStr += scanner.nextLine();
            }
            result = new JSONArray(jsonStr);
            
        }
        catch(IOException e){
            log.error("Failed to find JSON File at: " + filePath,e); 
        }
        catch(JSONException e){
            log.error("Not a valid JSON file at:" + filePath,e);   
        }
        return result;
    }   
    
    /**
     * Gets the value of the formatName.
     * @return 
     */
    public String getFormatName(){
        return formatName;
    }
    
    /**
     * Sets the value of the formatName.
     * @param formatName 
     */
    public void setFormatName(String formatName){
        this.formatName = formatName;
    }
    
    public Integer getImageHeight(){
        return this.imageHeight;
    }
    
    public void setImageHeight(Integer height){
        this.imageHeight = height;
    }
    
    public Integer getImageWidth(){
        return this.imageWidth;
    }
    
    public void setImageWidth(Integer width){
        this.imageWidth = width;
    }
    
    /**
     * Searches a JSONArray for an JSONObject with a specific key and keyValue.
     * @param array The JSONArray you want to search
     * @param key The name of the JSONObject field that is searched 
     * @param keyValue The Value of JSONObject field that is searched
     * @return returns the first JSONObject that has the field and the right value, returns null if no JSONobject is found
     */
    private JSONObject getSpecificObject(JSONArray array,String key, String keyValue){
        
        for(int i = 0; i < array.length(); i++){
            try{
                JSONObject currentObject = array.getJSONObject(i);
                if (currentObject.getString(key).equals(keyValue)){
                    return currentObject;
                }
            }
            catch(JSONException e){
                log.error(e);
            }
        }
        
        return null;
    }
    
    /**
     * Looks up the JSONObject answer type in answerTypes
     * @param answerType The answer type 
     * @return The JSONObject for the answer type, or null if the type was not found
     */
    private JSONObject getAnswerType(String answerType){
        return getSpecificObject(answerTypes,"type",answerType);
    }
    
    /**
     * Looks up the JSONObject question in survey
     * @param questionName The question
     * @return The JSONObject for the question, or null if the question was not found
     */
    private JSONObject getSurveyQuestion(String questionName){
        return getSpecificObject(survey,"name",questionName);
    }
    /**
     * validates if a question has all required fields, a valid answerType and a valid survey question.
     * @param question
     * @return 
     */
    private boolean isValidQuestion(JSONObject question){
        
        String questionId;
        
        if(question == null){
            return false;
        }
        
        try {
            questionId = question.getString("question");     
        }
        catch(JSONException e){
            return false;
        }
        try {
            question.getString("view");
        }
        catch(JSONException e){
            return false;
        }
        return isValidSurveyQuestion(questionId);
    }
    /**
     * 
     * @param questionId
     * @return 
     */
    private boolean isValidSurveyQuestion(String questionId){
        
        JSONObject surveyQuestion = getSurveyQuestion(questionId);
        String questionType;
        
        if (surveyQuestion == null){
            return false;
        }
        
        try {
            questionType = surveyQuestion.getString("type");
        }
        catch(JSONException e){
            return false;
        }

        return isValidAnswerType(questionType);
    }
    
    /**
     * 
     * @param questionType
     * @return 
     */
    private boolean isValidAnswerType(String questionType){
        
        JSONObject answerType = getAnswerType(questionType);
        return answerType != null;
    }

    private void calculateChartSize(Chart chart, Integer chartsPerRow, Integer chartsPerColumn){
        Double prefHeight, prefWidth;
        prefHeight = (imageHeight / chartsPerColumn) * 0.95;
        prefWidth = (imageWidth / chartsPerRow) * 0.95;
        chart.setPrefSize(prefWidth, prefHeight);
    }
    
    /**
     * Main method for generating an image out of a report with it results.
     * @param resultReport The report with it results
     * @return returns true if the generation and saving of the image was successful, false otherwise
     */
    public boolean generateImage(JSONArray resultReport){
        log.info("Image generation has started");
        
        GridPane gridPane = new GridPane();
        int x = 1;
        int y = 1;
        
        for(int i = 0; i < resultReport.length(); i++){
            JSONObject currentQuestion = null;
            String chartName = null;
            Chart chart;
            
            try {
                currentQuestion = resultReport.getJSONObject(i);
                chartName = currentQuestion.getString("view");
            }
            catch(JSONException | NullPointerException e){
                log.fatal(e);
            }
            
            if(chartName != null && isValidQuestion(currentQuestion)){
                switch(chartName){
                    case("bardiagram"): chart = generateBarChart(currentQuestion); break;
                    case("cakediagram"): chart = generatePieChart(currentQuestion); break;
                    default: chart = generateBarChart(currentQuestion); break;
                }
            calculateChartSize(chart, 3, 3);
            gridPane.add(chart, x, y); 
            if(x == 3){
                y += 1;
                x = 1; 
            }
            else{
                x += 1;
            }
            }
            else{
                log.debug("ChartName was null of " + currentQuestion);
            }
        }
        
        gridPane.setHgap(Formats.DINA4_WIDTH * 0.10);
        //gridPane.setVgap()
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        HBox hbox = new HBox();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPrefWidth(Formats.DINA4_WIDTH);
        gridPane.setPrefHeight(Formats.DINA4_HEIGHT);
        hbox.getChildren().add(gridPane);
        hbox.setPrefWidth(Formats.DINA4_WIDTH);
        hbox.setPrefHeight(Formats.DINA4_HEIGHT);       
        scrollPane.setContent(hbox);
        
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        
        scrollPane.setVmax(100.0);
        scrollPane.setPrefSize(width * 0.5, height * 0.8); //TODO

        //Puts the gridPane on the scene.
        File file = new File("C:\\Users\\Simon\\Desktop\\Adam 5.0\\data\\ChartStyle.css");
        scene.getStylesheets().add("file:///" + file.getAbsolutePath().replace("\\","/").replace(" ","%20")); //TODO: This is stupid, and i need something better to do this
 
        ((Group) scene.getRoot()).getChildren().addAll(scrollPane);
        
 
        log.info("End of image generation");
        return this.printToFile("test", hbox);
    }
    
    /**
     * Takes a snapshot of the scene and prints it to a file.
     * @return True if no IOException occurred
     */
    private boolean printToFile(String fileName,HBox hbox){
        Group root = new Group();
        Scene printScene = new Scene(root);
        //Puts the gridPane on the scene.
        File file = new File("C:\\Users\\Simon\\Desktop\\Adam 5.0\\data\\ChartStyle.css");
        printScene.getStylesheets().add("file:///" + file.getAbsolutePath().replace("\\","/").replace(" ","%20")); //TODO: This is stupid, and i need something better to do this
        ((Group) printScene.getRoot()).getChildren().addAll(hbox);
        WritableImage image = printScene.snapshot(null);
        File outFile = new File (fileName + "." + formatName);
        
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), formatName , outFile); 
            return true;
        }catch(IOException e){
            log.error(e);
            return false;
        }
    }
    
    /**
     * Generates a bar chart
     * @param question A question JSONObject of a report
     * @return 
     */
    private BarChart generateBarChart(JSONObject question){
        
        XYChart.Series data;
        BarChart<Number,String> chart;
        
        JSONObject surveyQuestion;
        JSONObject answerType;
        JSONObject result;
        
        try {
            
            surveyQuestion = getSurveyQuestion(question.getString("question"));
            try{
                answerType = getAnswerType(surveyQuestion.getString("type"));
            }
            catch(NullPointerException e){
                log.error( "The answerType of " +question+ " wasnt generated due to a Nullpointer");
                log.debug("",e);
                return null;
            }
   
            result = question.getJSONObject("result");
            data = generateDataBarChart(result,answerType);
            
            //Axis
            CategoryAxis yAxis = new CategoryAxis();
            yAxis.setAnimated(false); //Needs to be set, otherwise the labels are not printed to a file in the end.
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Votes"); //TODO: Multi Language support.
            xAxis.setAnimated(false); //Needs to be set, otherwise the labels are not printed to a file in the end.
            
            chart = new BarChart<>(xAxis,yAxis);
            chart.getData().addAll(data);
            chart.setTitle(surveyQuestion.getString("text"));
            chart.setLegendVisible(false);
        }
        catch(JSONException e){
            log.error(e);
            return null;
        }
      
        return chart;
    }
    
    /**
     * Creates the data series needed for creating a bar chart.
     * @param result The result JSONObject you wish to transform
     * @param answerType The answer type JSONObject of the question
     * @return 
     */
    private XYChart.Series generateDataBarChart(JSONObject result, JSONObject answerType){
        XYChart.Series series = new XYChart.Series();
        
        try{
            JSONObject answers;
            String[] fieldNames;
            
            try{
                answers = answerType.getJSONObject("answers");
                fieldNames = JSONObject.getNames(answers); 
            }
            catch(NullPointerException e){
                log.error("Missing JSONObject in result:" + result + " answerType: " + answerType);
                log.debug("",e);
                return series;
            }
            
            for(int i = 1; i < fieldNames.length; i++){ //i initialized with 1 to ignore the empty string result
                String answer = answers.getString(fieldNames[i]);
                Integer value;
                
                try {
                    value = result.getInt(fieldNames[i]);
                }
                catch(JSONException e){
                    value = 0;
                }
                
                series.getData().add(new XYChart.Data(value,answer));            
            }
        }
        catch(JSONException e){            
            log.error(e);
        }
        return series;
    }
    
    /**
     * Generates a pie chart.
     * @param question A question JSONObject of a report
     * @return 
     */
    private PieChart generatePieChart(JSONObject question){ //TODO: Build in some logging for error handling.
        ObservableList<PieChart.Data> data;
        PieChart chart;
        
        JSONObject surveyQuestion;
        JSONObject answerType;
        JSONObject result;
        
        try {
            surveyQuestion = getSurveyQuestion(question.getString("question"));
            try {
                answerType = getAnswerType(surveyQuestion.getString("type"));
            }
            catch(NullPointerException e){
                log.error( "The answerType of " +question+ " wasnt generated due to a NullPointer");
                log.debug("",e);
                return null;
            }
            
            result = question.getJSONObject("result");
            data = generateDataPieChart(result,answerType);
            
            chart = new PieChart(data);
            chart.setTitle(surveyQuestion.getString("text"));
        }
        catch(JSONException e){
            log.error(e);
            return null;
        }
        
        return chart;
    }
    
    
    /**
     * Creates the data list for a pie chart.
     * @param result The result JSONObject you wish to transform
     * @param answerType The answer type JSONObject of the question
     * @return 
     */
    private ObservableList<PieChart.Data> generateDataPieChart(JSONObject result, JSONObject answerType){
        ArrayList<PieChart.Data> list= new ArrayList<>();
       
        try{
            JSONObject answers;
            String[] fieldNames;
            
            try{
                answers = answerType.getJSONObject("answers");
                fieldNames = JSONObject.getNames(result);        
            }
            catch(NullPointerException e) {
                log.error("Missing JSONObject in result:" + result + " answerType: " + answerType);
                log.debug("",e);
                return FXCollections.observableArrayList(list);
            }
            
            for(int i = 1; i < fieldNames.length; i++){//i is 1 at the start to ignore the empty String result in the answers
                String answer = answers.getString(fieldNames[i]);
                Integer value = result.getInt(fieldNames[i]);
                list.add(new PieChart.Data(answer,value));
            }
        }
        catch(JSONException e){
            log.error(e);
        }
        
        return FXCollections.observableArrayList(list);
    }  
}
