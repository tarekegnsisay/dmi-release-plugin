package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class CommitService {
	final static Logger logger = Logger.getLogger(CommitService.class);

	public static void commitAllChangesToTackedFiles(Git git,String commitMassage) {

		CommitCommand commitCommand=git.commit().setAll(true);
		commitCommand.setMessage(commitMassage);
		String currentBranch="";
		try {
			currentBranch=commitCommand.getRepository().getBranch();
			commitCommand.call();
		}catch (Exception e) {
			logger.error("error while commiting changes to: "+currentBranch);
		}
	}

	public static void commitStagedFilesOnly(Git git,String commitMassage) {

		String currentBranch="";

		CommitCommand commitCommand;
		commitCommand=git.commit();
		commitCommand.setMessage(commitMassage);

		try {
			currentBranch=commitCommand.getRepository().getBranch();
			commitCommand.call();
		}catch (Exception e) {
			logger.error("error while commiting changes to: "+currentBranch);
		}
	}

	public static RevCommit getCommitByObjectId(Git git,ObjectId objectId) {
		if(objectId==null)
			return null;
		Repository repository=git.getRepository();
		RevCommit revCommit=null;

		if(!RepositoryAndCloneService.hasCommits(repository)) {
			return null;
		}
		try {
			RevWalk revWalk=new RevWalk(repository);
			RevCommit tempRevCommit=revWalk.parseCommit(objectId);
			revCommit=tempRevCommit;
			revWalk.dispose();
			revWalk.close();
		}catch (Exception e) {
			logger.error("error while parsing commit for ObjectId: "+objectId);
		}
		return revCommit;
	}

}
