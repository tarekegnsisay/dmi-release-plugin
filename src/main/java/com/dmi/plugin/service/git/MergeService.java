package com.dmi.plugin.service.git;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

public class MergeService {
	final static Logger logger = Logger.getLogger(MergeService.class);
	
	/*
	 * TODO merge should be able to handle handle merge conflicts in a graceful way 
	 */

	public static boolean mergeBranches(Git git,String destination, String source,String mergeMessage) {
		
		try {
			if(git.getRepository().findRef(source)==null)
				BranchService.checkoutRemoteBranch(git, source);
			if(git.getRepository().findRef(destination)==null)
				BranchService.checkoutRemoteBranch(git, destination);
			
			git.checkout().setName(destination).setCreateBranch(false).call();
			
			Ref sourceRef=git.getRepository().findRef(source);
			
			MergeCommand mergeCommand=git.merge().include(sourceRef).setMessage(mergeMessage);
			
			MergeResult mergeResult=mergeCommand.call();
			
			return checkMergeResult(mergeResult);
			
		}  catch (GitAPIException | IOException e) {
			logger.error("unable to merge branches, destination: [ "+destination+" ] and source: [ "+source+" ]"+e.getMessage());
		}
		return true;
}
	
	public static boolean checkMergeResult(MergeResult mergeResult){
		
		logger.info("merge result mergeResult.getFailingPaths(: "+mergeResult.getConflicts());
		logger.info("merge result mergeResult.getFailingPaths: "+mergeResult.getFailingPaths());
		logger.info("merge result mergeResult.toString()"+mergeResult.toString());
		
		MergeResult.MergeStatus mergeStatus=mergeResult.getMergeStatus();
		logger.info("merge status was: "+mergeStatus.toString());
		
		if(mergeStatus.equals(MergeStatus.CONFLICTING)) {
			logger.error(mergeResult.getConflicts().toString());
		}
		else if(mergeStatus.isSuccessful()){
			//BranchService.deleteBranch(git, source);
			logger.info("successful merge!!!");
			
		}
		return true;
		
	}

}
