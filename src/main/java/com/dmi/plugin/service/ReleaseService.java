package com.dmi.plugin.service;

import java.util.Calendar;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.dmi.plugin.scm.GitScmManagerService;
import com.dmi.plugin.util.LoggingUtils;
import com.dmi.plugin.util.MavenCommandExecutor;
import com.dmi.plugin.util.ScmUtils;

public class ReleaseService {
	private Log logger;
	private GitScmManagerService scmManagerService;
	 /** Will perform release start tasks
	 * @param project
	 * @param logger
	 */
	public void startRelease(MavenProject project, Log logger) {
		
		this.logger=logger;
		LoggingUtils.logProject(project,logger);
		
		String uri=ScmUtils.getScmUri(project.getScm(),this.logger);
		String localPath=project.getBasedir().getAbsolutePath();
		
		scmManagerService=new GitScmManagerService(uri,localPath);
				
		scmManagerService.pullRepo();
		this.logger.info("Pulling latest updates from git...");
		
		String releaseBranchName=calculateReleaseBranchName();
		scmManagerService.createBranch(releaseBranchName);
		
		scmManagerService.checkoutBranch(releaseBranchName);
		
		String replaceSNAPSHOTString="versions:force-releases -Dincludes=com.adesa.tdd:* -DprocessDependencies=true";
		String useLatestVersion="versions:use-latest-releases -Dincludes=com.adesa.tdd:* -DprocessDependencies=true";
		String commitChanges="versions:commit";
		
		MavenCommandExecutor commandExecutor=new MavenCommandExecutor();
		
		try {
			this.logger.info("Removing SNAPSHOT from Dependencies...");
			commandExecutor.executeMavenGoal(replaceSNAPSHOTString);
			commandExecutor.executeMavenGoal(useLatestVersion);
			commandExecutor.executeMavenGoal(commitChanges);
		} catch (MavenInvocationException e) {
			this.logger.error("MavenInvocationException");
		} catch (MojoFailureException e) {
			this.logger.error("MojoFailureException");
		}
		scmManagerService.commitChanges("Setting latest dependency versions for release");
		//scmManagerService.pushAllBranches();
		scmManagerService.pullRepo();
		this.logger.info("Release start setup completed successfully");
	}
	public void finishRelease(MavenProject project, String releaseBranchName, Log logger) {
		this.logger=logger;
		logger.info("Finishing release...");
		LoggingUtils.logProject(project,logger);
		
		String uri=ScmUtils.getScmUri(project.getScm(),this.logger);
		String localPath=project.getBasedir().getAbsolutePath();
		scmManagerService=new GitScmManagerService(uri,localPath);		
		
		
		this.logger.info("Pulling latest updates from git...");
		scmManagerService.checkoutBranch("master");
		scmManagerService.mergeBranches("master",releaseBranchName);
		//check all commits of release branches are in master
		
	}
	private String calculateReleaseBranchName() {
		String releaseName="release-";
		Calendar calendar=Calendar.getInstance();
		releaseName.concat(Integer.toString(calendar.get(Calendar.YEAR)));
		releaseName.concat(Integer.toString(calendar.get(Calendar.MONTH)));
		return releaseName;
	}

}
