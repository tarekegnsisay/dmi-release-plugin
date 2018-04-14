package com.dmi.plugin.service.git;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;

public class PushAndPullService {
	final static Logger logger=Logger.getLogger(PushAndPullService.class);
	public static void pushNewBranch(Git git,String newBranchName, CredentialsProvider userCredential) {
		try {

			git.push().setRemote("origin").setCredentialsProvider(userCredential).call();
			logger.info(" branch: [ "+newBranchName+" ] has been pushed successfully");
		} catch (GitAPIException e) {
			logger.error("unable to push branch: [ "+newBranchName+" ]"+e.getMessage());
		}
	}
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
