package com.dmi.plugin.service.git;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.dmi.plugin.util.Constants;

public class BranchService {
	final static Logger logger = Logger.getLogger(BranchService.class);

	public static boolean checkoutBranch(Git git, String branchToCheckout) {
		try {
			CheckoutCommand checkoutCommand = git.checkout().setName(branchToCheckout);
			checkoutCommand.call();
		} catch (Exception e) {
			logger.error("unable to checkout branch: " + branchToCheckout);
			return false;
		}
		return true;
	}

	public static boolean checkoutRemoteBranch(Git git, String remoteBranchToCheckout) {
		boolean createBranch=!isBranchExistInLocal(git,remoteBranchToCheckout);

		try {
			Ref ref = git.checkout().setCreateBranch(createBranch).setName(remoteBranchToCheckout)
					.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
					.setStartPoint(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME + "/" + remoteBranchToCheckout).call();
			if (ref == null) {
				logger.error("unable to resolve reference to branch: " + remoteBranchToCheckout);
				return false;
			}
		} catch (Exception e) {
			logger.error("unable to checkout remote branch: [" + remoteBranchToCheckout+"]"+e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean createBranch(Git git, String branchName) {
		if (isBranchExists(git, branchName, ListMode.ALL)) {
			logger.error("branch with the same name exists: [" + branchName + "]");
			return false;
		}
		CreateBranchCommand createBranchCommand = git.branchCreate().setName(branchName);
		try {

			createBranchCommand.call();
			setUpStream(git, branchName);

			logRefsAndGitConfig(git.getRepository());

			Ref newRef = git.getRepository().findRef(branchName);

			logger.info("reference name of newly created branch is: [ " + newRef.getName() + " ]");
			logger.info("reference of newly created branch is: [ " + newRef + " ]");

		} catch (Exception e) {
			logger.error("unable to create branch: [ " + branchName + " ]" + e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean createAndPushBranch(Git git, String branchName,
			UsernamePasswordCredentialsProvider userCredential) {

		if (isBranchExists(git, branchName, ListMode.ALL)) {
			logger.error("branch with the same name exists: [" + branchName + "]");
			return false;
		}
		CreateBranchCommand createBranchCommand = git.branchCreate().setName(branchName);

		try {
			createBranchCommand.call();
			setUpStream(git, branchName);

			logRefsAndGitConfig(git.getRepository());

			Ref newRef = git.getRepository().findRef(branchName);

			logger.info("reference name of newly created branch is: [ " + newRef.getName() + " ]");
			logger.info("reference of newly created branch is: [ " + newRef + " ]");

			git.checkout().setName(branchName).call();
			Iterable<PushResult> pushResults = git.push().setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME)
					.setCredentialsProvider(userCredential).call();

			for (PushResult pushResult : pushResults) {
				logger.info("push result message:" + pushResult.getMessages());
			}

		} catch (Exception e) {
			logger.error("unable to create or push branch: [ " + branchName + " ]" + e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean publishBranch(Git git, String branchName,
			UsernamePasswordCredentialsProvider userCredential) {
		if (!isBranchExists(git, branchName, ListMode.ALL)) {
			logger.error("branch doesn't exists, unable to publish [" + branchName + "]");
			return false;
		}

		try {
			setUpStream(git, branchName);
			git.checkout().setName(branchName).call();
			Iterable<PushResult> pushResults = git.push().setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME)
					.setCredentialsProvider(userCredential).call();

			for (PushResult pushResult : pushResults) {
				logger.info("push result message:" + pushResult.getMessages());
			}

		} catch (Exception e) {
			logger.error("unable to publish branch: [ " + branchName + " ]" + e.getMessage());
			return false;
		}
		return true;
	}

	public static void checkoutCommit(Git git, String commitId) {
		try {
			git.checkout().setStartPoint(commitId).call();
		} catch (Exception e) {
			logger.error("error while checkout commitId: " + commitId);
		}
	}

	public static void checkoutCommit(Git git, String commitId, String checkoutToBranch) {

		try {
			git.checkout().setCreateBranch(true).setName(checkoutToBranch).setStartPoint(commitId).call();
		} catch (Exception e) {
			logger.error("error while checkout commitId: [" + commitId + "] to branch: [" + checkoutToBranch + "]");
		}
	}

	public static boolean deleteBranchFromLocal(Git git, String branchToDelete) {

		if (branchToDelete.equalsIgnoreCase(Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH)) {
			logger.error("development branch can't be deleted");
			return false;
		}
		try {
			boolean isCheckedout = checkoutBranch(git, Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH);
			if (!isCheckedout) {
				logger.error("deletion of branch: [ " + branchToDelete
						+ " ] aborted due to unsuccessful checkout of development branch: [ "
						+ Constants.WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH + " ]");
				return false;
			}

			git.branchDelete().setForce(true).setBranchNames(branchToDelete).call();

			logger.info("branch: [ " + branchToDelete + " ] deleted");

		} catch (Exception e) {
			logger.error("unable to delete branch: [ " + branchToDelete + " ]" + e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean deleteBranchFromRemote(Git git, String branchToDelete,
			UsernamePasswordCredentialsProvider userCredential) {

		String refToDelete = Constants.GIT_DEFAULT_REFS_HEADS + branchToDelete;

		try {
			RefSpec deleteSpec = new RefSpec().setSource(null).setDestination(refToDelete);

			Iterable<PushResult> pushResults = git.push().setRefSpecs(deleteSpec)
					.setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME).setCredentialsProvider(userCredential).call();

			if (pushResults != null)
				for (PushResult pushResult : pushResults) {
					logger.info("push result message:" + pushResult);
				}
			logger.info("remote reference: [ " + refToDelete + " ] deleted");

		} catch (Exception e) {
			logger.error("unable to delete remote reference: [ " + refToDelete + " ]" + e.getMessage());
			return false;
		}
		return true;
	}

	public static void setUpStream(Git git, String branchName) {

		StoredConfig storedConfig = git.getRepository().getConfig();
		storedConfig.setString(Constants.GIT_CONFIG_BRANCH_SECTION, branchName, "remote",
				Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME);
		storedConfig.setString(Constants.GIT_CONFIG_BRANCH_SECTION, branchName, "merge",
				Constants.GIT_DEFAULT_REFS_HEADS + branchName);
		try {
			storedConfig.save();
		} catch (Exception e) {
			logger.error("unable to setupstream in config file for branch: " + branchName+"] "+e.getMessage());
		}

	}

	public static boolean isBranchExists(Git git, String branchName, ListMode listMode) {

		List<String> branches = getAllBranches(git, listMode);

		for (String branch : branches) {
			int index = branch.indexOf(branchName);
			if (index >= 0) {

				String temp = branch.substring(index);
				if (temp != null)
					if (temp.equalsIgnoreCase(branchName)) {
						return true;
					}
			}
		}
		return false;
	}
	public static boolean isBranchExistInLocal(Git git, String branchName){
		/*
		 * 
		 * 
	 		List<String> branches=getAllBranches(git,ListMode.ALL);
	 		List<String> remoteBranches=getAllBranches(git,ListMode.REMOTE);
	 		branches.removeAll(remoteBranches);

		 * 
		 * 
		 */
		String fullBranchName=Constants.GIT_DEFAULT_REFS_HEADS+branchName;
		try {
			return git.branchList().call().contains(fullBranchName);
		} catch (Exception e) {
			logger.error("error while retrieving local branch info: "+e.getMessage());
		}
		return false;
	}
	public static void getRecentlyModifiedBranch(Git git) {
		/*
		 * RefModels
		 */
	}

	public static void logRefsAndGitConfig(Repository repository) {

		Collection<Ref> refs = repository.getAllRefs().values();
		refs.forEach(ref -> logger.info(ref));
		logger.info("repository set up: \n\n" + repository.getConfig().toText());
	}

	public static List<String> getAllBranches(Git git, ListMode listMode) {
		List<String> branches = new ArrayList<>();
		try {
			List<Ref> branchRefs = git.branchList().setListMode(listMode).call();
			for (Ref ref : branchRefs) {
				branches.add(ref.getName());
			}
		} catch (Exception e) {
			logger.error("error retrieving branches");
		}
		return branches;
	}

	public static List<DiffEntry> compareBranches(Git git, String branch1, String branch2) {
		List<DiffEntry> diffs = null;
		try {
			ObjectId objectId1 = git.getRepository().resolve(git.getRepository().findRef(branch1).getName());
			ObjectId objectId2 = git.getRepository().resolve(branch2);

			ObjectReader objectReader = git.getRepository().newObjectReader();

			CanonicalTreeParser canonicalTreeParser1 = new CanonicalTreeParser();
			canonicalTreeParser1.reset(objectReader, objectId1);

			CanonicalTreeParser canonicalTreeParser2 = new CanonicalTreeParser();
			canonicalTreeParser1.reset(objectReader, objectId2);

			diffs = git.diff().setOldTree(canonicalTreeParser1).setNewTree(canonicalTreeParser2).call();

		} catch (Exception e) {
			logger.error("error comparing branches");
		}
		if (diffs != null)
			for (DiffEntry diff : diffs) {
				logger.info("Similarity score: " + diff.getScore() + "\n" + diff);
			}
		return diffs;

	}
}
