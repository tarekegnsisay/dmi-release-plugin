package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;

import com.dmi.plugin.util.Constants;

public class FetchService {
	final static Logger logger = Logger.getLogger(FetchService.class);
	public static void fetch(Git git) {
		try {
			git.fetch().call();
			git.branchCreate().setName(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH)
			.setStartPoint(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME+"/" + Constants.WORKFLOW_DEFAULT_MASTER_BRANCH)
			.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
			git.checkout().setName(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH).call();	
		} catch(Exception e) {
			logger.error("error while fetching..."+e.getMessage());
		}
		 
	}

}
