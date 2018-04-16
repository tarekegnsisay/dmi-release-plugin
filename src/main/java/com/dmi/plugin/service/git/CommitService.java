package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;

public class CommitService {
	final static Logger logger = Logger.getLogger(CommitService.class);
	
	public static void commitAllChangesToTackedFiles(Git git,String commitMassage) {
		
		CommitCommand commitCommand=git.commit().setAll(true);
		commitCommand.setMessage(commitMassage);
		String currentBranch="";
			try {
				currentBranch=commitCommand.getRepository().getBranch();
				commitCommand.call();
			}catch (Exception e) {
				logger.error("error while commiting changes to: "+currentBranch);
			}
	}

	public static void commitStagedFilesOnly(Git git,String commitMassage) {
		
		String currentBranch="";
		
		CommitCommand commitCommand;
		commitCommand=git.commit();
		commitCommand.setMessage(commitMassage);
		
		try {
			currentBranch=commitCommand.getRepository().getBranch();
			commitCommand.call();
		}catch (Exception e) {
			logger.error("error while commiting changes to: "+currentBranch);
		}
	}
	
}
