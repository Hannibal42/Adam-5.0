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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    private final FileChooser optionsJSONChooser = new FileChooser();
    private final FileChooser optionsCSSChooser = new FileChooser();
    private final DirectoryChooser importDirectoryChooser = new DirectoryChooser();
    
    @FXML private TextField importSelectFileTextField;
    @FXML private TextField diagramSelectReportTextField;
    @FXML private TextField importSelectDirectoryTextField;
    @FXML private TextField optionsSelectAnswerTypesTextField;
    @FXML private TextField optionsSelectSurveyTextField;
    @FXML private TextField optionsSelectChartStyleTextField;
    @FXML private Text importActionTarget;
    @FXML private Text diagramActionTarget;
    @FXML private ChoiceBox<String> diagramSelectClassChoiceBox;
    
    
    @FXML private void handleOptionsSelectAnswerTypesButtonAction(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = optionsJSONChooser.showOpenDialog(stage);
        if (file != null){
            optionsSelectAnswerTypesTextField.setText(file.getPath());
        }     
    }
    
    @FXML private void handleOptionsSelectSurveyButtonAction(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = optionsJSONChooser.showOpenDialog(stage);
        if (file != null){
            optionsSelectSurveyTextField.setText(file.getPath());
        }        
    }
    
    @FXML private void handleOptionsSelectChartStyleButtonAction(ActionEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = optionsCSSChooser.showOpenDialog(stage);
        if (file != null){
            optionsSelectChartStyleTextField.setText(file.getPath());
        }        
    }
    
    @FXML
    private void handleImportButtonAction(ActionEvent event){
        String dirPath = importSelectDirectoryTextField.getText();
        String filePath = importSelectFileTextField.getText();
        
        if((dirPath.equals("")) && (filePath.equals(""))){
            importActionTarget.setText("Select import data");
        }
        if((!dirPath.equals("")) && (!filePath.equals(""))) {
            importActionTarget.setText("Choose only one");
        }
        if(!dirPath.equals("")){
            importDirectory(event); 
        }
        if(!filePath.equals("")){
            importSingleFile(event);
        }
        diagramSelectClassChoiceBox.setItems(this.getTableNames()); // Refresh the diagram table choice box
    }
    
    private void importSingleFile(ActionEvent event){
        importActionTarget.setText(" ");
	String filePath = importSelectFileTextField.getText();
        
	if (isFilePath(filePath)){
            if(isCSVFile(filePath)){
                
		if(reader.insertCSVFile(filePath)){        
                    importActionTarget.setFill(Color.BLUE);
                    importActionTarget.setText("Import successfull!");
                }
                else{
                    importActionTarget.setFill(Color.RED);
                    importActionTarget.setText("Import unsuccessfull!");
                }
               
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
    
    private void importDirectory(ActionEvent event){
        importActionTarget.setText(" ");
	String dirPath = importSelectDirectoryTextField.getText();
        
	if (isDirectoryPath(dirPath)){
		if(reader.insertCSVDirectory(dirPath)){
                    importActionTarget.setFill(Color.BLUE);
                    importActionTarget.setText("Import successfull!");    
                }
                else{
                    importActionTarget.setFill(Color.RED);
                    importActionTarget.setText("Import unsuccessfull!");
                }
                
                File file = new File(dirPath);
                file = new File(file.getParent());
                importDirectoryChooser.setInitialDirectory(file);
	}
	else {
            importActionTarget.setText("Not a valid directory path!");
        } 
    }
    
    @FXML
    private void handleDiagramSaveButtonAction(ActionEvent event) {
        
        diagramActionTarget.setText(" ");
        String filePath = diagramSelectReportTextField.getText();
        String tableName = diagramSelectClassChoiceBox.getValue();
        Report report;
        if(isFilePath(filePath)) {
            if(isJSONFile(filePath)) {
                report = new Report(filePath,tableName);
                report.createResults();
                
                Stage stage = (Stage) root.getScene().getWindow();
                File file = diagramReportChooser.showSaveDialog(stage);
                report.writeReportToFile(new File(file.getAbsolutePath())); //TODO: Change this. A Diagram should be generated here.
                
                Stage stage2 = new Stage();
                stage2.setScene(report.generateImage());           
                stage2.show();
                
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
    private void handleImportSelectDirectoryButtonAction(ActionEvent event){
        Stage stage = (Stage) root.getScene().getWindow();
        
        File file = importDirectoryChooser.showDialog(stage);
        if (file != null){
            System.out.println(file.getPath());
            importSelectDirectoryTextField.setText(file.getPath());
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

    /**
     * Checks if the file ends with .CSV or .csv
     * @param filePath 
     */
    private Boolean isCSVFile(String filePath) {
        return (filePath.endsWith(".CSV") || filePath.endsWith(".csv"));
    }

    /**
     * Checks if there is a file at the end of the path.
     * @param filePath
     */
    private Boolean isFilePath(String filePath) {
        File file = new File(filePath);
        return file.isFile();
    }
    /**
     * Checks if there is a directory at the end of the path.
     * @param filePath
     */
    private Boolean isDirectoryPath(String dirPath){
        File file = new File(dirPath);
        return file.isDirectory();
    }

    /**
     *  Checks if the file ends with .JSON or .json
     * @param filePath 
     */
    private Boolean isJSONFile(String filePath) {
        return filePath.endsWith(".JSON") || filePath.endsWith(".json");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        ExtensionFilter csvFilter;
        csvFilter = new ExtensionFilter("CSV Files(*.csv)","*.csv");
        ExtensionFilter jsonFilter;
        jsonFilter = new ExtensionFilter("JSON Files(*.json)","*.json");
        ExtensionFilter cssFilter;
        cssFilter = new ExtensionFilter("CSS Files(*.css)","*.css");
          
        //Import tab initialize
        importFileChooser.setInitialDirectory(defaultDir);
        importFileChooser.setTitle("Choose a Class csv");
        importFileChooser.getExtensionFilters().add(csvFilter);     
        importDirectoryChooser.setTitle("Choose a Directory");
        
        //Diagram tab initialize
        diagramReportChooser.setInitialDirectory(defaultDir);
        diagramReportChooser.setTitle("Choose a Report json");
        diagramReportChooser.getExtensionFilters().add(jsonFilter);
        diagramSelectClassChoiceBox.setItems(this.getTableNames());
        
        //Options tab initialize
        optionsJSONChooser.setInitialDirectory(defaultDir);
        optionsJSONChooser.setTitle("Choose as JSON file");
        optionsJSONChooser.getExtensionFilters().add(jsonFilter);
        
        optionsCSSChooser.setInitialDirectory(defaultDir);
        optionsCSSChooser.setTitle("Choose a Stylesheet");
        optionsCSSChooser.getExtensionFilters().add(cssFilter);
        
        optionsSelectAnswerTypesTextField.setText(defaultDir + "\\answerTypes.json");
        optionsSelectSurveyTextField.setText(defaultDir + "\\survey.json");
        optionsSelectChartStyleTextField.setText(defaultDir + "\\ChartStyle.css");
       
    }    
    
}
