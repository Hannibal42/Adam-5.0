// de.ifsr.adam

import org.json.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Report{

	JSONArray report = null;

	private final static Charset ENCODING = StandardCharsets.UTF_8; 

	public Report(String reportPath){
		Path path = Paths.get(reportPath);
		String jsonStr = new String();
		
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


	public ReportResult getResult(){
		return null; 
	}

	public static void main(String[] args){
		Report report = new Report("C:/Users/Simon/Desktop/JAdam/report.json"); 
	}
}