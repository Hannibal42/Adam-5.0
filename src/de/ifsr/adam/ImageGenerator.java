/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;

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
import javafx.scene.chart.Chart;
import javafx.scene.chart.*; //Make this more specific
import java.util.ArrayList;


/**
 *
 * @author Simon
 */
public class ImageGenerator extends Application {
    
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
    
    ImageGenerator(){
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
        formatName = "png";
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
            return null;
        }
        
        
        return null;
    }
    
    private JSONObject getAnswerType(String answerType){
        return getSpecificObject(answerTypes,"type",answerType);
    }
    
    private JSONObject getQuestion(String questionName){
        return getSpecificObject(survey,"name",questionName);
    }
      
    public boolean generateImage(JSONArray resultReport){
        //scene = new Scene(new Group());
        
        for(int i = 0; i < resultReport.length(); i++){
            try {
                JSONObject currentObject = resultReport.getJSONObject(i);
                //Chart chart = generateDiagram(currentObject);
                generateObservableList(currentObject.getJSONObject("result"), null);
                //((Group) scene.getRoot()).getChildren().add(chart);
            }
            catch(JSONException e){
                System.out.println(e); //TODO: Logging
            }
        }
        
        return true;
    }
    
    private Chart generateDiagram(JSONObject question){
        
        
        return null;
    }
    
    public ObservableList<PieChart.Data> generateObservableList(JSONObject result, JSONObject answerType){
        ArrayList<PieChart.Data> list= new ArrayList<PieChart.Data>();
        
        try{
            String [] fieldNames = JSONObject.getNames(result);
            System.out.println(fieldNames[1]);
            for(int i = 1; i < fieldNames.length; i++){
                String answer = answerType.getJSONArray("answers").getString((int)fieldNames[i]); //Change the answerType array to an object!
                list.add(new PieChart.Data(fieldNames[i],));
            }
        }
        catch(NullPointerException e) {
            System.out.println(e + " Es wurde kein Result fuer die Frage erstellt."); //Log
        }
        return null;
    }
    
   
    
    
    //TODO: Remove
    
    @Override
    public void start(Stage stage){
        stage.setTitle("Importd Fruits");
        stage.setWidth(500);
        stage.setHeight(500);
        
        
    
    }
    
    
    public static void main(String[] args){ //TODO: Remove
        ImageGenerator gen = new ImageGenerator();
        gen.generateImage(gen.importJSONArray(System.getProperty("user.dir") + "/resultReport.json"));
    }
    
}
