package com.dmi.plugin.mojo.feature;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractConfiguratorMojo;
import com.dmi.plugin.service.FeatureService;

@Mojo(name="feature-start")
public class FeatureStartMojo extends AbstractConfiguratorMojo{

	private FeatureService featureService=new FeatureService();

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Creating new feature...feature prefix:"+scmBranchingConfiguration.getFeatureBranchPrefix());
		getLog().info("Creating new feature...git user:"+userConfiguration.getGitUsername());
		featureService.createFeature(project,getLog());
		getLog().info("Your branch was created? check everthing is OK");
	}

}