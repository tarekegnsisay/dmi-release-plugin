package com.dmi.plugin.mojo.release;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.ReleaseService;

@Mojo(name="finish")
public class ReleaseFinishMojo extends AbstractApplicationMojo{

	private ReleaseService releaseService=new ReleaseService();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Finishing a release...");
		releaseService.finishRelease(project,null, getLog());
		getLog().info("ReleaseService:startRelease returned");
	}

}
