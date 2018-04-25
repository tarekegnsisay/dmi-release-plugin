package com.dmi.plugin.service.git;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.dmi.plugin.util.StringUtils;
import com.dmi.plugin.util.UserConfiguration;

public class GitScmService {
	private String uri;
	private String localPath;
	private Repository repo;
	private Git git;
	private UsernamePasswordCredentialsProvider userCredential = null;

	final static Logger logger = Logger.getLogger(GitScmService.class);

	public GitScmService(String uri, String localPath, UserConfiguration userConfiguration) {

		if (userConfiguration != null) {

			String username = userConfiguration.getGitUsername();
			String password = userConfiguration.getGitPassword();

			if (!(StringUtils.isEmptyOrNull(username)) && !(StringUtils.isEmptyOrNull(password))) {
				userCredential = new UsernamePasswordCredentialsProvider(username, password);
			}
		} else {
			logger.warn("no user configuration is provided");
		}
		this.uri = uri;
		this.localPath = localPath;

		if (uri == null || localPath == null) {
			logger.error("uri or local directory path is empty, unable to open the repo");
			return;
		} else {
			logger.info("uri: " + this.uri + " & localpath: " + this.localPath);
		}

		try {
			this.repo = RepositoryAndCloneService.getRepository(this.uri, this.localPath);
		} catch (Exception e) {
			logger.error("error while processing repository: [" + e.getMessage() + "]");
			return;
		}
		try {
			this.git = new Git(this.repo);
			this.git.pull().call();
		} catch (Exception e) {
			logger.error("error initializing git instance, git=new Git(repo): [" + e.getMessage() + "]");
		}

	}

	public Git getGit() {
		return git;
	}

	public UsernamePasswordCredentialsProvider getUserCredential() {
		return userCredential;
	}

	public void stageFiles(String filePattern) throws NoFilepatternException, GitAPIException {
		StagingService.stageFiles(git, filePattern);
	}

	public void commitAllChangesToTackedFiles(String commitMassage) {

		CommitService.commitAllChangesToTackedFiles(git, commitMassage);
	}

	public void commitStagedFilesOnly(String commitMessage) {
		CommitService.commitStagedFilesOnly(git, commitMessage);
	}

	public boolean createBranch(String branchName) {
		return BranchService.createBranch(git, branchName);

	}

	public boolean createAndPushBranch(String branchName) {
		return BranchService.createAndPushBranch(git, branchName, userCredential);

	}

	public boolean createTag(ObjectId objectId, PersonIdent tagger, String tagName, String tagMessage) {
		return TagService.createTag(git, userCredential, objectId, tagger, tagName, tagMessage);

	}

	public boolean createHotfixBranchFromTag(String hotfixBranchName, String tagName) {
		return BranchService.createBranchFromTag(git, hotfixBranchName, tagName);
	}

	public boolean deleteTag(String tagName) {
		return TagService.deleteTag(git, tagName, userCredential);

	}

	public boolean deleteBranchFromLocal(String branchToDelete) {
		return BranchService.deleteBranchFromLocal(git, branchToDelete);
	}

	public boolean deleteBranchFromRemote(String branchToDelete) {
		return BranchService.deleteBranchFromRemote(git, branchToDelete, userCredential);
	}

	public boolean checkoutBranch(String branchToCheckout) {
		return BranchService.checkoutBranch(git, branchToCheckout);
	}

	public boolean checkoutRemoteBranch(String branchToCheckout) {
		return BranchService.checkoutRemoteBranch(git, branchToCheckout);
	}

	public List<String> getAllBranches() {
		return BranchService.getAllBranches(git, ListMode.ALL);
	}

	public void cloneAllBranches() {

		RepositoryAndCloneService.cloneAllBranches(uri, localPath);
	}

	public Repository cloneRepository() {
		return RepositoryAndCloneService.cloneRepository(uri, localPath);
	}

	public Repository cloneSpecificBranch(String branchToClone) {
		return RepositoryAndCloneService.cloneSpecificBranch(uri, localPath, branchToClone);
	}

	public void checkoutCommit(String commitId) throws RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, CheckoutConflictException, GitAPIException {
		BranchService.checkoutCommit(git, commitId);
	}

	public void checkoutCommit(String commitId, String checkoutToBranch) {
		BranchService.checkoutCommit(git, commitId, checkoutToBranch);
	}

	public void pullRepo() {
		PullService.pullRepo(git);
	}

	public Repository createGitRepo() {
		return RepositoryAndCloneService.createGitRepo(localPath);
	}

	public void mergeAndDeleteBranches(String destination, String source) {
		MergeService.mergeAndDeleteBranches(git, destination, source);
	}

	public boolean mergeBranches(String destination, String source, String mergeMessage) {
		return MergeService.mergeBranches(git, destination, source, mergeMessage);
	}

	public boolean pushBranch(String branchName) {
		return PushService.pushBranch(git, branchName, userCredential);
	}

	public boolean publishBranch(String branchName) {
		return BranchService.publishBranch(git, branchName, userCredential);
	}

	public boolean isBranchExists(String branchName) {
		return BranchService.isBranchExists(git, branchName, ListMode.ALL);
	}

	public boolean isBranchExistsInRemote(String branchName) {
		return BranchService.isBranchExists(git, branchName, ListMode.REMOTE);
	}

	public boolean isTagExists(String tagName) {
		return TagService.isTagExists(git, tagName);
	}

}
