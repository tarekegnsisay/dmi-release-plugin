package com.dmi.plugin.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class LoggingUtils {
	public static Map<String, LinkedList<String>> saveLog(Map<String, LinkedList<String>> oldLog, String newLogType, String message) {
		
		LinkedList<String> oldLogDetail=oldLog.get(newLogType.trim());
		if(oldLogDetail==null) {
			oldLogDetail=new LinkedList<String>();
		}
		oldLogDetail.add(message);
		oldLog.put(newLogType, oldLogDetail);
		return oldLog;
	}

	public static void logProject(MavenProject project, Log logger) {
		
		logger.info("Project version: "+project.getVersion());
		logger.info("Project NAme: "+project.getName());
		if(project!=null && project.getScm()!=null) {
			logger.info("Project URI: "+project.getScm().getConnection());
	}
	else {
		logger.error("Please set or check scm config in pom.xml ");
	}
		logger.info("Project base directory: "+project.getBasedir());
		logger.info("Project base directory: AbsolutePath: "+project.getBasedir().getAbsolutePath());
		
	
}

	public static void printLog(Map<String, LinkedList<String>> log, Log mavenLogger) {
		if(log==null) {
			mavenLogger.info("[Log is EMPTY OK]");
			return;
		}
		for(Entry<String, LinkedList<String>> e:log.entrySet()) {
			String logType=e.getKey();
			List<String> logEntryList=e.getValue();
			for(String logEntry:logEntryList) {
				if(logType.equals("error")) {
					mavenLogger.error("[["+logEntry+"]]");
				}
				else {
					mavenLogger.info("[["+logEntry+"]]");
				}
			}
			
		}
		
	}
}
