package com.dmi.plugin.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.UserConfiguration;

public abstract class AbstractConfiguratorMojo extends AbstractMojo {
	@Component
	protected MavenProject project;
	
	@Parameter(defaultValue="${scmBranchingConfiguration}")
	protected ScmBranchingConfiguration scmBranchingConfiguration;
	
	@Parameter(defaultValue="${userConfiguration}")
	protected UserConfiguration userConfiguration;
	

}
