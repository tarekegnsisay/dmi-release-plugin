package com.dmi.plugin.mojo.hotfix;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractConfiguratorMojo;
import com.dmi.plugin.service.HotfixService;

@Mojo(name="hotfix-finish")
public class HotfixFinishMojo extends AbstractConfiguratorMojo{

	private HotfixService hotFixService=new HotfixService();

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("finishing hotfix branch...");

		getLog().info(scmBranchingConfiguration.getHotFixBranchPrefix());

		hotFixService.finishHotfix(project,getLog());

		getLog().info("Hotfix branch has to be merged and deleted? check everthing is OK");
	}

}