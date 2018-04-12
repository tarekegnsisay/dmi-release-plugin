package com.dmi.plugin.mojo.feature;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.FeatureService;

@Mojo(name="feature-finish")
public class FeatureFinishMojo extends AbstractApplicationMojo{

	private FeatureService featureService=new FeatureService();

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Finishing new feature...:");

		/*
		 * find feature name and pass it to the service 
		 */
		String featureName="";
		featureService.finishFeature(project,featureName,getLog());
		getLog().info("Feature branch should be merged and deleted OK");
	}

}