package com.dmi.plugin.util;

import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class LoggingUtils {
	public static Map<String, List<String>> saveLog(Map<String, List<String>> oldLog, String newLogType, String message) {
		List<String> oldLogDetail=oldLog.get(newLogType);
		oldLogDetail.add(message);
		oldLog.put(newLogType, oldLogDetail);
		return oldLog;
	}

	public static void logProject(MavenProject project, Log logger) {
		
		logger.info("Project version: "+project.getVersion());
		logger.info("Project URI: "+project.getVersion());
		logger.info("Project base directory: "+project.getBasedir());
		logger.info("Project base directory: AbsolutePath: "+project.getBasedir().getAbsolutePath());
		
	}
}
