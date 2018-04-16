/**
 * 
 */
package com.dmi.scm.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.util.Constants;

/**
 * @author musema
 *
 */
public class BranchServiceTest extends AbstractBaseScmTestSetup {
	
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
	public void testCreateBranch() throws IOException, GitAPIException {
		
		String newBranchName="newtestbranch1";
		scmService.createBranch(newBranchName);
				
		Ref newRef=git.getRepository().findRef(newBranchName);
		
		assertNotNull(newRef);
		assertNotNull(newRef.getObjectId());
		
		List<Ref> branchRefs=git.branchList().setListMode(ListMode.ALL).call();
		
		String expectedFullBranchName=Constants.GIT_DEFAULT_REFS_HEADS+newBranchName;
		
		long b=branchRefs.stream()
				.filter(ref->ref.getName().equals(expectedFullBranchName))
				.count();
		
		assertEquals(1,b);
		
	}
	@Test
	public void testCreateAndPushBranch() throws IOException {
		
		String newBranchName="feature/f-ababa";
		scmService.createAndPushBranch(newBranchName);
				
		Ref newRef=git.getRepository().findRef(newBranchName);
		
		assertNotNull(newRef);
		assertNotNull(newRef.getObjectId());
		
	}
	@Test 
	public void testCheckoutBranch() {
		
	}
	@Test 
	public void testDeleteCurrentBranch() throws IOException {
		
		String newBranchName="newbranch";
		scmService.createBranch(newBranchName);
		boolean isDeleted=scmService.deleteBranchFromLocal(newBranchName);
		
		assertTrue(isDeleted);
		
		Ref newRef=git.getRepository().findRef(newBranchName);
		
		assertNull(newRef);
	}
	@Test
	public void testDeleteBranchFromRemote() throws IOException {
		String branchToDelete="feature/f-ababa";
		
		boolean isDeleted=scmService.deleteBranchFromRemote(branchToDelete);
		
		assertTrue(isDeleted);
		
		Ref newRef=git.getRepository().findRef(branchToDelete);
		
		assertNull(newRef);
		
	}
	@Test
	public void testMergeBranch() {
		
	}

}
