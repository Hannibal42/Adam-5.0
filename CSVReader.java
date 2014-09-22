import java.io.IOException;
import java.io.File;
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

class CSVReader {

	private final static Charset ENCODING = StandardCharsets.UTF_8; 

	public CSVReader(){
	}

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
	public void insertCSVFolder(List<String> filePaths) throws IOException {
		Iterator<String> iter = filePaths.iterator();
		while (iter.hasNext()){
			this.insertCSVFile(iter.next()); 
		}
	}

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
		
		//TODO: What to do with the SQLExceptions
		try {
			PreparedStatement stmt = ct.getStatement("DROP TABLE IF EXISTS " + tableName);
			stmt.executeUpdate();
			stmt = ct.getStatement(createString);
			stmt.executeUpdate();
		}
		catch(SQLException e){
			System.out.println(e);
		}
	}
	
	private void insertValues(String tableName, List<List<String>> values) {
		DBController ct =DBController.getInstance();
		ct.initDBConnection();
		tableName = this.cleanString(tableName);
		String insertString = "INSERT INTO " + tableName + " VALUES(";

		for (int i = 0 ; i < values.get(0).size(); i++) {
			insertString += "?,";
		}

		insertString = insertString.substring(0,insertString.length()-1);
		insertString += ")";

		Iterator<List<String>> iterList = values.iterator();
		iterList.next(); //Removes the first list with the column names

		try {

			PreparedStatement stmt = ct.getStatement(insertString);
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

	public Boolean isCSVFile(String filePath){
		if (filePath.endsWith(".CSV") || filePath.endsWith(".csv")){
			return true;
		}
		return false;
	}

	public Boolean isFilePath(String filePath){
		File file = new File(filePath);
		return file.exists();
	}

	private String cleanString(String input){
		return input.replaceAll("\\W",""); //Removes all non Word chars
	}

	private String getFileName(String filePath){
		File file = new File(filePath);
		return file.getName();
	}
}