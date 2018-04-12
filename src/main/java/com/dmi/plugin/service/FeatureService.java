package com.dmi.plugin.service;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.scm.GitScmManagerService;
import com.dmi.plugin.util.LoggingUtils;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.ScmUtils;
import com.dmi.plugin.util.UserConfiguration;

public class FeatureService extends AbstractApplicationService{
	
	public FeatureService(ScmBranchingConfiguration scmBranchingConfiguration, UserConfiguration userConfiguration) {
		super(scmBranchingConfiguration,userConfiguration);
	}

	public FeatureService() {}

	public boolean createFeature(MavenProject project,String featureName,Log logger) {
		boolean status=true;
		String baseDevBranch=scmBranchingConfiguration.getDevelopmentBranch();
		String uri=ScmUtils.getScmUri(project.getScm(),logger);
		String localPath=project.getBasedir().getAbsolutePath();
		
		
		String newFeatureName=getNewFeatureName(scmBranchingConfiguration.getFeatureBranchPrefix(),featureName);
		
		
		try {
			scmService=new GitScmManagerService(uri,localPath,userConfiguration);
			
			boolean branchExists=scmService.isBranchExists(newFeatureName);
			if(branchExists) {
				logger.error("Branch with name [ "+newFeatureName+ "  ] exists");
				return false;
			}
			
			scmService.checkoutBranch(baseDevBranch);
			scmService.createBranch(newFeatureName);
			scmService.checkoutBranch(newFeatureName);
			scmService.pushNewBranch(newFeatureName);
		}
		finally {
			LoggingUtils.printLog(scmService==null?null:scmService.getLog(),logger);
		}
		return status;
	}

	public void finishFeature(MavenProject project, String featureName, Log log) {
		// TODO Auto-generated method stub

	}
	public String getNewFeatureName(String prefix, String featureName) {
		String featurePrefix="feature-";
		featurePrefix=prefix==null?"feature/":prefix;
		/*
		 * TBD
		 */
		String newFeatureName=featurePrefix+featureName;
		/*
		 * check if the a branch with this name exists
		 */
		return newFeatureName;
	}

}
