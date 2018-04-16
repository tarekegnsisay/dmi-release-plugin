package com.dmi.scm.git;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileUtils {
	final static Logger looger=Logger.getLogger(FileUtils.class);
	
	public static File createFile(String path,String filename) {
		File file=new File(path+filename);
		return file;
	}
	public static FileWriter writeToFile(File file,String content) {
		FileWriter fileWriter=null;
		try {
			fileWriter=new FileWriter(file,true);
			fileWriter.append(content);
		} catch (IOException e) {

		}
		return fileWriter;
	}
}
