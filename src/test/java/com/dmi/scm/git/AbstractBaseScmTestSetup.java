package com.dmi.scm.git;

import java.io.FileWriter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.UserConfiguration;

public abstract class AbstractBaseScmTestSetup {
	
	protected static GitScmService scmService=null;
	protected static UserConfiguration userConfiguration=new UserConfiguration();
	
	protected static String gitUsername="musema.hassen@gmail.com";
	protected static String gitPassword="password1";
	
	protected  String uri="https://github.com/musema/plugin-test.git";
	protected   String localPath="";
	
	static Git git=null;
	
	RevWalk revWalk=null;
	
	FileWriter fileWriter=null;
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	static {
		userConfiguration.setGitUsername(gitUsername);
		userConfiguration.setGitPassword(gitPassword);
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

}
