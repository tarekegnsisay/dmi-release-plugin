package com.dmi.plugin.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Calendar;

import org.apache.maven.model.Scm;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.dmi.plugin.scm.GitScmManagerService;
import com.dmi.plugin.utility.MavenCommandExecutor;
import com.dmi.plugin.utility.SystemCommandExecutor;

public class ReleaseService {
	private Log logger;
	private GitScmManagerService scmManagerService;
	 /** Will perform release start tasks
	 * @param project
	 * @param logger
	 */
	public void startRelease(MavenProject project, Log logger) {
		
		this.logger=logger;
		logger.info("Release process started");
		logProject(project,logger);
		
		String uri=getScmUri(project.getScm());
		String localPath=project.getBasedir().getAbsolutePath();
		
		scmManagerService=new GitScmManagerService(uri,localPath);
				
		scmManagerService.pullRepo();
		logger.info("Pulling latest updates from git...");
		
		String releaseBranchName=calculateReleaseBranchName();
		scmManagerService.createBranch(releaseBranchName);
		
		scmManagerService.checkoutBranch(releaseBranchName);
		
		String replaceSNAPSHOTString="versions:force-releases -Dincludes=com.adesa.tdd:* -DprocessDependencies=true";
		String useLatestVersion="versions:use-latest-releases -Dincludes=com.adesa.tdd:* -DprocessDependencies=true";
		String commitChanges="versions:commit";
		
		MavenCommandExecutor commandExecutor=new MavenCommandExecutor();
		
		try {
			logger.info("Removing SNAPSHOT from Dependencies...");
			commandExecutor.executeMavenGoal(replaceSNAPSHOTString);
			commandExecutor.executeMavenGoal(useLatestVersion);
			commandExecutor.executeMavenGoal(commitChanges);
		} catch (MavenInvocationException e) {
			logger.error("MavenInvocationException");
		} catch (MojoFailureException e) {
			logger.error("MojoFailureException");
		}
		scmManagerService.commitChanges("Setting latest dependency versions for release");
		
		//scmManagerService.pushAllBranches();
		scmManagerService.pullRepo();
		logger.info("Release start setup completed successfully");
	}

	

	private String calculateReleaseBranchName() {
		String releaseName="release-";
		Calendar calendar=Calendar.getInstance();
		releaseName.concat(Integer.toString(calendar.get(Calendar.YEAR)));
		releaseName.concat(Integer.toString(calendar.get(Calendar.MONTH)));
		return releaseName;
	}



	public void excuteCommand(Log logger) {
		try {
			SequenceInputStream sis=SystemCommandExecutor.executeCommand();
			BufferedReader br=new BufferedReader(new InputStreamReader(sis));
			
			String line="";
			while((line=br.readLine())!=null) {
				logger.info(line);
			}
			if(br.ready()) {
				while((line=br.readLine())!=null) {
					logger.info(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void executeCommandsWithMavenInvoker(Log logger) {
		MavenCommandExecutor mavenCommandExecutor=new MavenCommandExecutor();
		try {
			mavenCommandExecutor.executeMavenGoal("versions:set -DnewVersion=3.0.0-SNAPSHOT");
		} catch (MavenInvocationException e) {
			logger.error(e);
		}
		catch(MojoFailureException e) {
			logger.error(e);
		}
		logger.info("maven invoker was called here");
	}
	private void logProject(MavenProject project, Log logger) {
		
		logger.info("Project version: "+project.getVersion());
		logger.info("Project URI: "+project.getVersion());
		logger.info("Project base directory: "+project.getBasedir());
		logger.info("Project base directory: AbsolutePath: "+project.getBasedir().getAbsolutePath());
		
	}
	private String getScmUri(Scm scm) {
		String uri="",scmConnection="";
		if(scm!=null) {
			scmConnection=scm.getConnection();
			scmConnection=scmConnection.trim();
			int index=scmConnection.indexOf("https");
			index=index>=0?index:scmConnection.indexOf("git@");
			index=index>=0?index:scmConnection.indexOf("http");
			uri=scmConnection.substring(index);
			if(uri.isEmpty()) {
				logger.error(" scm url inside pom must be set");
				return null;
			}
			logger.info("Repo URI:"+uri);
			//check if valid git repo
			//scmManagerService.validateRepo(uri);
			return uri;
			
		}
		else {
			logger.error("scm element in pom.xm must be provided");
			return null;
		}
	
	}
}
