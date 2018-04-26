package com.dmi.plugin.service;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.UserConfiguration;

public class HotfixService extends AbstractApplicationService{

	final static Logger logger = Logger.getLogger(HotfixService.class);

	public HotfixService(MavenProject mavenProject,ScmBranchingConfiguration scmBranchingConfiguration, UserConfiguration userConfiguration) {
		super(mavenProject,scmBranchingConfiguration, userConfiguration);
	}

	public HotfixService() {
	}

	public boolean startHotfix(String hotfixName, String tagName) {

		String baseDevBranch = scmBranchingConfiguration.getDevelopmentBranch();
		String newHotfixBranchName = getFullNameOfHotfixBranch(scmBranchingConfiguration.getHotfixBranchPrefix(), hotfixName);

		boolean isTagExists=scmService.isTagExists(tagName);

		if(!isTagExists){
			logger.error("tag with name: ["+tagName+"] doesn't exists, please choose available tag name and start over.");
			return false;
		}
		boolean isBranchExists = scmService.isBranchExists(newHotfixBranchName);
		if (isBranchExists) {
			logger.error("Branch with name [" + newHotfixBranchName + "] already exists");
			return false;
		}

		if(scmService.checkoutBranch(baseDevBranch))
			if(scmService.createHotfixBranchFromTag(newHotfixBranchName, tagName))
				if(scmService.checkoutBranch(newHotfixBranchName))
					return true;

		return false;
	}
	/*
	 * TODO versions in hotfix release, but SNAPSHOT in develop, merging those branches should preserve this behavior
	 * 
	 * 
	 */
	public boolean finishHotfix(String hotfixName) {

		String fullNameOfHotfixBranch = getFullNameOfHotfixBranch(scmBranchingConfiguration.getFeatureBranchPrefix(), hotfixName);
		
		String devBranch=scmBranchingConfiguration.getDevelopmentBranch();
		String masterBranch=scmBranchingConfiguration.getMasterBranch();
		
		String mergeMessage1 = "merging feature [" + hotfixName + "] to development stream before finishing hotfix";
		String mergeMessage2 = "merging feature [" + hotfixName + "] to master before finishing hotfix";

		String successMessage=" hotfix is completed now [" + fullNameOfHotfixBranch + "] branch is merged in to ["
				+ masterBranch+" and "+devBranch + "] and deleted from local and remote repositories.";

		if(scmService.checkoutBranch(devBranch))
			if(scmService.mergeBranches(devBranch, fullNameOfHotfixBranch,	mergeMessage1))
				if(scmService.mergeBranches(masterBranch, fullNameOfHotfixBranch,	mergeMessage2))
					if(scmService.pushBranch(devBranch))
						if(scmService.pushBranch(masterBranch))
							if(scmService.deleteBranchFromLocal(fullNameOfHotfixBranch))
								if(scmService.deleteBranchFromRemote(fullNameOfHotfixBranch)){
									logger.info(successMessage);
									return true;
								}

		return false;
	}

	public boolean publishHotfix(String hotfixName) {

		String fullNameofHotfix = getFullNameOfHotfixBranch(scmBranchingConfiguration.getHotfixBranchPrefix(), hotfixName);

		boolean isPublished = scmService.publishBranch(fullNameofHotfix);

		if (isPublished)
			return true;
		return false;
	}

	public String getFullNameOfHotfixBranch(String prefix, String hotfixName) {
		String hotefixPrefix = prefix == null ?scmBranchingConfiguration.getHotfixBranchPrefix() : prefix;
		String newHotfixBranchName = hotefixPrefix + hotfixName;
		return newHotfixBranchName;
	}
}
