/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;

import java.io.File;
import java.io.IOException;
import org.json.*;
import javafx.scene.Scene;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import javafx.scene.Group;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.chart.*; //Make this more specific
import java.util.ArrayList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
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
        
        
        for(int i = 0; i < resultReport.length(); i++){
            try {
                JSONObject currentObject = resultReport.getJSONObject(i);
                PieChart chart = generateDiagram(currentObject);
                //System.out.println(currentObject.getString("type"));
               // JSONObject type = getAnswerType(currentObject.getString("type"));
                //System.out.println(type);                    
                //System.out.println(generateObservableList(currentObject.getJSONObject("result"), type));
                ((Group) scene.getRoot()).getChildren().add(chart);
                
                WritableImage image = scene.snapshot(null);
                File outFile = new File ("test.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null)
                    , "png" , outFile);
                }
                 catch(IOException ex) {
                    System.out.println(ex.getMessage());
                }
       
            }
            
            catch(JSONException e){
                System.out.println(e); //TODO: Logging
            }
        }
        
        return true;
    }
    
    private PieChart generateDiagram(JSONObject question){ //TODO: Build in some logging for error handling.
        ObservableList<PieChart.Data> data = null;
        PieChart chart = null;
        try {
            //System.out.println(question.getString("question"));
            JSONObject surveyQuestion = getQuestion(question.getString("question"));
            //System.out.println(surveyQuestion);
            JSONObject answerType = getAnswerType(surveyQuestion.getString("type"));
            //System.out.println(answerType);
            JSONObject result = question.getJSONObject("result");
            data = generateObservableList(result,answerType);
            System.out.println(data);
            chart = new PieChart(data);
            chart.setTitle(surveyQuestion.getString("name"));
        }
        catch(JSONException | NullPointerException e){
            System.out.println(e);
        }
        return chart;
    }
    
    public ObservableList<PieChart.Data> generateObservableList(JSONObject result, JSONObject answerType){
        ArrayList<PieChart.Data> list= new ArrayList<PieChart.Data>();
        
        
        try{
            final JSONObject answers = answerType.getJSONObject("answers");
            final String [] fieldNames = JSONObject.getNames(result);
            System.out.println(answers);
            for(int i = 1; i < fieldNames.length; i++){
                
                System.out.println(fieldNames[i]);
                
                String answer = answers.getString(fieldNames[i]);
                System.out.println("answer:" + answer);
                
                Integer value = result.getInt(fieldNames[i]);
                System.out.println("value: " + value);
                
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
    
   
    
    
    //TODO: Remove
    

    
    
    public static void main(String[] args){ //TODO: Remove   
        //ImageGenerator gen = new ImageGenerator();
       // gen.generateImage(gen.importJSONArray(System.getProperty("user.dir") + "/resultReport.json"));
    }
    
}
