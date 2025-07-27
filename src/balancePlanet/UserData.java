package balancePlanet;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class UserData {
	FileWriter myFileWriter;
	String directoryName=System.getProperty("user.dir")+"/res/";
	BufferedWriter myBufferedWriter;
	CoreStuff tc;

	public UserData(CoreStuff tcl) {
		tc=tcl;
	}
	public void writeUserDataFile(String startTime, String endTime) {
		try {
			myFileWriter=new FileWriter(directoryName+"/Logs/"+tc.userName+"_Log.txt",true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
		myBufferedWriter=new BufferedWriter(myFileWriter);
		writeSingleLine("User Name: "+tc.userName);
		writeSingleLine("Start Time: "+startTime);
		for (Page pg: tc.pages) {
			writeSingleLine(pg.visitCount+"  "+pg.title+"  "+pg.value);
		}
		writeSingleLine(".");
		writeSingleLine("End Time: "+endTime);
		writeSingleLine(".");
		try {
			myBufferedWriter.flush();
			myBufferedWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}				
	}

	public void writeSingleLine(String theText) {
		try {
			myBufferedWriter.append(theText);
			myBufferedWriter.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
