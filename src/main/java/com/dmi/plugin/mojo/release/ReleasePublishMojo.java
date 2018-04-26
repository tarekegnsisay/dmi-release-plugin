package com.dmi.plugin.mojo.release;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.ReleaseService;
import com.dmi.plugin.util.Constants;

@Mojo(name="release-publish",aggregator=true)
public class ReleasePublishMojo extends AbstractApplicationMojo{

	private ReleaseService releaseService=new ReleaseService();

	public void execute() throws MojoExecutionException, MojoFailureException {
		
		releaseService=new ReleaseService(project,scmBranchingConfiguration,userConfiguration);

		boolean isConfirmed= confirmAction("Do you really wants to publish a release? Enter [ yes ] to continue");

		if(isConfirmed){

			String releaseName=acceptStringInput("Enter release name you wanted to publish");

			boolean status=releaseService.publishRelease(releaseName);

			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
		}
	}

}