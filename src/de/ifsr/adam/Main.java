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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

/**
 * The Main.
 * @author Simon
 */
public class Main extends Application {
    
    static Logger log = Logger.getLogger(Main.class.getName());
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml_adam.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Adam 5.0");
        stage.setResizable(false);
        stage.setScene(scene);
        
        try {
            String uristring = "file:" + System.getProperty("user.dir") + "/logo.png"; 
            stage.getIcons().add(new Image(uristring));  //TODO: Can I do this in FXML? 
        }
        catch(Exception e){
            log.error("Icon not loaded",e);
        }
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	Properties properties = new Properties();
	try {
	    properties.load(new FileReader(new File("C:\\Users\\Simon\\Desktop\\Adam 5.0\\properties\\log4j.properties"))); //TODO: Make this dynamic.
	} 
	catch (IOException ex) {
	    
	    Logger.getRootLogger().setLevel(Level.ERROR);
	}
	properties.setProperty("log", System.getProperty("user.dir") + "\\log");
	PropertyConfigurator.configure(properties); 
        launch(args);
    }
    
}
