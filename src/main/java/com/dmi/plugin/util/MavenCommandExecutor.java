package com.dmi.plugin.util;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;


public class MavenCommandExecutor {
	
	final static Logger logger = Logger.getLogger(MavenCommandExecutor.class);
	
	private final Invoker commandInvoker;

	public MavenCommandExecutor() {
		Invoker tempInvoker=new DefaultInvoker();
		commandInvoker=tempInvoker;	
	}
	public MavenCommandExecutor(File localPath) {
		Invoker tempInvoker=new DefaultInvoker();
		tempInvoker.setLocalRepositoryDirectory(localPath);
		commandInvoker=tempInvoker;
	}
	public boolean executeMavenGoals(List<String> goals)  {

		InvocationRequest request=new DefaultInvocationRequest();
		
		logger.info("here are list of goals to be evaluated:");
		logger.info(goals);
		
		request.setGoals(goals);
		InvocationResult result = null;
		try {
			result = commandInvoker.execute(request);
		} catch (MavenInvocationException e) {
			logger.error("maven invocation exception during release version changes");
		}
		
		int exitCode=0;
		if(result!=null)
			exitCode=result.getExitCode();
				
		if(exitCode!=0) {
			if ( result.getExecutionException() != null )
			{
				logger.error("maven invocation exception during version changes, "+ result.getExecutionException() );
			}
			else
			{
				logger.error("Maven goal execution failure inside: exitCode is:"+exitCode );
			}
		}

		return true;
	}
	public void executeMavenGoal(String goal) throws MavenInvocationException , MojoFailureException {

		executeMavenGoals(Collections.singletonList(goal));
	}
	
}
