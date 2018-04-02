package com.dmi.plugin.util;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;


public class MavenCommandExecutor {
	private final Invoker commandInvoker;
	public MavenCommandExecutor() {
		Invoker myInvoker=new DefaultInvoker();
		commandInvoker=myInvoker;
	}
	public MavenCommandExecutor(File localRepo) {
		Invoker myInvoker=new DefaultInvoker();
		myInvoker.setLocalRepositoryDirectory(localRepo);
		commandInvoker=myInvoker;
	}
	public void executeMavenGoals(List<String> goals)   throws MavenInvocationException, MojoFailureException {
		InvocationRequest request=new DefaultInvocationRequest();
		request.setGoals(goals);
		
		InvocationResult result=commandInvoker.execute(request);
		int exitCode=result.getExitCode();
		setInputHandler(request);
		if(exitCode!=0) {
			 if ( result.getExecutionException() != null )
		        {
				 throw new MavenInvocationException("Maven goal execution failure inside:"+
		        (getClass().getName()),result.getExecutionException() );
		        }
		        else
		        {
		        	 throw new MavenInvocationException("Maven goal execution failure inside:"+
		     		        (getClass().getName())+" exitCode is:"+exitCode );
		        }
		}
		
		
	}
	public void executeMavenGoal(String goal) throws MavenInvocationException , MojoFailureException {
		
		executeMavenGoals(Collections.singletonList(goal));
	}
	public void setInputHandler(InvocationRequest request) {
		
	}

}