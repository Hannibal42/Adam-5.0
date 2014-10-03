/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.ifsr.adam;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.ChoiceBox;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

/**
 *
 * @author Simon
 */
public class FXMLAdamController implements Initializable {
    @FXML private Parent root; //I am rooooooooot!
    private final CSVReader reader = new CSVReader();
    private final File defaultDir = new File(System.getProperty("user.dir"));
    private final FileChooser importFileChooser = new FileChooser();
    private final FileChooser diagramReportChooser = new FileChooser();
    
    @FXML private TextField importSelectFileTextField;
    @FXML private TextField diagramSelectReportTextField;
    @FXML private Text importActionTarget;
    @FXML private Text diagramActionTarget;
    @FXML private ChoiceBox<String> diagramSelectClassChoiceBox;
    
    @FXML
    private void handleImportButtonAction(ActionEvent event){
        importActionTarget.setText(" ");
	String filePath = importSelectFileTextField.getText();
	if (reader.isFilePath(filePath)){
            if(reader.isCSVFile(filePath)){
                
		reader.insertCSVFile(filePath);
                
                importActionTarget.setFill(Color.BLUE);
                importActionTarget.setText("Import succesfull!"); //TODO Add some succesfull test.
               
                File file = new File(filePath);
                file = new File(file.getParent());
                importFileChooser.setInitialDirectory(file);
            }
            else{
		importActionTarget.setText("Not a CSV file!");
            }
	}
	else {
            importActionTarget.setText("Not a valid file path!");
        }
    }
    
    @FXML
    private void handleDiagramSaveButtonAction(ActionEvent event) {
        diagramActionTarget.setText(" ");
        String filePath = diagramSelectReportTextField.getText();
        String tableName = diagramSelectClassChoiceBox.getValue();
        Report report;
        if(reader.isFilePath(filePath)) {
            if(reader.isJSONFile(filePath)) {
                report = new Report(filePath,tableName);
                report.createResults();
                
                Stage stage = (Stage) root.getScene().getWindow();
                File file = diagramReportChooser.showSaveDialog(stage);
                report.writeReportToFile(new File(file.getAbsolutePath() + ".json")); //TODO: Change this.
                
                diagramActionTarget.setFill(Color.BLUE);
                diagramActionTarget.setText("Diagrams generated!"); //TODO Add some succesfull test.
                
                file = new File(filePath);
                file = new File(file.getParent());
                diagramReportChooser.setInitialDirectory(file);
            }
            else {
                diagramActionTarget.setText("Not a JSON file!");
            }
        }
        else{
            diagramActionTarget.setText("Not a valid file path!");
        }
    }
    
    @FXML
    private void handleImportSelectFileButtonAction(ActionEvent event){
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = importFileChooser.showOpenDialog(stage);
            if (file != null){
                importSelectFileTextField.setText(file.getPath());
            } 
    }
    
    @FXML
    private void handleDiagramSelectReportButtonAction(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = diagramReportChooser.showOpenDialog(stage);
            if (file != null){
                diagramSelectReportTextField.setText(file.getPath());
            }  
    }
    
    private ObservableList<String> getTableNames() {
        ObservableList<String> tableNames;
        tableNames = FXCollections.observableList(DBController.getInstance().getTableNames());
        return tableNames;      
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        importFileChooser.setInitialDirectory(defaultDir);
        diagramReportChooser.setInitialDirectory(defaultDir);
        diagramSelectClassChoiceBox.setItems(this.getTableNames());         
    }    
    
}
