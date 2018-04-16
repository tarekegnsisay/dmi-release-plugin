package com.dmi.plugin.service.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

public class TagService {
	
	public static void createTag(Git git) throws ConcurrentRefUpdateException, InvalidTagNameException, NoHeadException, GitAPIException {
		@SuppressWarnings("unused")
		RevCommit commit = git.commit().setMessage("Commit message").call();
		@SuppressWarnings("unused")
		RevTag tag = (RevTag) git.tag().setName("tag-name").call();
	}

}
