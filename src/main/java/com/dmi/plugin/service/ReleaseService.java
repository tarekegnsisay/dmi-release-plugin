package com.dmi.plugin.service;

import java.util.Calendar;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.LoggingUtils;
import com.dmi.plugin.util.MavenCommandExecutor;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.ScmUtils;
import com.dmi.plugin.util.UserConfiguration;

public class ReleaseService  extends AbstractApplicationService {
	private Log logger;
	
	public ReleaseService(MavenProject mavenProject,ScmBranchingConfiguration scmBranchingConfiguration, UserConfiguration userConfiguration) {
		super(mavenProject,scmBranchingConfiguration,userConfiguration);
	}

	public ReleaseService() {}
	
	 /** Will perform release start tasks
	 * @param project
	 * @param logger
	 */
	public void startRelease(MavenProject project, Log logger) {
		try {
			this.logger=logger;
			LoggingUtils.logProject(project,logger);
			
			String uri=ScmUtils.getScmUri(project.getScm());
			String localPath=project.getBasedir().getAbsolutePath();
			
			this.logger.info("Inside release service:> uri:"+uri+" & localpath:"+localPath);
			scmService=new GitScmService(uri.trim(),localPath.trim(),userConfiguration);
			this.logger.info("Inside release service:>"+"git ripo created??");	
			scmService.pullRepo();
			this.logger.info("Pulling latest updates from git...");
			
			String releaseBranchName=calculateReleaseBranchName();
			this.logger.info("release branch name::"+releaseBranchName);
			scmService.createBranch(releaseBranchName);
			
			scmService.checkoutBranch(releaseBranchName);
			
			
			
			String replaceSNAPSHOTString="versions:force-releases -Dincludes=com.adesa.tdd:* -DprocessDependencies=true";
			String useLatestVersion="versions:use-latest-releases -Dincludes=com.adesa.tdd:* -DprocessDependencies=true";
			String commitChanges="versions:commit";
			
			MavenCommandExecutor commandExecutor=new MavenCommandExecutor();
			
			try {
				this.logger.info("Removing SNAPSHOT from Dependencies...");
				commandExecutor.executeMavenGoal("--version");
				commandExecutor.executeMavenGoal(replaceSNAPSHOTString);
				commandExecutor.executeMavenGoal(useLatestVersion);
				commandExecutor.executeMavenGoal(commitChanges);
			} catch (MavenInvocationException e) {
				this.logger.error("MavenInvocationException");
			} catch (MojoFailureException e) {
				this.logger.error("MojoFailureException");
			}
			//scmManagerService.commitChanges("Setting latest dependency versions for release");
			//scmManagerService.pushAllBranches();
			scmService.pullRepo();
			this.logger.info("Release start setup completed successfully");
		}
		finally {
			
		}
		
		
	}
	public void finishRelease(MavenProject project, String releaseBranchName, Log logger) {
		this.logger=logger;
		logger.info("Finishing release...");
		LoggingUtils.logProject(project,logger);
		
		String uri=ScmUtils.getScmUri(project.getScm());
		String localPath=project.getBasedir().getAbsolutePath();
		scmService=new GitScmService(uri,localPath,userConfiguration);		
		
		
		this.logger.info("Pulling latest updates from git...");
		scmService.checkoutBranch("master");
		scmService.mergeAndDeleteBranches("master",releaseBranchName);
		//check all commits of release branches are in master
		
	}
	private String calculateReleaseBranchName() {
		String releaseName="release-";
		Calendar calendar=Calendar.getInstance();
		releaseName+=Integer.toString(calendar.get(Calendar.YEAR));
		releaseName+=Integer.toString(calendar.get(Calendar.MONTH));
		releaseName+=Integer.toString(calendar.get(Calendar.HOUR));
		return releaseName;
	}

}
