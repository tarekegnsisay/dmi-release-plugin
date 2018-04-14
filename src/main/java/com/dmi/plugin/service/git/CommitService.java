package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;

public class CommitService {
	final static Logger logger = Logger.getLogger(CommitService.class);
	public static void commitChanges(Git git,String commitMassage) {
		
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
	
	public static void checkoutCommit(Git git,String commitId) {
		try {
		git.checkout()
	    .setStartPoint(commitId)
	    .call();
		} catch (Exception e) {
			logger.error("error while checkout commitId: "+commitId);
		}
	}
	
	public static void checkoutCommit(Git git,String commitId, String checkoutToBranch)  {
		
		try {
			git.checkout()
			.setCreateBranch(true)
			.setName(checkoutToBranch)
			.setStartPoint(commitId)
			.call();
		} catch (Exception e) {
			logger.error("error while checkout commitId: ["+commitId+"] to branch: ["+checkoutToBranch+"]");
		}
	}
}
