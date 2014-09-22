//package de.ifsr.adam;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import java.io.File;

public class View extends Application{

	final CSVReader reader = new CSVReader();

	@Override
	public void start(Stage primaryStage) {
		
		GridPane grid = new GridPane();
		File defaultDir = new File(System.getProperty("user.dir"));

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25,25,25,25));

		Text scenetitle = new Text("Welcome to Adam 5.0");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle,0,0,2,1);

		//Button for Data selection
		
		FileChooser dataChooser = new FileChooser();
		dataChooser.setTitle("Select Data File");
		dataChooser.setInitialDirectory(defaultDir);

		
		Button dataBtn = new Button("Select Data File");
		grid.add(dataBtn,0,1);

		TextField dataTextField = new TextField();
		grid.add(dataTextField,1,1); 

		dataBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e){
				File file = dataChooser.showOpenDialog(primaryStage);
				if (file != null){
					dataTextField.setText(file.getPath());
				}
			}

		});		
	
		//Button for Question selection
		Button questionBtn = new Button("Select Questions");
		grid.add(questionBtn,0,2);

		TextField questionTextField = new TextField();
		grid.add(questionTextField,1,2);

		questionBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e){
				questionTextField.setText("Selected");
			}

		});

		//Button for Report selection
		Button reportBtn = new Button("Select Report");
		grid.add(reportBtn,0,3);

		TextField reportTextField = new TextField();
		grid.add(reportTextField,1,3);

		reportBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e){
				reportTextField.setText("Selected");
			}

		});

		//Save Button bottom right corner. 
		Button saveBtn = new Button("Save Diagram");
		HBox hbSaveBtn = new HBox(10);
		hbSaveBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbSaveBtn.getChildren().add(saveBtn);
		grid.add(hbSaveBtn,1,4);

		final Text actiontarget = new Text();
		grid.add(actiontarget,1,6);

		saveBtn.setOnAction(new EventHandler<ActionEvent>() { 

			@Override
			public void handle(ActionEvent e){
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Diagrams generated.");
				
				String filePath = dataTextField.getText();
				if (reader.isFilePath(filePath)){
					if(reader.isCSVFile(filePath)){
						reader.insertCSVFile(filePath);
					}
					else{
						dataTextField.setText("Not a CSV file!");
					}
				}
				else {
					dataTextField.setText("Not a valid file path!");
				}
			}
		});

		//grid.setGridLinesVisible(true);
		Scene scene = new Scene(grid,300,275);
		primaryStage.setTitle("Adam 5.0");
		primaryStage.setScene(scene);
		//primaryStage.getStylesheets().add(Login.class.getResource("Adam.css").toExternalForm());  //TODO: Pimp my form!
		primaryStage.show();
	}

	public static void main(String[] args){
		launch(args); // TODO: I have to include the JavaFX Packager to embeded the JavaFX Launcher
	}

}