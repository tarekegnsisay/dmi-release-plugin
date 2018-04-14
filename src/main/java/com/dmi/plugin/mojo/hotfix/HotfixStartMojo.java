package com.dmi.plugin.mojo.hotfix;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.HotfixService;

@Mojo(name="hotfix-start")
public class HotfixStartMojo extends AbstractApplicationMojo{

	private HotfixService hotFixService=new HotfixService();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Creating hotfix branch...");
		getLog().info(scmBranchingConfiguration.getHotFixBranchPrefix());
		
		hotFixService.createHotFix(project,getLog());
		
		getLog().info("Your branch was created? check everthing is OK");
	}

}