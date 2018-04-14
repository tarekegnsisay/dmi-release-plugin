package com.dmi.plugin.service.git;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import com.dmi.plugin.util.UserConfiguration;



public class GitScmService {
	private String uri;
	private String localPath;
	private Repository repo;
	private Git git;
	final UsernamePasswordCredentialsProvider userCredential;
	
	final static Logger logger = Logger.getLogger(GitScmService.class);


	public GitScmService(String uri,String localPath, UserConfiguration userConfiguration){
		userCredential = new UsernamePasswordCredentialsProvider(userConfiguration.getGitUsername(), userConfiguration.getGitPassword());
		this.uri=uri;
		this.localPath=localPath;

		if(uri==null||localPath==null) {
			logger.error("uri or local directory path is empty, unable to open the repo");
			return;
		}
		else {
			logger.info("uri:"+this.uri+" & localpath:"+this.localPath);
		}

		this.repo=RepositoryAndCloneService.getRepository(this.uri,this.localPath);
		this.git=new Git(this.repo);
	}


	public void stageFiles(String fileOrDirectoryPattern) throws NoFilepatternException, GitAPIException {
		AddCommand add=this.git.add();
		add.addFilepattern(fileOrDirectoryPattern).call();
	}

	public void commitChanges(String commitMassage) {

		CommitService.commitChanges(git, commitMassage);
	}

	public void createBranch(String branchName) {
		BranchAndTagService.createBranch(git, branchName);

	}


	public void createTag() throws ConcurrentRefUpdateException, InvalidTagNameException, NoHeadException, GitAPIException {
		BranchAndTagService.createTag(git);
	}

	public boolean deleteBranch(String branchToDelete) {
		return BranchAndTagService.deleteBranch(git, branchToDelete);
	}
	public boolean checkoutBranch(String branchToCheckout) {
		return BranchAndTagService.checkoutBranch(git, branchToCheckout);
	}


	public void cloneAllBranches() {
		
		RepositoryAndCloneService.cloneAllBranches(uri, localPath);
	}
	public Repository cloneRepo() {
		return RepositoryAndCloneService.cloneRepo(uri, localPath);
	}


	public Repository cloneSpecificBranch(String branchToClone) {
		return RepositoryAndCloneService.cloneSpecificBranch(uri, localPath, branchToClone);
	}
	public void checkoutCommit(String commitId) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException{
		CommitService.checkoutCommit(git, commitId);
	}

	public void checkoutCommit(String commitId, String checkoutToBranch) {
		CommitService.checkoutCommit(git, commitId, checkoutToBranch);
	}
	public void pullRepo() {
		PushAndPullService.pullRepo(git);
	}

	public Repository createGitRepo() {
		return RepositoryAndCloneService.createGitRepo(localPath);
	}

	public void mergeAndDeleteBranches(String destination, String source) {
		MergeService.mergeAndDeleteBranches(git, destination, source);
	}


	public void pushNewBranch(String newBranchName) {
		PushAndPullService.pushNewBranch(git, newBranchName, userCredential);
	}
	public boolean isBranchExists(String branchName){
		return BranchAndTagService.isBranchExists(git, branchName);
	}

	@SuppressWarnings("unused")
	private void setUpStream(String branchName) {
		BranchAndTagService.setUpStream(git, branchName);

	}




}
