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
import javafx.stage.FileChooser;
import javafx.scene.control.TextField;

/**
 *
 * @author Simon
 */
public class FXMLAdamController implements Initializable {
    
    @FXML private Label label;
    @FXML private Text actionTarget;
    @FXML private FileChooser importFileChooser;
    @FXML private TextField selectImportFileTextField;
    
    
  
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        actionTarget.setText("Sign in button pressed");
    }
    
    @FXML
    private void handleSelectFileButtonAction(ActionEvent event){
/*        File file = importFileChooser.showOpenDialog(FXMLAdam.getStage());
            if (file != null){
            selectImportFileTextField.setText(file.getPath());
            } */
        System.out.println("Test");
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
