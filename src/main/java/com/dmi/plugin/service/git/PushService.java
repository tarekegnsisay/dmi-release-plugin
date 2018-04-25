package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;

import com.dmi.plugin.util.Constants;

public class PushService {
	final static Logger logger=Logger.getLogger(PushService.class);
	public static boolean pushBranch(Git git,String branchName, CredentialsProvider userCredential) {
		try {

			git.push().setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME).setCredentialsProvider(userCredential).call();
			logger.info(" branch: [ "+branchName+" ] has been pushed successfully");
		} catch (GitAPIException e) {
			logger.error("unable to push branch: [ "+branchName+" ] "+e.getMessage());
			return false;
		}
		return true;
	}
}
