package com.dmi.plugin.service;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.scm.GitScmManagerService;
import com.dmi.plugin.util.LoggingUtils;
import com.dmi.plugin.util.ScmUtils;

public class FeatureService extends AbstractApplicationService{
	private GitScmManagerService scmService=null;
	public void createFeature(MavenProject project,Log logger) {
		String baseDevBranch="develop";//will be configuration param later
		String uri=ScmUtils.getScmUri(project.getScm(),logger);
		String localPath=project.getBasedir().getAbsolutePath();

		String newFeatureName=getNewFeatureName();
		try {
			scmService=new GitScmManagerService(uri,localPath);
			scmService.checkoutBranch(baseDevBranch);
			//			scmService.createBranch(newFeatureName);
			//			scmService.checkoutBranch(newFeatureName);
			scmService.pushNewBranch(newFeatureName);
		}
		finally {
			LoggingUtils.printLog(scmService==null?null:scmService.getLog(),logger);
		}

	}
	public String getNewFeatureName() {
		String featurePrefix="feature-";
		/*
		 * TBD
		 */
		String featureName="feature-"+ (int)(Math.random()*100);
		String newFeatureName=featurePrefix+featureName;
		/*
		 * check if the a branch with this name exists
		 */
		return newFeatureName;
	}
	public void finishFeature(MavenProject project, String featureName, Log log) {
		// TODO Auto-generated method stub

	}

}
