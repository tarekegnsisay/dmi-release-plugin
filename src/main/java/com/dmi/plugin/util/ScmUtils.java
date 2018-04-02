package com.dmi.plugin.util;

import org.apache.maven.model.Scm;
import org.apache.maven.plugin.logging.Log;

public class ScmUtils {
	public static String getScmUri(Scm scm,Log logger) {
		String uri="",scmConnection="";
		if(scm!=null) {
			scmConnection=scm.getConnection();
			scmConnection=scmConnection.trim();
			int index=scmConnection.indexOf("https");
			index=index>=0?index:scmConnection.indexOf("git@");
			index=index>=0?index:scmConnection.indexOf("http");
			uri=scmConnection.substring(index);
			if(uri.isEmpty()) {
				logger.error(" scm url inside pom must be set");
				return null;
			}
			logger.info("Repo URI:"+uri);
			//check if valid git repo
			//scmManagerService.validateRepo(uri);
			return uri;
			
		}
		else {
			logger.error("scm element in pom.xm must be provided");
			return null;
		}
	
	}
}
