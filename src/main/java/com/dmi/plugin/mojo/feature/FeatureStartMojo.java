package com.dmi.plugin.mojo.feature;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.FeatureService;
import com.dmi.plugin.util.Constants;

@Mojo(name="feature-start",aggregator=true)
public class FeatureStartMojo extends AbstractApplicationMojo{

	private FeatureService featureService;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		featureService=new FeatureService(project,scmBranchingConfiguration,userConfiguration);
			
		boolean isConfirmed=confirmAction("Do you really wants to start a new feature? Enter [ yes ] to continue");
		
		if(isConfirmed)
		{
			String featureName=acceptStringInput("Enter new feature name");
			
			boolean status=featureService.createFeature(featureName);
			
			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
			else {
				
				getLog().info("Your branch was created? check everthing is OK");
			}
		}
		
	}

}