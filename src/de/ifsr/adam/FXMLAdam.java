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
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Simon
 */
public class FXMLAdam extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml_adam.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Adam 5.0");
        stage.setScene(scene);
        
        try {
            String uristring = "file:" + System.getProperty("user.dir") + "/logo.png"; 
            stage.getIcons().add(new Image(uristring));  //TODO: Can I do this in FXML? 
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
