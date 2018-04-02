package com.dmi.plugin.utility;

import java.util.List;
import java.util.Map;

public class LoggingUtility {
	public static Map<String, List<String>> saveLog(Map<String, List<String>> oldLog, String newLogType, String message) {
		List<String> oldLogDetail=oldLog.get(newLogType);
		oldLogDetail.add(message);
		oldLog.put(newLogType, oldLogDetail);
		return oldLog;
	}

}
