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


/**
 *
 * @author Simon
 */
public class ImageGenerator {
    
    private final static Charset ENCODING = StandardCharsets.UTF_8; 
    private String formatName;
    final private JSONArray survey;
    final private JSONArray answerTypes; 
    
    ImageGenerator(String formatName){
        this.formatName = formatName;
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
    }
    
    ImageGenerator(){
        survey = importJSONArray(System.getProperty("user.dir") + "/survey.json" ); //TODO: Make this more dynamic
        answerTypes = importJSONArray(System.getProperty("user.dir") + "/answerTypes.json");//TODO: Make this more dynamic
        formatName = "png";
        System.out.println(survey.toString());
        System.out.println(answerTypes.toString());
    }
    
    private JSONArray importJSONArray(String filePath){
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
    
    
    
    public boolean generateImage(JSONArray resultReport){
        return true;
        
    }
    
    public static void main(String[] args){ //TODO: Remove
        ImageGenerator gen = new ImageGenerator();
    }
    
}
