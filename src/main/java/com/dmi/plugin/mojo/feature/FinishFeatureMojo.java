package com.dmi.plugin.mojo.feature;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.FeatureService;

@Mojo(name="feature-finish")
public class FinishFeatureMojo extends AbstractApplicationMojo{

	private FeatureService featureService=new FeatureService();

public void execute() throws MojoExecutionException, MojoFailureException {
		
		featureService=new FeatureService(scmBranchingConfiguration,userConfiguration);
		
		String response= promptUser("Do you really wants to [finish] a feature? Enter [ yes ] to continue");
		
		if(!response.equalsIgnoreCase("yes")) {
			response= promptUser("You've entered:[ "+response+" ] please enter [ yes ] to continue");
			
		}
		if(!response.equalsIgnoreCase("yes")) {
			
			getLog().info("Task aborted, you've  entered:[ "+response+" ]");
		}
		else {
			
			String featureName=promptUser("Enter feature name you want to finish:");
			
			boolean status=featureService.finishFeature(project,featureName);
			
			if(!status) {
				getLog().info("Something went wrong, please check error log and try again");
			}
		}
	}

}