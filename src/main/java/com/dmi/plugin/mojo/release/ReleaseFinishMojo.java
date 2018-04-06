package com.dmi.plugin.mojo.release;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractConfiguratorMojo;
import com.dmi.plugin.service.ReleaseService;

@Mojo(name="start")
public class ReleaseFinishMojo extends AbstractConfiguratorMojo{

	private ReleaseService releaseService=new ReleaseService();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Finishing a release...");
		releaseService.finishRelease(project,null, getLog());
		getLog().info("ReleaseService:startRelease returned");
	}

}
