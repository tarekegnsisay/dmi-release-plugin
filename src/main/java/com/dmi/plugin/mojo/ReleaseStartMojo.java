package com.dmi.plugin.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.service.ReleaseService;

@Mojo(name="start", defaultPhase = LifecyclePhase.SITE_DEPLOY )
public class ReleaseStartMojo extends AbstractMojo{

	@Parameter(defaultValue="${project}")
	private MavenProject project;
	
	private ReleaseService releaseService=new ReleaseService();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Release start started::");
		
		releaseService.startRelease(project,getLog());
		//releaseService.executeCommandsWithMavenInvoker(getLog());
		
		getLog().info("Release start Finished::Command invoker executed");
	}

}
