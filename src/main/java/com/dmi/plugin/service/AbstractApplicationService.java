package com.dmi.plugin.service;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.Constants;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.StringUtils;
import com.dmi.plugin.util.UserConfiguration;

public class AbstractApplicationService {
	protected GitScmService scmService = null;
	protected ScmBranchingConfiguration scmBranchingConfiguration;
	protected UserConfiguration userConfiguration;

	public AbstractApplicationService() {
	}

	public AbstractApplicationService(ScmBranchingConfiguration scmBranchingConf, UserConfiguration userConfiguration) {

		scmBranchingConfiguration = scmBranchingConf;

		if (scmBranchingConfiguration == null) {
			scmBranchingConfiguration = new ScmBranchingConfiguration();
			scmBranchingConfiguration.setMasterBranch(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH);
			scmBranchingConfiguration.setDevelopmentBranch(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);
			scmBranchingConfiguration.setFeatureBranchPrefix(Constants.WORKFLOW_DEFAULT_FEATURE_BRANCH_PREFIX);
			scmBranchingConfiguration.setReleaseBranchPrefix(Constants.WORKFLOW_DEFAULT_RELEASE_BRANCH_PREFIX);
			scmBranchingConfiguration.setHotfixBranchPrefix(Constants.WORKFLOW_DEFAULT_HOTFIX_BRANCH_PREFIX);
			scmBranchingConfiguration.setTagPrefix(Constants.WORKFLOW_DEFAULT_TAG_PREFIX);
		} 
		else {
			checkAndSetDefaults();
		}
		/*
		 * more on user configuration
		 */
		this.userConfiguration = userConfiguration;

	}

	private void checkAndSetDefaults() {
		if (StringUtils.isEmptyOrNull(scmBranchingConfiguration.getMasterBranch())) {
			scmBranchingConfiguration.setMasterBranch(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH);
		}
		if (StringUtils.isEmptyOrNull(scmBranchingConfiguration.getDevelopmentBranch())) {
			scmBranchingConfiguration.setDevelopmentBranch(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);
		}
		if (StringUtils.isEmptyOrNull(scmBranchingConfiguration.getFeatureBranchPrefix())) {
			scmBranchingConfiguration.setFeatureBranchPrefix(Constants.WORKFLOW_DEFAULT_FEATURE_BRANCH_PREFIX);
		}
		if (StringUtils.isEmptyOrNull(scmBranchingConfiguration.getReleaseBranchPrefix())) {
			scmBranchingConfiguration.setReleaseBranchPrefix(Constants.WORKFLOW_DEFAULT_RELEASE_BRANCH_PREFIX);
		}
		if (StringUtils.isEmptyOrNull(scmBranchingConfiguration.getHotfixBranchPrefix())) {
			scmBranchingConfiguration.setHotfixBranchPrefix(Constants.WORKFLOW_DEFAULT_HOTFIX_BRANCH_PREFIX);
		}
		if (StringUtils.isEmptyOrNull(scmBranchingConfiguration.getTagPrefix())) {
			scmBranchingConfiguration.setTagPrefix(Constants.WORKFLOW_DEFAULT_TAG_PREFIX);
		}
	}

}
