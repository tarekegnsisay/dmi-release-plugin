package com.dmi.plugin.mojo.hotfix;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.dmi.plugin.mojo.AbstractApplicationMojo;
import com.dmi.plugin.service.HotfixService;
import com.dmi.plugin.util.Constants;

@Mojo(name="hotfix-finish")

public class HotfixFinishMojo extends AbstractApplicationMojo{

	private HotfixService hotfixService=new HotfixService();

public void execute() throws MojoExecutionException, MojoFailureException {
		
		hotfixService=new HotfixService(project,scmBranchingConfiguration,userConfiguration);
		
		boolean isConfirmed= confirmAction("Do you really wants to [finish] a hotfix? Enter [ yes ] to continue");
		
		if(isConfirmed) {
			
			String hotfixName=acceptStringInput("Enter existing hotfix name you want to finish:");
			
			boolean status=hotfixService.finishHotfix(hotfixName);
			
			if(!status) {
				throw new MojoFailureException(Constants.MAVEN_GENERIC_MOJO_FAILURE_EXCCEPTION_MESSAGE);
			}
		}
	}

}