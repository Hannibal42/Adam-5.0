import java.io.IOException;
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

	public void insertCSVFile(final String pathString, final String tableName){
		Path path = Paths.get(pathString);
		//Path path = Paths.get(System.getProperty("user.dir") + "/" + fileName);
		List<List<String>> content = new ArrayList<List<String>>(); //Whats the fastes list of them all?
		
		try (Scanner scanner = new Scanner(path,ENCODING.name())){
			
			while(scanner.hasNextLine()){
				List<String> line = Arrays.asList(scanner.nextLine().split(","));
				content.add(line);
			}

		}
		catch (IOException e) {
			System.out.println(e);
		}
		//System.out.println(content.get(0));

		this.createTable(tableName, content.get(0));
		this.insertValues(tableName, content);
	}

	public void insertCSVFiles(final List<String> fileNames) throws IOException {
		Iterator<String> iter = fileNames.iterator();
		while (iter.hasNext()){
			this.insertCSVFile(iter.next());
		}
	}


	//TODO: Santize tableName and other inputs
	private void createTable(String tableName,List<String> headRow) {
		DBController ct = DBController.getInstance();
		ct.initDBConnection();
		tableName = tableName.replace(".","");
		String createString = "CREATE TABLE "+ tableName + "(";   
		Iterator<String> iter = headRow.iterator();

		while (iter.hasNext()){
				createString += iter.next() + " INTEGER, "; 
			}

		createString = createString.substring(0,createString.length()-2);
		createString += ")";
		
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
	//TODO: Santize tableName and other inputs
	private void insertValues(String tableName, List<List<String>> values) {
		DBController ct =DBController.getInstance();
		ct.initDBConnection();
		tableName = tableName.replace(".","");
		String insertString = "INSERT INTO " + tableName + " VALUES(";

		for (int i = 0 ; i < values.get(0).size(); i++) {
			insertString += "?,";
		}

		insertString = insertString.substring(0,insertString.length()-1);
		insertString += ")";
		//System.out.println(insertString); 

		Iterator<List<String>> iterList = values.iterator();
		iterList.next(); //Removes the first list with the column names
		try {

			PreparedStatement stmt = ct.getStatement(insertString);
			ct.setAutoCommit(false);

			while (iterList.hasNext()){
				Iterator<String> iterValues = iterList.next().iterator();
				int i = 1; 
				while (iterValues.hasNext()){
					stmt.setString(i,iterValues.next());
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



	//public static void main(String[] args) throws IOException{
	//	CSVReader reader = new CSVReader();
	//	reader.insertCSVFile("b1.csv");
	//}
	
}