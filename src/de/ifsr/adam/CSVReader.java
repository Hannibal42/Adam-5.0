package de.ifsr.adam;

import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List; 
import java.util.Scanner;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
* The CSVReader reads the data of the evaluation from a given CSV file.
*/
public class CSVReader {

	private final static Charset ENCODING = StandardCharsets.UTF_8; 

	public CSVReader(){
	}

	/**
	* Main method for inserting files into the database. Parses the CSV and calls all functions needed
	* to create the table and insert the values into the database.
	* @param filePath path to a CSV file
	*/
	public void insertCSVFile(final String filePath){
		Path path = Paths.get(filePath);
		List<List<String>> content = new ArrayList<List<String>>();

		try (Scanner scanner = new Scanner(path,ENCODING.name())){
			
			while(scanner.hasNextLine()){
				List<String> line = Arrays.asList(scanner.nextLine().split(","));
				content.add(line);
			}

		}
		catch (IOException e) {
			System.out.println(e);
		}

		this.createTable(this.getFileName(filePath), content.get(0));
		this.insertValues(this.getFileName(filePath), content);
	}

	//TODO: Fix this.
	public void insertCSVDirectory(final String dirPath) {
		File directory = new File(dirPath);
                
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name){
                        return name.toLowerCase().endsWith(".csv");
                    }
                };
                
                File[] files = directory.listFiles(filter);
                
                for(File file : files) {
                    if (file.isFile()){
                        insertCSVFile(file.getPath());
                    }
                }

	}

	/**
	* Creates the table for a CSV file.
	* @param tableName Name for the new table, headRow - A List containing all column names.
	*/
	private void createTable(String tableName,List<String> headRow) {
		DBController ct = DBController.getInstance();
		ct.initDBConnection();
		tableName = this.cleanString(tableName);

		String createString = "CREATE TABLE "+ tableName + "(";   
		Iterator<String> iter = headRow.iterator();

		while (iter.hasNext()){
				createString += cleanString(iter.next()) + " INTEGER, "; 
			}

		createString = createString.substring(0,createString.length()-2);
		createString += ")";
		
		try {
			PreparedStatement stmt = ct.getPreparedStatement("DROP TABLE IF EXISTS " + tableName);
			stmt.executeUpdate();
			stmt = ct.getPreparedStatement(createString);
			stmt.executeUpdate();
		}
		catch(SQLException e){
			System.out.println(e);
		}

	}

	/**
	* Inserts the values of the file into the table given.
	* @param tableName Name of the table, values - A matrix with each value in the file, each List in the List is a line in the file.
	*/ 
	private void insertValues(String tableName, List<List<String>> values) {
		DBController ct =DBController.getInstance();
		ct.initDBConnection();
		tableName = this.cleanString(tableName);
		String insertString = "INSERT INTO " + tableName + " VALUES(";

		for (int i = 0 ; i < values.get(0).size(); i++) {
			insertString += "?,";
		}

		insertString = insertString.substring(0,insertString.length()-1); //Removes the last comma.
		insertString += ")";

		Iterator<List<String>> iterList = values.iterator();
		iterList.next(); //Removes the first list with the column names.

		try {

			PreparedStatement stmt = ct.getPreparedStatement(insertString);
			ct.setAutoCommit(false);

			while (iterList.hasNext()){
				Iterator<String> iterValues = iterList.next().iterator();
				int i = 1; 
				while (iterValues.hasNext()){
					stmt.setString(i,this.cleanString(iterValues.next()));
					i += 1;
				}
				stmt.addBatch();
			}
			stmt.executeBatch(); 
			ct.commit();
		}
		catch(SQLException e){
			System.out.println(e);
		}
		finally{
			try{
				ct.setAutoCommit(true);
			}
			catch(SQLException e){
				System.out.println(e);
			}
		}
	}

	/**
	* Removes all alphanumeric characters from the string.
	*/
	private String cleanString(String input){
		return input.replaceAll("\\W",""); //Removes all non Word chars
	}

	/**
	* Returns the name of the file.
	*/
	private String getFileName(String filePath){
		File file = new File(filePath);
		return file.getName();
	}
}