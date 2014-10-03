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
    
    
    @FXML private Label label;
    @FXML private Text actionTarget;
    @FXML private final FileChooser importFileChooser = new FileChooser();
    @FXML private TextField selectImportFileTextField;
    @FXML private Text importActionTarget;
    @FXML private ChoiceBox<String> diagramSelectClassChoiceBox;
    
    
  
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @FXML
    private void handleImportButtonAction(ActionEvent event){
        importActionTarget.setText(" ");
	String filePath = selectImportFileTextField.getText();
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
		selectImportFileTextField.setText("Not a CSV file!");
            }
	}
	else {
            selectImportFileTextField.setText("Not a valid file path!");
        }
    }
    
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        actionTarget.setText("Sign in button pressed");
    }
    
    @FXML
    private void handleSelectFileButtonAction(ActionEvent event){
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = importFileChooser.showOpenDialog(stage);
            if (file != null){
                selectImportFileTextField.setText(file.getPath());
            } 
    }
    
    @FXML
    private void handleDiagramSelectClassChoiceBoxAction(ActionEvent event) {
        System.out.println("Hallo Welt!"); 
    }
    
    private ObservableList<String> getTableNames() {
        return FXCollections.observableArrayList(DBController.getInstance().getTableNames());
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        importFileChooser.setInitialDirectory(defaultDir);
        System.out.println(this.getTableNames());
        //System.out.println(diagramSelectClassChoiceBox.getItems());
        //diagramSelectClassChoiceBox.setItems(this.getTableNames());         
    }    
    
}
