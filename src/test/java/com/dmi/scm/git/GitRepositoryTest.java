package com.dmi.scm.git;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.service.git.RepositoryAndCloneService;
import com.dmi.plugin.util.UserConfiguration;

public class GitRepositoryTest {
	final static Logger logger = Logger.getLogger(GitScmService.class);
	
	@SuppressWarnings("unused")
	private static GitScmService scmService=null;
	//private static ScmBranchingConfiguration scmBranchingConfiguration=new ScmBranchingConfiguration();
	private static UserConfiguration userConfiguration=new UserConfiguration();
	private static String uri="scm:git:https://github.com/musema/plugin-test.git";
	private  String localPath="";
	private static String gitUsername="musema.hassen@gmail.com";
	private static String gitPassword="password";
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	 
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		userConfiguration.setGitUsername(gitUsername);
		userConfiguration.setGitPassword(gitPassword);
		localPath=tempFolder.newFolder("gitarea22").getAbsolutePath()+"/.git";
		scmService=new GitScmService(uri,localPath,userConfiguration);
	}

	@After
	public void tearDown() throws Exception {
		tempFolder.delete();
	}

	@Test
	public void testCreateRepo() throws IOException {
		Repository repo=RepositoryAndCloneService.getRepository(uri, localPath);
		assertNotNull(repo);
		logger.info("Current branch is::>>|"+repo.getFullBranch());
		logger.info("LocalPath::>>|"+localPath);
		
	}
	@Test
	public void testOpenRepo() {
		
	}
}
