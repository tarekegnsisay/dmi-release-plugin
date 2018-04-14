package com.dmi.plugin.service.git;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;

import com.dmi.plugin.util.Constants;

public class BranchAndTagService {
	final static Logger logger = Logger.getLogger(BranchAndTagService.class);

	public static boolean deleteBranch(Git git,String branchToDelete) {
		boolean deleted=false;
		try {
			String currentBranch=git.getRepository().getFullBranch();
			boolean isCheckedout=checkoutBranch(git,Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);
			if(!isCheckedout) {
								logger.error("deletion of branch: [ "+branchToDelete+" ] aborted due to unsuccessful checkout of development branch: [ "+Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH+" ]");
				return false;
			}
			if(branchToDelete.equalsIgnoreCase(currentBranch)) {
				/*
				 * check out another branch and proceed deletion, master,develop?TBD
				 */
			}
			git.branchDelete().setForce(true).setBranchNames(branchToDelete).call();
			logger.info("branch: [ "+branchToDelete+" ] was deleted");
		}  catch (GitAPIException | IOException e) {
						logger.error("unable to delete branch: [ "+branchToDelete+" ]"+e.getMessage());
		}
		return deleted;
	}
	public static boolean checkoutBranch(Git git,String branchToCheckout) {
		boolean status=false;
		try {
			CheckoutCommand checkoutCommand=git.checkout().setName(branchToCheckout);
			checkoutCommand.call();
			status=true;
		}  catch (Exception e) {
						logger.error("unable to checkout branch: "+branchToCheckout);
		} 
		return status;
	}

	public static void createBranch(Git git,String branchName) {
		CreateBranchCommand createBranchCommand=git.branchCreate().setName(branchName);
		try {
			createBranchCommand.call();
			setUpStream(git,branchName);
		} catch (Exception e) {
						logger.error("unable to create branch: "+branchName);
		} 

	}
	public static void createTag(Git git) throws ConcurrentRefUpdateException, InvalidTagNameException, NoHeadException, GitAPIException {
		@SuppressWarnings("unused")
		RevCommit commit = git.commit().setMessage("Commit message").call();
		@SuppressWarnings("unused")
		RevTag tag = (RevTag) git.tag().setName("tag-name").call();
	}

	public static void setUpStream(Git git,String branchName) {

		StoredConfig storedConfig = git.getRepository().getConfig();
		storedConfig.setString( Constants.GIT_CONFIG_BRANCH_SECTION,branchName, "remote", branchName );
		storedConfig.setString( Constants.GIT_CONFIG_BRANCH_SECTION,branchName, "merge", branchName );
		try {
			storedConfig.save();
		} catch (IOException e) {
			logger.error("unable to setupstream in config file for branch: "+branchName);
		}

	}

	public static boolean isBranchExists(Git git,String branchName){
		boolean exists=true;
		ObjectId.isId(branchName);
		Ref ref;
		Repository repository=git.getRepository();
		try {
			ref = repository.exactRef(Constants.GIT_DEFAULT_HEADS_REF + branchName);
			if (ref == null) {
				ref=repository.exactRef(Constants.GIT_DEFAULT_REMOTE_REF + branchName);
				if(ref==null) {
					exists=false;
					logger.info("branch checked, doesn't exist: good to go");
				}

			}
		} catch (IOException e) {
						logger.error("error while trying to check if branch [ "+branchName+" ] exists"+e.getMessage());
		}
		return exists;
	}

}
