package com.dmi.plugin.mojo.release;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.ReleaseService;
import com.dmi.plugin.util.Constants;

@Mojo(name="release-start",aggregator=true)
public class ReleaseStartMojo extends AbstractApplicationMojo{

	private ReleaseService releaseService;

	public void execute() throws MojoExecutionException, MojoFailureException {
		
		releaseService=new ReleaseService(project,scmBranchingConfiguration,userConfiguration);

		boolean isConfirmed=confirmAction("Do you really wants to start a new release? Enter [ yes ] to continue");

		if(isConfirmed)
		{
			String releaseName=acceptStringInput("Enter new release name");

			boolean status=releaseService.startRelease(releaseName);

			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
			else {

				getLog().info("Your branch was created? check everthing is OK");
			}
		}

	}

}
