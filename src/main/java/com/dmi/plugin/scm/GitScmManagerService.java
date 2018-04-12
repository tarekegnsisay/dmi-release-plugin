package com.dmi.plugin.scm;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;

import com.dmi.plugin.util.Constants;
import com.dmi.plugin.util.LoggingUtils;
import com.dmi.plugin.util.UserConfiguration;



public class GitScmManagerService {
	private String uri;
	private String localPath;
	private Repository repo;
	private Git git;
	final UsernamePasswordCredentialsProvider userCredential;
	private Map<String, LinkedList<String>> log;
	
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
	public Map<String, LinkedList<String>> getLog() {
		return log;
	}
	public void setLog(Map<String, LinkedList<String>> log) {
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
	public Repository getRepo() {
		return repo;
	}
	
	public GitScmManagerService(String uri,String localPath, UserConfiguration userConfiguration){
		userCredential = new UsernamePasswordCredentialsProvider(userConfiguration.getGitUsername(), userConfiguration.getGitPassword());
		this.setUri(uri);
		this.setLocalPath(localPath);
		this.log=new HashMap<String,LinkedList<String>>();
				
		if(uri==null||localPath==null) {
			this.log=LoggingUtils.saveLog(log,"error","uri or local directory path is empty, unable to open the repo");
			return;
		}
		else {
			this.log=LoggingUtils.saveLog(log,"info","uri:"+this.uri+" & localpath:"+this.localPath);
		}
		
		try {
			this.repo=getRepository();
		} catch (IOException e) {
			this.log=LoggingUtils.saveLog(log,"error","Error while Initializing local repo: "+localPath);
		}
		this.git=new Git(this.repo);
	}
	
	private Repository getRepository() throws IOException {
		//check conditions and return a repo
		if(!isGitRepo()) {
			return initializeGitDirectory();
		}
		else {
			return getExistingLocalGitRepo();
		}
		
		
		
	}
	public Repository getExistingLocalGitRepo() {
		
		CheckoutCommand checkout=null;
		try {
			checkout = Git.open(new File(this.localPath+"/.git")).checkout();
			LoggingUtils.saveLog(log,"info","Repository opened: "+localPath);
		} catch (IOException e) {
			LoggingUtils.saveLog(log,"error","error while opening local repository at: "+localPath);
		}
		return checkout!=null?checkout.getRepository():null;
		
	}
	
	public void stageFiles(String fileOrDirectoryPattern) throws NoFilepatternException, GitAPIException {
		AddCommand add=this.git.add();
		add.addFilepattern(fileOrDirectoryPattern).call();
	}
	
	public void commitChanges(String commitMassage) {
		
		CommitCommand commitCommand=this.git.commit().setAll(true);
		commitCommand.setMessage(commitMassage);
		String currentBranch="";
			try {
				currentBranch=commitCommand.getRepository().getBranch();
				commitCommand.call();
			}catch (Exception e) {
				LoggingUtils.saveLog(log,"error","error while commiting changes to: "+currentBranch);
			}
		
		
	}
	public void createBranch(String branchName) {
		CreateBranchCommand createBranchCommand=this.git.branchCreate().setName(branchName);
		try {
			createBranchCommand.call();
			setUpStream(branchName);
		} catch (Exception e) {
			LoggingUtils.saveLog(this.log, "error","unable to create branch: "+branchName);
		} 
		
	}
	private void setUpStream(String branchName) {
		
		StoredConfig storedConfig = git.getRepository().getConfig();
		storedConfig.setString( Constants.GIT_BRANCH_CONFIG_SECTION,branchName, "remote", branchName );
		storedConfig.setString( Constants.GIT_BRANCH_CONFIG_SECTION,branchName, "merge", branchName );
		try {
			storedConfig.save();
		} catch (IOException e) {
			LoggingUtils.saveLog(this.log, "error","unable to setupstream in config file for branch: "+branchName);
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
	
	public void checkoutBranch(String branchToCheckout) {
		try {
			CheckoutCommand checkoutCommand=this.git.checkout().setName(branchToCheckout);
			checkoutCommand.call();
		}  catch (Exception e) {
			LoggingUtils.saveLog(this.log, "error","unable to checkout branch: "+branchToCheckout);
		} 
	}

	
	public void cloneAllBranches() {
		try {
			this.git = Git.cloneRepository().setURI(this.uri).setDirectory(new File(this.localPath))
					  .setCloneAllBranches(true)//!!!!
					  .call();
			LoggingUtils.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
		}catch (GitAPIException e) {
			LoggingUtils.saveLog(log, "error", "unable to clone all branches: [ "+this.uri+" ]"+e.getMessage());
		}
	
	}
	public Repository initializeGitDirectory() {
		Repository repo=null;
		try {
			this.git = Git.init().setDirectory(new File(this.localPath)).call();
			repo = this.git.getRepository();

			
			 StoredConfig config = repo.getConfig();
			RemoteConfig remoteConfig = new RemoteConfig(config, Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME);
			remoteConfig.addURI(new URIish(this.uri));
			
			remoteConfig.addFetchRefSpec(new RefSpec(Constants.GIT_DEFAULT_SRC_REF_SPEC+":"+Constants.GIT_DEFAULT_DST_REF_SPEC));
			remoteConfig.update(config);
			config.save();

			this.git.fetch().call();
			this.git.branchCreate().setName(Constants.GIT_DEFAULT_MASTER_BRANCH_NAME)
				.setStartPoint(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME+"/" + Constants.GIT_DEFAULT_MASTER_BRANCH_NAME)
			    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
			this.git.checkout().setName(Constants.GIT_DEFAULT_MASTER_BRANCH_NAME).call();

			
		}catch(Exception e) {
			LoggingUtils.saveLog(this.log, "error","exception while initializeGitDirectory: "+localPath);
		}
		return repo;
		
	}
	public Repository cloneRepo() {
		Repository repo=null;
		
		try {
			Git.cloneRepository()
			  .setURI(uri)
			  .setDirectory(new File(this.localPath))
			  .call();
			LoggingUtils.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
			CheckoutCommand checkout=Git.open(new File(this.localPath+"/.git")).checkout();
			repo=checkout.getRepository();
		} catch (IOException | GitAPIException e) {
			LoggingUtils.saveLog(log, "error", "unable to clone repo from uri: [ "+this.uri+" ]"+e.getMessage());
		}
		return repo;
	}
	

	public Repository cloneSpecificBranch(String branchToClone) {
		Repository repo=null;
		try {
			Git.cloneRepository()
			  .setURI(this.uri)
			  .setDirectory(new File(this.localPath))
			  .setBranchesToClone(Collections.singletonList(branchToClone))
			  .setBranch(branchToClone)
			  .call();
			LoggingUtils.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
			
			CheckoutCommand checkout=Git.open(new File(this.localPath+"/.git")).checkout();
			repo=checkout.getRepository();
		} catch (IOException | GitAPIException e) {
			LoggingUtils.saveLog(log, "error", "unable to clone branch: [ "+branchToClone+" ]"+e.getMessage());
		}
		return repo;
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
			LoggingUtils.saveLog(log, "info", "Repository is cloned sucessfully");
		} catch (GitAPIException e) {
			LoggingUtils.saveLog(log, "error", "unable to pull repo from: [ "+this.uri+" ]"+e.getMessage());
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
		}catch (IOException e) {
			LoggingUtils.saveLog(log, "error", "unable to create git repo at: [ "+localPath+" ]"+e.getMessage());
		}
	}
public boolean isGitRepo() {	
	boolean detected=RepositoryCache.FileKey.isGitRepository(new File(this.localPath+"/.git"), FS.DETECTED);
	return detected;
}
public void mergeBranches(String destination, String source) {
		try {
			
			this.git.checkout().setName(destination).setCreateBranch(false).call();
			
			ObjectId obj=this.git.getRepository().resolve(source);
			MergeCommand mergeCommand=this.git.merge();
			mergeCommand.include(obj);
			
			MergeResult mergeResult=mergeCommand.call();
			MergeResult.MergeStatus status=mergeResult.getMergeStatus();
			if(status.equals(MergeStatus.CONFLICTING)) {
				LoggingUtils.saveLog(log, "error", mergeResult.getConflicts().toString());
			}
			else if(status.isSuccessful()){
				deleteBranch(source);
				
			}
		}  catch (GitAPIException | IOException e) {
			LoggingUtils.saveLog(log, "error", "unable to merge branches, destination: [ "+destination+" ] and source: [ "+source+" ]"+e.getMessage());
		}
}
	
private void deleteBranch(String branchToDelete) {
	try {
		String currentBranch=this.git.getRepository().getFullBranch();
		if(branchToDelete.equalsIgnoreCase(currentBranch)) {
			/*
			 * check out another branch and proceed deletion, master,develop?TBD
			 */
		}
		this.git.branchDelete().setForce(true).setBranchNames(branchToDelete).call();
	}  catch (GitAPIException | IOException e) {
		LoggingUtils.saveLog(log, "error", "unable to delete branch: [ "+branchToDelete+" ]"+e.getMessage());
	}
	
}
public void pushNewBranch(String newBranchName) {
	try {
		  git.push().setRemote("origin").setCredentialsProvider(userCredential).call();
		  LoggingUtils.saveLog(log, "info", " branch: [ "+newBranchName+" ] has been pushed successfully");
	} catch (GitAPIException e) {
		LoggingUtils.saveLog(log, "error", "unable to push branch: [ "+newBranchName+" ]"+e.getMessage());
	}
}
public boolean isBranchExists(String branchName){
	boolean exists=true;
	Ref ref;
	try {
		ref = repo.exactRef(Constants.GIT_DEFAULT_HEADS_REF + branchName);
		if (ref == null) {
			ref=repo.exactRef(Constants.GIT_DEFAULT_REMOTE_REF + branchName);
			if(ref==null) {
				exists=false;
				LoggingUtils.saveLog(log, "info", "branch checked, doesn't exist: good to go");
			}
				
	    }
	} catch (IOException e) {
		LoggingUtils.saveLog(log, "error", "error while trying to check if branch [ "+branchName+" ] exists"+e.getMessage());
	}
    return exists;
}


	
	

}
