package com.dmi.plugin.service.git;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.dmi.plugin.util.Constants;

public class BranchService {
	final static Logger logger = Logger.getLogger(BranchService.class);

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

			logRefsAndGitConfig(git.getRepository());

			Ref newRef=git.getRepository().findRef(branchName);

			logger.info("reference name of newly created branch is: [ "+newRef.getName()+" ]");
			logger.info("reference of newly created branch is: [ "+newRef+" ]");

		} catch (Exception e) {
			logger.error("unable to create branch: [ "+branchName+" ]"+e.getMessage());
		} 

	}
	public static void createAndPushBranch(Git git,String branchName, UsernamePasswordCredentialsProvider userCredential) {
		CreateBranchCommand createBranchCommand=git.branchCreate().setName(branchName);
		try {
			createBranchCommand.call();
			setUpStream(git,branchName);

			logRefsAndGitConfig(git.getRepository());

			Ref newRef=git.getRepository().findRef(branchName);

			logger.info("reference name of newly created branch is: [ "+newRef.getName()+" ]");
			logger.info("reference of newly created branch is: [ "+newRef+" ]");


			git.checkout().setName(branchName).call();	
			Iterable<PushResult> pushResults=git.push().setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME).setCredentialsProvider(userCredential).call();

			for(PushResult pushResult:pushResults) {
				logger.info("push result message:"+pushResult.getMessages());
			}

		} catch (Exception e) {
			logger.error("unable to create or push branch: [ "+branchName+" ]"+e.getMessage());
		} 

	}
	public static void checkoutCommit(Git git,String commitId) {
		try {
			git.checkout()
			.setStartPoint(commitId)
			.call();
		} catch (Exception e) {
			logger.error("error while checkout commitId: "+commitId);
		}
	}

	public static void checkoutCommit(Git git,String commitId, String checkoutToBranch)  {

		try {
			git.checkout()
			.setCreateBranch(true)
			.setName(checkoutToBranch)
			.setStartPoint(commitId)
			.call();
		} catch (Exception e) {
			logger.error("error while checkout commitId: ["+commitId+"] to branch: ["+checkoutToBranch+"]");
		}
	}
	public static boolean deleteBranch(Git git,String branchToDelete) {
		boolean isDeleted=false;
		if(branchToDelete.equalsIgnoreCase(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH)) {
			logger.error("development branch can't be deleted");
			return false;
		}
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
			logger.info("repository set up after deletion of branch [ "+branchToDelete+" ]: \n\n");

			logRefsAndGitConfig(git.getRepository());
			isDeleted=true;
		}  catch (GitAPIException | IOException e) {
			logger.error("unable to delete branch: [ "+branchToDelete+" ]"+e.getMessage());
		}
		return isDeleted;
	}
	public static boolean deleteBranchFromRemote(Git git,String branchToDelete, UsernamePasswordCredentialsProvider userCredential) {

		boolean isDeleted=false;
		String refToDelete=Constants.GIT_DEFAULT_REFS_HEADS+branchToDelete;

		try {			
			RefSpec deleteSpec=new RefSpec().setSource(null).setDestination(refToDelete);

			Iterable<PushResult> pushResults=git.push()
					.setRefSpecs(deleteSpec)
					.setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME)
					.setCredentialsProvider(userCredential)
					.call();
			for(PushResult pushResult:pushResults) {
				logger.info("push result message:"+pushResult);
			}
			logger.info("remote reference: [ "+refToDelete+" ] was deleted");
			logger.info("repository set up after deletion of remote reference [ "+refToDelete+" ]: \n\n");

			logRefsAndGitConfig(git.getRepository());

			isDeleted=true;

		}  catch (Exception e) {
			logger.error("unable to delete remote reference: [ "+refToDelete+" ]"+e.getMessage());
		}
		return isDeleted;
	}
	public static void setUpStream(Git git,String branchName) {

		StoredConfig storedConfig = git.getRepository().getConfig();
		storedConfig.setString( Constants.GIT_CONFIG_BRANCH_SECTION,branchName, "remote", Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME );
		storedConfig.setString( Constants.GIT_CONFIG_BRANCH_SECTION,branchName, "merge", Constants.GIT_DEFAULT_REFS_HEADS+branchName);
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
			ref = repository.exactRef(Constants.GIT_DEFAULT_REFS_HEADS + branchName);
			if (ref == null) {
				ref=repository.exactRef(Constants.GIT_DEFAULT_REFS_REMOTES + branchName);
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
	public static void logRefsAndGitConfig(Repository repository) {

		Collection<Ref> refs=repository.getAllRefs().values();
		refs.forEach(ref->logger.info(ref));
		logger.info("repository set up: \n\n"+repository.getConfig().toText());
	}

}
