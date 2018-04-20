package com.dmi.plugin.service;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class HotfixService extends AbstractApplicationService{

	public void createHotFix(MavenProject project, Log log) {
		/*
		 * Compute hotfix branch name and call super
		 */
		String hotFixBranchName="hotfix-branch-name";
		//createBranch(hotFixBranchName);
	}

	public void finishHotfix(MavenProject project, Log log) {
		
	}

}
