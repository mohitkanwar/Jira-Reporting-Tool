package com.mk.jira.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHandler {
	
	public String getFileContents(String filepath) throws IOException{
		DateFormat year = new SimpleDateFormat("yyyy");
		DateFormat month = new SimpleDateFormat("MMM");
		DateFormat day = new SimpleDateFormat("dd");

		Date date = new Date();
		String folder = Configurations.LOCATION+ File.separator + year.format(date)
				+ File.separator + month.format(date) + File.separator
				+ day.format(date) + File.separator;
		return  new String(Files.readAllBytes(Paths.get(folder+filepath)));
	}
	public String getConfigFileContents(String filepath) throws IOException{
		
		return  new String(Files.readAllBytes(Paths.get(filepath)));
	}
	public void createFile(String input,String filename) throws IOException{
		
		
		DateFormat year = new SimpleDateFormat("yyyy");
		DateFormat month = new SimpleDateFormat("MMM");
		DateFormat day = new SimpleDateFormat("dd");

		Date date = new Date();
		String folder = Configurations.LOCATION+ File.separator + year.format(date)
				+ File.separator + month.format(date) + File.separator
				+ day.format(date) + File.separator;
		
		
		File file=new File(folder+filename);
		if(file.exists()){
			file.delete();
			Files.createFile(file.toPath());
		}
		else{
			Files.createDirectories(file.getParentFile().toPath());
			Files.createFile(file.toPath());
		}

		try {
		    FileWriter f2 = new FileWriter(file, false);
		    f2.write(input);
		    f2.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
}
