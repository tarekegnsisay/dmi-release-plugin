package com.dmi.scm.git;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.dmi.plugin.service.git.RepositoryAndCloneService;
import com.dmi.plugin.util.Constants;

public class GitRepositoryTest extends AbstractBaseScmTestSetup {

	@Before
	public void setUp() throws Exception {
		localPath=tempFolder.newFolder("gitarea").getAbsolutePath();
	}

	@After
	public void tearDown() throws Exception {
		tempFolder.delete();
	}

	@Test
	public void testCreateRepository() throws IOException {
		Repository repo=RepositoryAndCloneService.getRepository(uri, localPath);
		
		assertNotNull(repo);
		assertNotNull(repo.findRef(Constants.GIT_REFS_HEAD));
		
		Ref head=repo.findRef(Constants.GIT_REFS_HEAD);
		assertNotNull(head.getObjectId());

	}
	@Test
	public void testCloneRepository() throws IOException {
		Repository repo=RepositoryAndCloneService.cloneRepository(uri, localPath);
		assertNotNull(repo);
		assertNotNull(repo.findRef(Constants.GIT_REFS_HEAD));
	}
}
