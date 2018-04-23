package com.dmi.scm.git;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmi.plugin.service.git.GitScmService;
import com.dmi.plugin.service.git.TagService;
import com.dmi.plugin.util.Constants;

public class TagServiceTest extends AbstractBaseScmTestSetup{
	String tagName="";
	String tagMessage="";
	PersonIdent tagger=null;

	@Before
	public void setUp() throws Exception {

		localPath=tempFolder.newFolder("gitarea").getAbsolutePath();
		scmService=new GitScmService(uri,localPath,userConfiguration);
		git=scmService.getGit();
		
		tagger=new PersonIdent("musema","mhassen@dminc.com");
		tagName="tag";
		tagMessage="This a tag is created at: "+new Date().toString()+" created by "+tagger.toString();
		tagger=new PersonIdent("musema","mhassen@dminc.com");

	}

	@After
	public void tearDown() throws Exception {
		tempFolder.delete();
	}
	@Test
	public void testTagMasterBranch() throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		tagName+="-3.0.0-diaz";
		scmService.checkoutBranch(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH);

		ObjectId objectId=git.getRepository().resolve((Constants.GIT_REFS_HEAD));

		boolean isCreated=scmService.createTag(objectId, tagger, tagName, tagMessage);
		boolean isExists=TagService.isTagExists(git, tagName);

		assertTrue(isExists);
		assertTrue(isCreated);
	}
	@Test
	public void testTagCurrentDevelopmentBranch() throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		tagName+="T2";
		scmService.checkoutBranch(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);

		ObjectId objectId=git.getRepository().resolve((Constants.GIT_REFS_HEAD).trim());

		boolean isCreated=scmService.createTag(objectId, tagger, tagName, tagMessage);
		boolean isExists=TagService.isTagExists(git, tagName);

		assertTrue(isExists);
		assertTrue(isCreated);
	}
	
	@Test
	public void testTagOneCommitBeforeCurrentMaster() throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		
		tagName+="T3";
		scmService.checkoutBranch(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH);
		ObjectId objectId=git.getRepository().resolve(Constants.GIT_REFS_HEAD+"~1");

		boolean isCreated=scmService.createTag(objectId, tagger, tagName, tagMessage);
		boolean isExists=TagService.isTagExists(git, tagName);

		assertTrue(isExists);
		assertTrue(isCreated);
	}
	@Test

	public void testDeleteTag() throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		
		tagName+="T4";

		scmService.checkoutBranch(Constants.WORKFLOW_DEFAULT_MASTER_BRANCH);

		ObjectId objectId=git.getRepository().resolve((Constants.GIT_REFS_HEAD));

		boolean isCreated=scmService.createTag(objectId, tagger, tagName, tagMessage);
		boolean isDeleted=scmService.deleteTag(tagName);
		boolean isExists=TagService.isTagExists(git, tagName);

		assertTrue(isCreated);
		assertTrue(isDeleted);
		assertFalse(isExists);
	}
}
