package com.dmi.scm.git;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.Constants;

public class MergeServiceTest extends AbstractBaseScmTestSetup {
	@Before
	public void setUp() throws Exception {
		localPath=tempFolder.newFolder("gitarea").getAbsolutePath();
		scmService=new GitScmService(uri,localPath,userConfiguration);
		git=scmService.getGit();
	}

	@After
	public void tearDown() throws Exception {
		tempFolder.delete();
	}

	@Test
	public void testMergeBranches() throws NoWorkTreeException, GitAPIException, IOException  {
		boolean checkout;
		
		String branchToMerge="feature/f-333";
		
		scmService.createAndPushBranch(branchToMerge);
		
		String mergeMessage="Merging new feature to development stream";
		checkout=scmService.checkoutRemoteBranch(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);
		assertTrue(checkout);
		
		
		checkout=scmService.checkoutRemoteBranch(branchToMerge);
		assertTrue(checkout);
		
		scmService.pushBranch(branchToMerge);
		
		scmService.mergeBranches(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH, branchToMerge,mergeMessage);
		
		scmService.pushBranch(branchToMerge);
		scmService.pushBranch( Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);
		scmService.deleteBranchFromRemote(branchToMerge);
	}
}
