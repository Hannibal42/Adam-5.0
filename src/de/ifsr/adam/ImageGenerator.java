/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;

import org.json.*;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;



/**
 *
 * @author Simon
 */
public class ImageGenerator {
    
    private final static Charset ENCODING = StandardCharsets.UTF_8; 
    private String formatName;
    final private JSONArray survey;
    final private JSONArray answerTypes; 
    private Scene scene;
    
  
    ImageGenerator(String formatName){
        this.formatName = formatName;
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
    }
    
    ImageGenerator(Scene scene){
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
        formatName = "png";
        this.scene = scene;
    } 
    
    public JSONArray importJSONArray(String filePath){ //TODO: Make this private.
        Path path = Paths.get(filePath);
        String jsonStr = new String();
        JSONArray result = null; 
        
        try (Scanner scanner = new Scanner(path,ENCODING.name())){
            
            while(scanner.hasNext()){
                jsonStr += scanner.nextLine();
            }
            result = new JSONArray(jsonStr);
            
        }
        catch(Exception e){
           System.out.println(e); //Log this.
        }
        return result;
    }   
    
    public String getFormatName(){
        return formatName;
    }
    
    public void setFormatName(String formatName){
        this.formatName = formatName;
    }
    
    //returns null if the Object is not in the Array
    private JSONObject getSpecificObject(JSONArray array,String key, String keyValue){
        
        for(int i = 0; i < array.length(); i++){
            try{
                JSONObject currentObject = array.getJSONObject(i);
                if (currentObject.getString(key).equals(keyValue)){
                    return currentObject;
                }
            }
            catch(JSONException e){
                System.out.println(e);
            }
        }
        
        return null;
    }
    
    public JSONObject getAnswerType(String answerType){
        return getSpecificObject(answerTypes,"type",answerType);
    }
    
    public JSONObject getQuestion(String questionName){
        return getSpecificObject(survey,"name",questionName);
    }
      
    public boolean generateImage(JSONArray resultReport){ //TODO: Make it private
        
        GridPane gridPane = new GridPane();
        int x = 1;
        int y = 1;
        for(int i = 0; i < resultReport.length(); i++){
            try {
                JSONObject currentObject = resultReport.getJSONObject(i);
                Chart chart = generatePieChart(currentObject);
                //System.out.println(currentObject.getString("type"));
               // JSONObject type = getAnswerType(currentObject.getString("type"));
                //System.out.println(type);                    
                //System.out.println(generateObservableList(currentObject.getJSONObject("result"), type))
                gridPane.add(chart, x, y);
                
                if(x == 3){
                    y += 1;
                    x = 1; 
                }
                else{
                    x += 1;
                }
       
            }
            
            catch(JSONException e){
                System.out.println(e); //TODO: Logging
            }
        }
        
        ((Group) scene.getRoot()).getChildren().add(gridPane);
 
        return this.printToFile();
    }
    
    private boolean printToFile(){
        WritableImage image = scene.snapshot(null);
        File outFile = new File ("test." + formatName);
        
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), formatName , outFile); 
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }
    
    private BarChart generateBarChart(JSONObject question){
        XYChart.Series data;
        BarChart<Number,String> chart = null;
        
        try {
            JSONObject surveyQuestion = getQuestion(question.getString("question"));
            JSONObject answerType = getAnswerType(surveyQuestion.getString("type"));
            JSONObject result = question.getJSONObject("result");
            data = generateObservableListBarChart(result,answerType);
            CategoryAxis yAxis = new CategoryAxis();
            NumberAxis xAxis = new NumberAxis();
            chart = new BarChart<Number,String>(xAxis,yAxis);
            //TODO: set the Labels of the Axis
            chart.getData().addAll(data);
            chart.setTitle(surveyQuestion.getString("text"));
            chart.setLegendVisible(false);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
      
        return chart;
    }
    
    private XYChart.Series generateObservableListBarChart(JSONObject result, JSONObject answerType){
        XYChart.Series series = new XYChart.Series();
        
        try{
            final JSONObject answers = answerType.getJSONObject("answers");
            final String[] fieldNames = JSONObject.getNames(answers);
            
            for(int i = 1; i < fieldNames.length; i++){
                System.out.println(fieldNames[i]);
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
            
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        return series;
    }
    
    private PieChart generatePieChart(JSONObject question){ //TODO: Build in some logging for error handling.
        ObservableList<PieChart.Data> data;
        PieChart chart = null;
        
        try {
            //System.out.println(question.getString("question"));
            JSONObject surveyQuestion = getQuestion(question.getString("question"));
            //System.out.println(surveyQuestion);
            JSONObject answerType = getAnswerType(surveyQuestion.getString("type"));
            //System.out.println(answerType);
            JSONObject result = question.getJSONObject("result");
            data = generateObservableListPieChart(result,answerType);
            //System.out.println(data);
            chart = new PieChart(data);
            chart.setTitle(surveyQuestion.getString("text"));
        }
        catch(JSONException | NullPointerException e){
            System.out.println(e);
        }
        
        return chart;
    }
    
    private ObservableList<PieChart.Data> generateObservableListPieChart(JSONObject result, JSONObject answerType){
        ArrayList<PieChart.Data> list= new ArrayList<PieChart.Data>();
       
        try{
            final JSONObject answers = answerType.getJSONObject("answers");
            final String[] fieldNames = JSONObject.getNames(result);
            //System.out.println(answers);
            for(int i = 1; i < fieldNames.length; i++){//i is 1 at the start to ignore the empty String result in the answers
                
                //System.out.println(fieldNames[i]);
                
                String answer = answers.getString(fieldNames[i]);
               // System.out.println("answer:" + answer);
                
                Integer value = result.getInt(fieldNames[i]);
                //System.out.println("value: " + value);
                
                list.add(new PieChart.Data(answer,value));
            }
        }
        catch(NullPointerException e) {
            System.out.println(e + " Es wurde kein Result fuer die Frage erstellt."); //Log
        }
        catch(JSONException e){
            System.out.println(e);
        }
        
        return FXCollections.observableArrayList(list);
    }

    
    
    public static void main(String[] args){ //TODO: Remove   
        //ImageGenerator gen = new ImageGenerator();
       // gen.generateImage(gen.importJSONArray(System.getProperty("user.dir") + "/resultReport.json"));
    }
    
}
