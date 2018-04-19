package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;

public class PushService {
	final static Logger logger=Logger.getLogger(PushService.class);
	public static void pushNewBranch(Git git,String newBranchName, CredentialsProvider userCredential) {
		try {

			git.push().setRemote("origin").setCredentialsProvider(userCredential).call();
			logger.info(" branch: [ "+newBranchName+" ] has been pushed successfully");
		} catch (GitAPIException e) {
			logger.error("unable to push branch: [ "+newBranchName+" ]"+e.getMessage());
		}
	}
}
