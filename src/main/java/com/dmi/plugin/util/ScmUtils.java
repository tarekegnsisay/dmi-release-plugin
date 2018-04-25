package com.dmi.plugin.util;

import org.apache.log4j.Logger;
import org.apache.maven.model.Scm;

public class ScmUtils {
	final static Logger logger = Logger.getLogger(ScmUtils.class);
	public static String getScmUri(Scm scm) {
		String uri="",scmConnection="";
		if(scm!=null) {
			/*
			 * need more url validation logic here:-git ls-remote important here, more on this soon
			 */
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
			logger.info("uri resolved: ["+uri+"]");
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
