package com.dmi.plugin.mojo.feature;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.FeatureService;
import com.dmi.plugin.util.Constants;

@Mojo(name="feature-finish",aggregator=true)
public class FeatureFinishMojo extends AbstractApplicationMojo{

	private FeatureService featureService;

	public void execute() throws MojoExecutionException, MojoFailureException {

		featureService=new FeatureService(project,scmBranchingConfiguration,userConfiguration);

		boolean isConfirmed= confirmAction("Do you really wants to [finish] a feature? Enter [ yes ] to continue");

		if(isConfirmed){

			String featureName=acceptStringInput("Enter feature name you want to finish");

			boolean status=featureService.finishFeature(featureName);

			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
		}
		else{
			getLog().info("Task aborted, confirmation needed");
		}
	}
}