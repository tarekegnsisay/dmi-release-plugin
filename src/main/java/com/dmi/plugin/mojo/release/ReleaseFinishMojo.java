package com.dmi.plugin.mojo.release;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.ReleaseService;
import com.dmi.plugin.util.Constants;

@Mojo(name="release-finish",aggregator=true)
public class ReleaseFinishMojo extends AbstractApplicationMojo{
	
	private ReleaseService releaseService;

	public void execute() throws MojoExecutionException, MojoFailureException {
		
		releaseService=new ReleaseService(project,scmBranchingConfiguration,userConfiguration);

		boolean isConfirmed=confirmAction("Do you really wants to finish a release? Enter [ yes ] to continue");

		if(isConfirmed)
		{
			String releaseName=acceptStringInput("Enter new release name");

			boolean status=releaseService.finishRelease(releaseName);

			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
		}

	}

}
