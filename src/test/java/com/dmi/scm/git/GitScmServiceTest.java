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

public class GitScmServiceTest extends AbstractBaseScmTestSetup {
	
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
		boolean gitStatus=git.status().call().isClean();
		assertTrue(gitStatus);
	}
	@Test
	public void testGitStatusIsNotClean() throws IOException, NoWorkTreeException, GitAPIException {

		File file=FileUtils.createFile(localPath, "/testFile.txt");
		FileUtils.writeToFile(file,"Hello DMI, I am a test content");
		
		System.out.println("new file is created at:"+file.getAbsolutePath());
		System.out.println("git repo directory is:"+git.getRepository().getDirectory().getParentFile());
		
		boolean gitStatus=git.status().call().isClean();
		assertFalse(gitStatus);
	}
	
	@Test
	public void testNumberOfUntrackedFiles() throws IOException, NoWorkTreeException, GitAPIException {
		File testFile1=new File(localPath+"/testFile1.txt");
		fileWriter=new FileWriter(testFile1);
		fileWriter.write("Hello DMI, I am file1");
		int noOfUntracked=0;
		noOfUntracked=git.status().call().getUntracked().size();
		assertEquals(1,noOfUntracked);
		
		File testFile2=new File(localPath+"/testFile2.txt");
		fileWriter=new FileWriter(testFile2);
		fileWriter.write("Hello DMI, I am file2");
		noOfUntracked=git.status().call().getUntracked().size();
		assertEquals(2,noOfUntracked);
	}
}
