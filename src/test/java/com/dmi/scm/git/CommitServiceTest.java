package com.dmi.scm.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmi.plugin.service.git.GitScmService;

public class CommitServiceTest extends AbstractBaseScmTestSetup {
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
	public void testGitStatusIsClean() throws NoWorkTreeException, GitAPIException {
		boolean isClean=git.status().call().isClean();
		
		assertTrue(isClean);
	}
	@Test
	public void testCommitAllChanges() throws NoWorkTreeException, GitAPIException  {
		File file=FileUtils.createFile(localPath, "/testFile1.txt");
		FileUtils.writeToFile(file, "This content will be commited");
		
		boolean isClean=git.status().call().isClean();
		
		assertFalse(isClean);
		
		int untracked=git.status().call().getUntracked().size();
		
		assertEquals(1,untracked);
		
		scmService.stageFiles(".");//stage them first
		
		untracked=git.status().call().getUntracked().size();

		assertEquals(0,untracked);
		
		scmService.commitAllChangesToTackedFiles("Test commit");
		
		isClean=git.status().call().isClean();
		
		assertTrue(isClean);
		
	}
	@Test
	public void testCommitChangesMadeToFilePattern() throws NoWorkTreeException, GitAPIException, IOException  {
		
		
		File file1=FileUtils.createFile(localPath, "/testFile1.txt");
		FileWriter fileWriter1=FileUtils.writeToFile(file1, "This file will be commited");
		
		File file2=FileUtils.createFile(localPath, "/testFile2.txt");
		FileUtils.writeToFile(file2, "This file will not be commited");
		
		boolean isClean=git.status().call().isClean();	
		int untracked=git.status().call().getUntracked().size();
		int uncommited=git.status().call().getUncommittedChanges().size();
		
		assertFalse(isClean);
		assertEquals(2,untracked);
		assertEquals(0,uncommited);
		
		scmService.stageFiles(file1.getName());
		scmService.commitStagedFilesOnly("Test commit changes to a pattern ");
				
		isClean=git.status().call().isClean();
		untracked=git.status().call().getUntracked().size();
		uncommited=git.status().call().getUncommittedChanges().size();
		
		assertFalse(isClean);
		assertEquals(1,untracked);
		assertEquals(0,uncommited);
		
		scmService.stageFiles(file2.getName());
		scmService.commitStagedFilesOnly("Test commiting the second file");
		
		isClean=git.status().call().isClean();
		untracked=git.status().call().getUntracked().size();
		uncommited=git.status().call().getUncommittedChanges().size();
		
		assertTrue(isClean);
		assertEquals(0,untracked);
		assertEquals(0,uncommited);
		
		
		fileWriter1.write("I will make the file dirty");
		fileWriter1.flush();
		
		scmService.stageFiles(file1.getName());
		
		isClean=git.status().call().isClean();
		untracked=git.status().call().getUntracked().size();
		uncommited=git.status().call().getUncommittedChanges().size();
		
		assertFalse(isClean);
		assertEquals(0,untracked);
		assertEquals(1,uncommited);
		
	}

}
