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
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
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
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * The Controller that holds all the functions called by the GUI
 * @author Simon
 */
public class FXMLController implements Initializable {
    static Logger log = Logger.getLogger(FXMLController.class.getName());
    
    
    private final CSVReader reader = new CSVReader();
    private final File defaultDir = new File(System.getProperty("user.dir"));
    private final FileChooser importFileChooser = new FileChooser();
    private final FileChooser diagramReportChooser = new FileChooser();
    private final FileChooser optionsJSONChooser = new FileChooser();
    private final FileChooser optionsCSSChooser = new FileChooser();
    private final DirectoryChooser importDirectoryChooser = new DirectoryChooser();
    private  ObservableList<String> diagramObservableList;
    
    @FXML private Parent root; //I am rooooooooot!
    @FXML private TextField importSelectFileTextField;
    @FXML private TextField diagramSelectReportTextField;
    @FXML private TextField importSelectDirectoryTextField;
    @FXML private TextField optionsSelectAnswerTypesTextField;
    @FXML private TextField optionsSelectSurveyTextField;
    @FXML private TextField optionsSelectChartStyleTextField;
    @FXML private Text importActionTarget;
    @FXML private Text diagramActionTarget;
    @FXML private ChoiceBox<String> diagramSelectClassChoiceBox;
    @FXML private ScrollPane reportEditor;
    
    
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
    
    @FXML private void handleOptionsSaveLogButtonAction(ActionEvent event) {
	Properties properties = new Properties();
	try {
	    properties.load(new FileReader(new File("C:\\Users\\Simon\\Desktop\\Adam 5.0\\properties\\log4j.properties"))); //TODO: Make this dynamic.
	} 
	catch (IOException ex) {
	    BasicConfigurator.configure();
	    Logger.getRootLogger().setLevel(Level.ERROR);
	}
	properties.setProperty("log4j.appender.FILE.immediateFlush", "true");
	properties.setProperty("log", System.getProperty("user.dir") + "\\log");
	PropertyConfigurator.configure(properties);
	properties.setProperty("log4j.appender.FILE.immediateFlush", "false");
	PropertyConfigurator.configure(properties);
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
                
		String surveyPath = optionsSelectSurveyTextField.getText();
		String answerTypesPath = optionsSelectAnswerTypesTextField.getText();
		String stylesheetPath = optionsSelectChartStyleTextField.getText();
                Stage stage2 = new Stage();
                stage2.setScene(report.generateImage(surveyPath,answerTypesPath,stylesheetPath));     
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
    
    @FXML
    private void handleReportNewReportButtonAction(ActionEvent event) {
	VBox vbox = new VBox();
	reportEditor.setContent(vbox);
	vbox.setAlignment(Pos.TOP_CENTER);
	handleReportAddQuestionButtonAction(event);
    }
    
    @FXML
    public void handleReportAddQuestionButtonAction(ActionEvent e) {
	VBox vbox ;
	
	try {
	    vbox = (VBox) reportEditor.getContent();
	} 
	catch(Exception ex){
	    vbox = new VBox();
	    System.out.println(ex); //TODO: Logging
	}
	
	vbox.setAlignment(Pos.TOP_CENTER);
	GridPane gridPane = new GridPane();
	
	TextField question = new TextField();
	Label questionLabel = new Label("Question:"); //TODO: Multilanguage support
	gridPane.add(questionLabel, 0, 0);
	gridPane.add(question, 1, 0);
	
	ChoiceBox diagram = new ChoiceBox();
	diagram.setItems(diagramObservableList);
	Label diagramLabel = new Label("Diagram:");//TODO: Multilanguage support
	gridPane.add(diagramLabel, 0, 1);
	gridPane.add(diagram, 1, 1);
	
	TextField comment = new TextField();
	Label commentLabel = new Label("Comment:"); //TODO: Multilanguage support
	gridPane.add(commentLabel, 0, 2);
	gridPane.add(comment, 1,2);
	
	HBox buttonBox = new HBox();
	buttonBox.setAlignment(Pos.CENTER);
	Button button = new Button("Add Question");
	button.setOnAction(new EventHandler<ActionEvent>() { 
	    @Override public void handle(ActionEvent e) {
		handleReportAddQuestionButtonAction(e);
	    }
	});
	buttonBox.getChildren().add(button);
	
	try{//Removing the last "New Question" Button
	    ObservableList<Node> children = vbox.getChildren();
	    children.remove(children.size()-1);
	}
	catch(Exception ex){}
	vbox.getChildren().add(gridPane);
	vbox.getChildren().add(buttonBox);

	reportEditor.setContent(vbox);	
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
	
	//Report Editor Tap initialize
	ArrayList<String> diagramsArrayList = new ArrayList();
	diagramsArrayList.add("bardiagram");
	diagramsArrayList.add("cakediagram");
	diagramObservableList = FXCollections.observableList(diagramsArrayList);
       
    }    
    
}
