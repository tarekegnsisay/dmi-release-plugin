package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;

public class StagingService {
	final static Logger logger=Logger.getLogger(StagingService.class);
	
	public static void stageFiles(Git git,String filePattern) {
		try {
			git.add().addFilepattern(filePattern).call();
		} catch (Exception e) {
			logger.error("unable to stage file: [ "+filePattern +" ] "+e.getMessage());
		}
	}
}
