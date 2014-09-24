//package de.ifsr.adam;

import org.json.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Report{

	JSONArray report = null;

	private final static Charset ENCODING = StandardCharsets.UTF_8; 
	private final String tableName;

	public Report(String reportPath,String tableName){
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


	public JSONArray getResult(){
		for(int i = 0; i < report.length(); i++){
			try {
				this.getQuestionResult(report.getJSONObject(i));
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		return null;
	}

	private JSONObject getQuestionResult(JSONObject question) throws JSONException{
		DBController ct = DBController.getInstance();
		ct.initDBConnection();

		String sqlQuery = "SELECT t1." + question.getString("question") + ",COUNT(t2." + question.getString("question") +") ";
		sqlQuery += "FROM " + tableName + " t1 ";
		sqlQuery += "INNER JOIN " + tableName + " t2 ON t1.rowid=t2.rowid ";
		sqlQuery += "GROUP BY t1." + question.getString("question") + " ";
		sqlQuery += "ORDER BY t1." + question.getString("question") + " ASC;";

		System.out.println(sqlQuery);

		/*SELECT b1.F1,COUNT(b2.F1) FROM b3CSV b1
		 INNER JOIN b3CSV b2 ON b1.rowid = b2.rowid
		 GROUP BY b1.F1
		 ORDER BY b1.F1 ASC
		*/

		 try {
		 	Statement stmt = ct.getStatement();
		 	ResultSet resultSet = stmt.executeQuery(sqlQuery);
		 	
		 	while(resultSet.next()){
		 		System.out.println(resultSet.getInt(1));
		 		System.out.println(resultSet.getInt(2));	
		 	}

		 }
		 catch (SQLException e) {
		 	System.out.println(e);
		 }

		return null;


	}


	public static void main(String[] args){
		Report report = new Report("C:/Users/Simon/Desktop/JAdam/data/report.json","b3CSV"); 
		report.getResult();
	}
}