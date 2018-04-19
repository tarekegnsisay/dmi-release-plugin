package com.dmi.plugin.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.UserConfiguration;

public abstract class AbstractApplicationMojo extends AbstractMojo {
	@Component
	protected MavenProject project;
	
	@Parameter(defaultValue="${scmBranchingConfiguration}")
	protected ScmBranchingConfiguration scmBranchingConfiguration;
	
	@Parameter(defaultValue="${userConfiguration}")
	protected UserConfiguration userConfiguration;
	
	
	@Component
	protected Prompter prompt;
	
	public String promptUser(String message) {
		String response="";
		try {
			response=prompt.prompt(message,"\n");
		} catch (PrompterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	

}
