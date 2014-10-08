package userInterf;
import java.io.*;

public class Parser {
	/*
	 * Parser class - input management 
	 */
	
	//Class fields
	private FileReader fRead = null;
	private BufferedReader bRead = null;
	
	//Constructor
	public Parser(String file){
		try{fRead = new FileReader(file);}
		catch (FileNotFoundException e) {System.out.println(e.getMessage());}
		bRead = new BufferedReader(fRead);
	}
	//Release link to file
	public int close(){
		try {
			bRead.close();
			fRead.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	//Read a line from the file and return the splitted line
	public String[] readLine(){
		String line = null;
		//try to read a line
		try{
			line = bRead.readLine();
		} catch(IOException e){
			e.printStackTrace();
		}
		//Split the line using spaces - if line is null, return null
		String[] result = (line != null) ? line.split(" ") : null;
		//return splitted string
		return result;
	}
	
}
