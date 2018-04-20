package com.dmi.plugin.service;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.Constants;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.ScmUtils;
import com.dmi.plugin.util.UserConfiguration;

public class FeatureService extends AbstractApplicationService {

	final static Logger logger = Logger.getLogger(FeatureService.class);

	public FeatureService(ScmBranchingConfiguration scmBranchingConfiguration, UserConfiguration userConfiguration) {
		super(scmBranchingConfiguration, userConfiguration);
	}

	public FeatureService() {
	}

	public boolean createFeature(MavenProject project, String featureName) {
		String baseDevBranch = scmBranchingConfiguration.getDevelopmentBranch();
		String uri = ScmUtils.getScmUri(project.getScm());
		String localPath = project.getBasedir().getAbsolutePath();

		String newFeatureName = getFullNameOfFeature(scmBranchingConfiguration.getFeatureBranchPrefix(), featureName);

		try {
			scmService = new GitScmService(uri, localPath, userConfiguration);

			boolean branchExists = scmService.isBranchExists(newFeatureName);
			if (branchExists) {
				logger.error("Branch with name [" + newFeatureName + "] already exists");
				return false;
			}

			scmService.checkoutBranch(baseDevBranch);
			scmService.createBranch(newFeatureName);
			scmService.checkoutBranch(newFeatureName);
			/*
			 * we won't publish/push it unless for sharing
			 */
		} catch (Exception e) {
			logger.error("error occured while creating a feature, with name: [" + featureName + "]");
			return false;
		}
		return true;
	}
	public boolean publishFeature(MavenProject project, String featureName) {

		String uri = ScmUtils.getScmUri(project.getScm());
		String localPath = project.getBasedir().getAbsolutePath();

		String featureFullName = getFullNameOfFeature(scmBranchingConfiguration.getFeatureBranchPrefix(), featureName);

		try {
			userConfiguration.setGitUsername("musema.hassen@gmail.com");
			userConfiguration.setGitPassword("musads2555");
			scmService = new GitScmService(uri, localPath, userConfiguration);
			boolean isPublished=scmService.publishBranch(featureFullName);
			if(isPublished) {
				logger.info("feature is published, you can tell others to contribute to it.");
			}
			else{
				logger.error("error occured while publishin a feature, with name: [" + featureFullName + "]");
				return false;
			}
			
		} catch (Exception e) {
			logger.error("error occured while publishing a feature, with name: [" + featureFullName + "]");
			return false;
		}
		return true;
	}
	public void finishFeature(MavenProject project, String featureName, Log log) {
		

	}

	public String getFullNameOfFeature(String prefix, String featureName) {
		String featurePrefix = prefix == null ? Constants.WORKFLOW_DEFAULT_FEATURE_BRANCH_PREFIX : prefix;
		String newFeatureName = featurePrefix + featureName;
		return newFeatureName;
	}

}
