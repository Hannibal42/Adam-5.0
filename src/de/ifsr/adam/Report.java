package de.ifsr.adam;

import org.json.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.PrintWriter;
import java.io.File;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
* Main class for report handling, can construct a report from JSON file and has functionality for evaluating and saving the report.
*/
public class Report{

	private final static Charset ENCODING = StandardCharsets.UTF_8; 
	private final String tableName;
	private JSONArray report = null;
	private Boolean result = false;

	/**
	*	Basic Report constructor, reads a prepared report from a JSON file.
	* 	@param reportPath Path to the JSON file
        *       @param tableName the table you want to operate on. 
	*/
	public Report(String reportPath, String tableName){
		Path path = Paths.get(reportPath);
		String jsonStr = new String();
		this.tableName = tableName;
		
		try (Scanner scanner = new Scanner(path,ENCODING.name())){

			while(scanner.hasNext()){
				jsonStr += scanner.nextLine();
			}
			report = new JSONArray(jsonStr);

		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	/**
	* Evaluates the questions that are defined in the report. Should be used before any other function call is made. Sets the result field true.
	*/
	public void createResults(){
		JSONObject currentQuestion;

		for(int i = 0; i < report.length(); i++){	
			try{
				currentQuestion = report.getJSONObject(i); //getJSONObject is reference but the doc says its by value
				currentQuestion.put("result",getQuestionResult(currentQuestion));
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		result = true;
	}

	/**
	*	A function to evaluate one question in a report, used in createResults()
	*	@param question A single questions of a report
	*	@return A JSONObject holding all the results.
	* 	@throws JSONException if no new JSONObject can be created
	*/
	private JSONObject getQuestionResult(JSONObject question) throws JSONException{
		DBController ct = DBController.getInstance();
		ct.initDBConnection();

		String sqlQuery = "SELECT t1." + question.getString("question") + ",COUNT(t2." + question.getString("question") +") ";
		sqlQuery += "FROM " + tableName + " t1 ";
		sqlQuery += "INNER JOIN " + tableName + " t2 ON t1.rowid=t2.rowid ";
		sqlQuery += "GROUP BY t1." + question.getString("question") + " ";
		sqlQuery += "ORDER BY t1." + question.getString("question") + " ASC;";

		/* Exampel Query
		 SELECT b1.F1,COUNT(b2.F1) FROM b3CSV b1
		 INNER JOIN b3CSV b2 ON b1.rowid = b2.rowid
		 GROUP BY b1.F1
		 ORDER BY b1.F1 ASC
		*/

		JSONObject resultJSON = new JSONObject();
		try {
		 	Statement stmt = ct.getStatement();
		 	ResultSet resultSet = stmt.executeQuery(sqlQuery);
		 	
		 	while(resultSet.next()){	
		 		resultJSON.put(resultSet.getString(1),resultSet.getInt(2));
		 	}

		}
		catch (SQLException e) {
		 	System.out.println(e);
		}

		return resultJSON;
	}


	/**
	* @return The value of the report field.
	*/
	public JSONArray getReport(){
		return this.report;
	}

	/**
	* Writes the report to a JSON file.
	* @param path Path to the output directory
        * @param fileName the name of the new JSON file
	*/
	public void writeReportToFile(String path, String fileName){
		try{
			File file = new File(path+ "/"+ fileName + ".json");
			file.delete();
			file.createNewFile();

			PrintWriter writer = new PrintWriter(file);
			writer.println(this.report.toString(1));
			writer.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
        
        /**
	* Writes the report to a JSON file.
	* @param file The file you want to save to.
	*/
        public void writeReportToFile(File file){
            try{
		file.delete();
		file.createNewFile();

		PrintWriter writer = new PrintWriter(file);
		writer.println(this.report.toString(1));
		writer.close();
            }
            catch(Exception e){ //TODO:
		System.out.println(e);
            }
	}
        

        
	/**
	* @return The value of the result field.
	*/
	public Boolean hasResult(){
		return this.result;
	}

	/**
	* Gives back the result JSONObject of a given question, createResults() must be called before this function is used.
	* @param question the question
	* @return The JSONObject holding the results for the question.
	*/
	public JSONObject getResult(String question){
		JSONObject curQuestion = null;  
		String questionName = null;

		for(int i = 0; i < report.length(); i++){
			
			try{
				curQuestion = report.getJSONObject(i);
				questionName = curQuestion.getString("question");
			}
			catch(JSONException e){
				System.out.println(e);
			}

			if(question.equals(questionName)){
				try {
					return curQuestion.getJSONObject("result");
				}
				catch(JSONException e){
				}
			}
			
		}
		return null;
	}

	public static void main(String[] args){
		Report report = new Report("C:/Users/Simon/Desktop/JAdam/data/report.json","b3CSV"); 
		report.createResults();
		report.writeReportToFile(System.getProperty("user.dir"),"Report");
	}
}