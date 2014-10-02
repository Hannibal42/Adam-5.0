/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Simon
 */
public class FXMLAdam extends Application {
    private static Stage stage; //TODO: Not sure if this is the right way to share the Stage
    private static Scene scene; //TODO: I realy shouldnt do this....
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml_adam.fxml"));
        this.stage = stage;
        
        Scene scene = new Scene(root, 230, 150);
        this.scene = scene;
        
        stage.setTitle("Adam 5.0");
        stage.setScene(scene);
        stage.show();
    }
    
    public static Stage getStage(){
        return stage; 
    }
    
    public static Scene getScene(){
        return scene;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
