package com.dmi.plugin.service.git;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;

public class MergeService {
	final static Logger logger = Logger.getLogger(MergeService.class);
	public static void mergeAndDeleteBranches(Git git,String destination, String source) {
		try {
			
			git.checkout().setName(destination).setCreateBranch(false).call();
			
			ObjectId obj=git.getRepository().resolve(source);
			MergeCommand mergeCommand=git.merge();
			mergeCommand.include(obj);
			
			MergeResult mergeResult=mergeCommand.call();
			MergeResult.MergeStatus status=mergeResult.getMergeStatus();
			if(status.equals(MergeStatus.CONFLICTING)) {
				logger.error(mergeResult.getConflicts().toString());
			}
			else if(status.isSuccessful()){
				BranchAndTagService.deleteBranch(git, source);
				
			}
		}  catch (GitAPIException | IOException e) {
			logger.error("unable to merge branches, destination: [ "+destination+" ] and source: [ "+source+" ]"+e.getMessage());
		}
}


}
