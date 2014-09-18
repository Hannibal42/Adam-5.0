import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List; 
import java.util.Scanner;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;


class CSVReader {

	private final static Charset ENCODING = StandardCharsets.UTF_8; 

	public CSVReader(){
	}

	public void insertCSVFile(final String fileName) throws IOException{
		Path path = Paths.get(System.getProperty("user.dir") + "/" + fileName);

		try (Scanner scanner = new Scanner(path,ENCODING.name())){

			while(scanner.hasNextLine()){
				System.out.println(scanner.nextLine());

			}

		}
	}

	public static void main(String[] args) throws IOException{
		CSVReader reader = new CSVReader();
		reader.insertCSVFile("b1.csv");
	}
	
}