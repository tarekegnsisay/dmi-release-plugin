package com.dmi.plugin.service;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.util.Constants;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.UserConfiguration;

public class FeatureService extends AbstractApplicationService {

	final static Logger logger = Logger.getLogger(FeatureService.class);

	public FeatureService(MavenProject mavenProject,ScmBranchingConfiguration scmBranchingConfiguration, UserConfiguration userConfiguration) {
		super(mavenProject,scmBranchingConfiguration, userConfiguration);
	}

	public FeatureService() {}

	public boolean createFeature(String featureName) {

		String baseDevBranch = scmBranchingConfiguration.getDevelopmentBranch();
		String newFeatureName = getFullNameOfFeature(scmBranchingConfiguration.getFeatureBranchPrefix(), featureName);

		boolean branchExists = scmService.isBranchExists(newFeatureName);

		if (branchExists) {
			logger.error("Branch with name [" + newFeatureName + "] already exists");
			return false;
		}

		if(scmService.checkoutBranch(baseDevBranch))
			if(scmService.createBranch(newFeatureName))
				if(scmService.checkoutBranch(newFeatureName))
					return true;

		return false;
	}

	public boolean publishFeature(String featureName) {

		String featureFullName = getFullNameOfFeature(scmBranchingConfiguration.getFeatureBranchPrefix(), featureName);

		if (scmService.publishBranch(featureFullName)) {
			logger.info("feature is published, you can tell others to contribute to it.");
		} 
		else {
			logger.error("error occured while publishin a feature, with name: [" + featureFullName + "]");
			return false;
		}
		return true;
	}

	public boolean finishFeature(String featureName) {

		String featureFullName = getFullNameOfFeature(scmBranchingConfiguration.getFeatureBranchPrefix(), featureName);
		String mergeMessage = "merging feature [" + featureName + "] to development stream before finishing";
		String devBranch=scmBranchingConfiguration.getDevelopmentBranch();

		if(scmService.checkoutBranch(devBranch))
			if(scmService.mergeBranches(devBranch, featureFullName,	mergeMessage))
				if(scmService.pushBranch(devBranch))
					if(scmService.deleteBranchFromLocal(featureFullName))
						if(scmService.deleteBranchFromRemote(featureFullName))

						{
							logger.info(" feature finished, now [" + featureFullName + "] branch is merged in to ["
									+ devBranch + "] and deleted from local and remote repositories.");
							return true;
						}
		return false;
	}

	public String getFullNameOfFeature(String prefix, String featureName) {
		String featurePrefix = prefix == null ? Constants.WORKFLOW_DEFAULT_FEATURE_BRANCH_PREFIX : prefix;
		String newFeatureName = featurePrefix + featureName;
		return newFeatureName;
	}

}
