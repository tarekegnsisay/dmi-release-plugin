package com.dmi.plugin.scm;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.dmi.plugin.utility.LoggingUtility;



public class GitScmManagerService {
	private String uri;
	private String localPath;
	private Repository repo;
	private Git git;
	
	private Map<String, List<String>> log=new HashMap<String,List<String>>();
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public Map<String, List<String>> getLog() {
		return log;
	}
	public void setLog(Map<String, List<String>> log) {
		this.log = log;
	}
	public Git getGit() {
		return git;
	}
	public void setGit(Git git) {
		this.git = git;
	}
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	
	public GitScmManagerService(String uri,String localPath){
		this.setUri(uri);
		this.setLocalPath(localPath);
		init();
	}
	public Repository getRepo() {
		return repo;
	}
	public void init(){
		if(uri==null||localPath==null)
			return;
		try {
			this.repo=getRepository();
		} catch (IOException e) {
			LoggingUtility.saveLog(log,"error","Error while Initializing local repo: "+localPath);
		}
		this.git=new Git(this.repo);
	}
	
	private Repository getRepository() throws IOException {
		//check conditions and return a repo
		return getExistingLocalGitRepo();
		
	}
	public Repository getExistingLocalGitRepo() throws IOException {
		
		CheckoutCommand checkout=Git.open(new File(this.localPath+"/.git")).checkout();
		return checkout.getRepository();
		
	}
	
	public void stageFiles(String fileOrDirectoryPattern) throws NoFilepatternException, GitAPIException {
		AddCommand add=this.git.add();
		add.addFilepattern(fileOrDirectoryPattern).call();
	}
	
	public void commitChanges(String commitMassage) {
		CommitCommand commit=this.git.commit();
		commit.setMessage(commitMassage);
		try {
			commit.call();
		} 
		catch (Exception e) {
			handleGitExceptions(e);
		} 
	}
	public void createBranch(String branchName) {
		CreateBranchCommand createBranchCommand=this.git.branchCreate().setName(branchName);
		try {
			createBranchCommand.call();
		} catch (Exception e) {
			handleGitExceptions(e);
		} 
		
	}
	public void createTag() throws ConcurrentRefUpdateException, InvalidTagNameException, NoHeadException, GitAPIException {
		@SuppressWarnings("unused")
		RevCommit commit = git.commit().setMessage("Commit message").call();
		@SuppressWarnings("unused")
		RevTag tag = (RevTag) git.tag().setName("tag-name").call();
	}
	
	public void pushChanges() {
		
	}
	
	public void checkoutBranch(String releaseBranch) {
		//git push --set-upstream origin $BRANCH
		
	}
	public void checkoutBranch(String uri, String releaseBranch) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void cloneAllBranches() {
		try {
			this.git = Git.cloneRepository().setURI(this.uri).setDirectory(new File(this.localPath))
					  .setCloneAllBranches(true)//!!!!
					  .call();
			LoggingUtility.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
		} catch (Exception e) {
			handleGitExceptions(e);
		} 
	
	}
	
	public void cloneRepo() {
		try {
			Git.cloneRepository()
			  .setURI(uri)
			  .setDirectory(new File(localPath))
			  .call();
			LoggingUtility.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
		} catch (Exception e) {
			handleGitExceptions(e);
		} 
	
	}
	

	public void cloneSpecificBranch(String branchToClone) {
		try {
			Git.cloneRepository()
			  .setURI(this.uri)
			  .setDirectory(new File(this.localPath))
			  .setBranchesToClone(Collections.singletonList(branchToClone))
			  .setBranch(branchToClone)
			  .call();
			LoggingUtility.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
		} catch (Exception e) {
			handleGitExceptions(e);
		} 
	
	}
	public void checkoutCommit(String commitId) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException{
		this.git.checkout()
	    .setStartPoint(commitId)
	    .call();
	}
	
	public void checkoutCommit(String commitId, String checkoutToBranch) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
		this.git.checkout()
	    .setCreateBranch(true)
	    .setName(checkoutToBranch)
	    .setStartPoint(commitId)
	    .call();
	}
	public void pullRepo() {
		PullCommand pullCommand=this.git.pull();
		try {
			pullCommand.call();
			LoggingUtility.saveLog(log, "info", "Repository is cloned sucessfully");
		} catch (Exception e) {
			handleGitExceptions(e);
		} 
	}

	@SuppressWarnings("unused")
	public void createGitRepo() {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		try {
			Repository repository = repositoryBuilder.setGitDir(new File(this.localPath))
			                .readEnvironment() // scan environment GIT_* variables
			                .findGitDir() // scan up the file system tree
			                .setMustExist(true)
			                .build();
		}catch (Exception e) {
			handleGitExceptions(e);
		} 
	}
	
public void handleGitExceptions(Exception e) {
	if(e instanceof WrongRepositoryStateException)
		LoggingUtility.saveLog(log, "error", "WrongRepositoryStateException");
	else if(e instanceof InvalidConfigurationException) {
		LoggingUtility.saveLog(log, "error", "InvalidConfigurationException");
}
	else if(e instanceof InvalidConfigurationException) {
		LoggingUtility.saveLog(log, "error", "InvalidConfigurationException");
	}
	else if(e instanceof InvalidRemoteException) {
		LoggingUtility.saveLog(log, "error", "InvalidRemoteException");
	}
	else if(e instanceof CanceledException) {
		LoggingUtility.saveLog(log, "error", "CanceledException ");
	}
	else if(e instanceof RefNotFoundException) {
		LoggingUtility.saveLog(log, "error", "RefNotFoundException");
	}
	else if(e instanceof RefNotAdvertisedException) {
		LoggingUtility.saveLog(log, "error", "RefNotAdvertisedException");
	}
	else if(e instanceof NoHeadException) {
		LoggingUtility.saveLog(log, "error", "NoHeadException");
	}
	else if(e instanceof TransportException) {
		LoggingUtility.saveLog(log, "error", "TransportException");
	}
	else if(e instanceof GitAPIException) {
		LoggingUtility.saveLog(log, "error", "GitAPIException");
	}
	
	else if(e instanceof AbortedByHookException) {
		LoggingUtility.saveLog(log, "error", "AbortedByHookException");
	}
	else if(e instanceof NoMessageException) {
		LoggingUtility.saveLog(log, "error", "NoMessageException");
	}
	else if(e instanceof UnmergedPathsException) {
		LoggingUtility.saveLog(log, "error", "UnmergedPathsException");
	}
	
	
	else {
		LoggingUtility.saveLog(log, "error", "Error occured ");
	}
}
	
	

}
