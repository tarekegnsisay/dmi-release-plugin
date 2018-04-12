package com.dmi.plugin.mojo.feature;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.FeatureService;


@Mojo(name="feature-start")
public class FeatureStartMojo extends AbstractApplicationMojo{

	private FeatureService featureService;

	public void execute() throws MojoExecutionException, MojoFailureException {
		featureService=new FeatureService(scmBranchingConfiguration,userConfiguration);
		getLog().info("Creating new feature...usercredentials:"+userConfiguration.getGitUsername()+";"+userConfiguration.getGitPassword());
		
		String response= promptUser("Do you really wants to start a new feature? Enter [ yes ] to continue \n");
		
		if(!response.equalsIgnoreCase("yes")) {
			response= promptUser("You've entered:[ "+response+" ] please enter [ yes ] to continue \n");
			
		}
		if(!response.equalsIgnoreCase("yes")) {
			
			getLog().info("Task aborted, you've  entered:[ "+response+" ]");
		}
		else {
			getLog().info("You've chosen:"+response);
			String featureName=promptUser("Enter new feature name: \n");
			boolean status=featureService.createFeature(project,featureName,getLog());
			if(status) {
				getLog().info("Your branch was created? check everthing is OK");
			}
			else {
				getLog().info("Something went wrong, please check error log and try again");
			}
		}
		
	}

}