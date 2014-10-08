package userInterf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	/*
	 * Writer class - output management
	 */
	
	//Class Fields
	private FileWriter fWrite = null;
	private BufferedWriter bWrite = null;
	
	//Constructor
		public Writer(String file, boolean append){
			try{fWrite = new FileWriter(file, append);}
			catch (IOException e) {System.out.println(e.getMessage());}
			bWrite = new BufferedWriter(fWrite);
		}
	//Release link to file
	public int close(){
		try {
			bWrite.close();
			fWrite.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	//Print a string to file
	public void write(String data){
		try {bWrite.write(data);}
		catch (IOException e) {e.printStackTrace();}
	}
}