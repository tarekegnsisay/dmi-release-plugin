package com.dmi.plugin.mojo.hotfix;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.HotfixService;
import com.dmi.plugin.util.Constants;

@Mojo(name="hotfix-start")
public class HotfixStartMojo extends AbstractApplicationMojo{

	private HotfixService hotfixService=new HotfixService();
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		hotfixService=new HotfixService(project,scmBranchingConfiguration,userConfiguration);
		
		boolean isConfirmed= confirmAction("Do you really wants to start a hotfix branch? Enter [ yes ] to continue");
		
		if(isConfirmed) {
		
			String tagName=acceptStringInput("Enter a tagName you wanted to create hotfix branch from");
			
			String hotfixName=acceptStringInput("enter new hotfix name to be created");
			
			boolean status=hotfixService.startHotfix(hotfixName, tagName);
			
			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
			else {
				getLog().info("Hotfix branch is created in local repository, you can publish is when ready. ");
			}
		}
		
	}

}