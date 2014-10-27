/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author Simon
 */
public class TestScene extends Application {
    @Override
    public void start(Stage stage){
        stage.setTitle("Importd Fruits");
        stage.setWidth(500);
        stage.setHeight(500);
        Scene scene = new Scene(new Group());
        ImageGenerator gen = new ImageGenerator(scene);
        gen.generateImage(gen.importJSONArray(System.getProperty("user.dir") + "/resultReport.json"));
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args){
        launch(args);
    }
    
}