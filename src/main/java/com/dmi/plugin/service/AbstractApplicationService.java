package com.dmi.plugin.service;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.Constants;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.ScmUtils;
import com.dmi.plugin.util.StringUtils;
import com.dmi.plugin.util.UserConfiguration;

public class AbstractApplicationService {

	final static Logger logger = Logger.getLogger(AbstractApplicationService.class);
	
	protected ScmBranchingConfiguration scmBranchingConfiguration;
	protected UserConfiguration userConfiguration;
	
	protected GitScmService scmService = null;
	protected String uri;
	protected String localPath;
	protected MavenProject project;
	
	
	public AbstractApplicationService() {
	}

	public AbstractApplicationService(MavenProject mavenProject,ScmBranchingConfiguration scmBranchingConf, UserConfiguration userConfiguration) {

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
		this.project=mavenProject;
		
		if(project==null){
			logger.error("this plugin can be used only with maven projects");
		}
		else{
			logger.info("maven project is detected:"+project.getDescription());
		}
		uri = ScmUtils.getScmUri(project.getScm());

		localPath = project.getBasedir().getAbsolutePath();

		scmService = new GitScmService(uri, localPath, userConfiguration);
		if(scmService==null){
			
		}

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
