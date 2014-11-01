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
import javafx.scene.layout.GridPane;
/**
 *^
 * @author Simon
 */
public class TestScene extends Application {
    @Override
    public void start(Stage stage){
        stage.setTitle("Test");
        Group root = new Group();
        Scene scene = new Scene(root);
        GridPane gridPane = new GridPane();
        root.getChildren().add(gridPane);
        ImageGenerator gen = new ImageGenerator(scene);
        gen.generateImage(gen.importJSONArray(System.getProperty("user.dir") + "/resultReport.json"));
        
        stage.setScene(scene);
        stage.show();
    }
    
    
    public static void main(String[] args){
        launch(args);
    }
    
}
