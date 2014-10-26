/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.scene.image.WritableImage;
import javafx.scene.Group;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;

/**
 *
 * @author Simon
 */
public class PDFPrinterExample extends Application{
    
    @Override
    public void start(Stage stage){
        Scene scene = new Scene(new Group());
        stage.setTitle("Importd Fruits");
        stage.setWidth(500);
        stage.setHeight(500);
        
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Grapefruit",13),
                new PieChart.Data("Oranges", 25),
                new PieChart.Data("Plums", 10),
                new PieChart.Data("Pears", 22),
                new PieChart.Data("Apples", 30));
       final PieChart chart = new PieChart(pieChartData);
       chart.setTitle("Imported Fruits");
       
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
       
       stage.setScene(scene);
       stage.show();
    }
    
    public static void main(String[] args){
        launch(args);
    }
    
}
