package com.dmi.plugin.service.git;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

public class PullService {
	
	private final static Logger logger=Logger.getLogger(PullService.class);
	
	public static void pullRepo(Git git) {
		String currentBranch="";
		PullCommand pullCommand=git.pull();
		try {
			currentBranch=git.getRepository().getBranch();
			pullCommand.call();
			logger.info("Pull command was successful.");
		} catch (GitAPIException | IOException e) {
			logger.error("unable to pull to branch: [ "+currentBranch+" ]"+e.getMessage());
		}
	}


}
