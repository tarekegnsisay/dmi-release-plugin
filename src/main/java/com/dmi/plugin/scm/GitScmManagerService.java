package com.dmi.plugin.scm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.settings.Settings;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.MergeResult.MergeStatus;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
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
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
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
import org.eclipse.jgit.util.FS;

import com.dmi.plugin.util.LoggingUtils;



public class GitScmManagerService {
	private String uri;
	private String localPath;
	private Repository repo;
	private Git git;
	
	private Map<String, List<String>> log;
	
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
	public Repository getRepo() {
		return repo;
	}
	
	public GitScmManagerService(String uri,String localPath){
		
		this.setUri(uri);
		this.setLocalPath(localPath);
		log=new HashMap<String,List<String>>();
				
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
	public Repository getExistingLocalGitRepo() throws IOException {
		
		CheckoutCommand checkout=Git.open(new File(this.localPath+"/.git")).checkout();
		LoggingUtils.saveLog(log,"info","Repository opened: "+localPath);
		return checkout.getRepository();
		
	}
	
	public void stageFiles(String fileOrDirectoryPattern) throws NoFilepatternException, GitAPIException {
		AddCommand add=this.git.add();
		add.addFilepattern(fileOrDirectoryPattern).call();
	}
	
	public void commitChanges(String commitMassage) {
		CommitCommand commit=this.git.commit().setAll(true);
		commit.setMessage(commitMassage);
		
			try {
				commit.call();
			} catch (NoHeadException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (NoMessageException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (UnmergedPathsException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (ConcurrentRefUpdateException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (WrongRepositoryStateException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (AbortedByHookException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
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
	
	public void checkoutBranch(String branchToCheckout) {
		try {
			CheckoutCommand checkoutCommand=this.git.checkout().setName(branchToCheckout);
			checkoutCommand.call();
		}  catch (Exception e) {
			handleGitExceptions(e);
		} 
	}
	public void checkoutBranch(String uri, String releaseBranch) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void cloneAllBranches() {
		try {
			this.git = Git.cloneRepository().setURI(this.uri).setDirectory(new File(this.localPath))
					  .setCloneAllBranches(true)//!!!!
					  .call();
			LoggingUtils.saveLog(this.log, "info","Repo was sucessfully cloned to: "+localPath);
		} catch (Exception e) {
			handleGitExceptions(e);
		} 
	
	}
	public Repository initializeGitDirectory() {
		Repository repo=null;
		try {
			this.git = Git.init().setDirectory(new File(this.localPath)).call();
			repo = this.git.getRepository();

			
			 StoredConfig config = repo.getConfig();
			RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
			remoteConfig.addURI(new URIish(this.uri));
			
			remoteConfig.addFetchRefSpec(new RefSpec(
			    "+refs/heads/*"+
			   ":refs/remotes/origin/*"));
			remoteConfig.update(config);
			config.save();

			this.git.fetch().call();
			this.git.branchCreate().setName("master").setStartPoint("origin/" + "master")
			    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
			this.git.checkout().setName("master").call();

			
		}catch(Exception e) {
			handleGitExceptions(e);
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
		} catch (Exception e) {
			handleGitExceptions(e);
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
		} catch (Exception e) {
			handleGitExceptions(e);
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
public boolean isGitRepo() {	
	boolean detected=RepositoryCache.FileKey.isGitRepository(new File(this.localPath), FS.DETECTED);
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
		} catch (Exception e) {
			handleGitExceptions(e);
		}
		
}
	
private void deleteBranch(String branchToDelete) {
	try {
		this.git.branchDelete().setBranchNames(branchToDelete).call();
	}  catch (Exception e) {
		handleGitExceptions(e);
	}
	
}
public void pushNewBranch(String newFeatureName) {
	//git push --set-upstream origin $newFeatureName
	try {
		  Ref newBranchName = git.checkout()
                  .setName(newFeatureName)
                  .setCreateBranch(true)
                  //.setStartPoint(startPoint)
                  .call();
		  git.push().setRemote("origin").call();
		
//		CreateBranchCommand createBranchCommand=this.git.branchCreate().setName(newFeatureName);
//		//createBranchCommand.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
//		createBranchCommand.setStartPoint("develop");
//		createBranchCommand.call();
//		this.git.checkout().setName(newFeatureName).call();
//		String spec=newFeatureName+":"+newFeatureName;
//		PushCommand pushCommand=this.git.push();
//		//pushCommand.setRemote("origin");
//		//pushCommand.setRefSpecs(new RefSpec(spec));
//		pushCommand.call();
	} catch (GitAPIException e) {
		handleGitExceptions(e);
	}
	

}
public void handleGitExceptions(Exception e) {
	if(e instanceof WrongRepositoryStateException)
		LoggingUtils.saveLog(log, "error", "WrongRepositoryStateException");
	else if(e instanceof InvalidConfigurationException) {
		LoggingUtils.saveLog(log, "error", "InvalidConfigurationException");
}
	else if(e instanceof InvalidConfigurationException) {
		LoggingUtils.saveLog(log, "error", "InvalidConfigurationException");
	}
	else if(e instanceof InvalidRemoteException) {
		LoggingUtils.saveLog(log, "error", "InvalidRemoteException");
	}
	else if(e instanceof CanceledException) {
		LoggingUtils.saveLog(log, "error", "CanceledException ");
	}
	else if(e instanceof RefNotFoundException) {
		LoggingUtils.saveLog(log, "error", "RefNotFoundException");
	}
	else if(e instanceof RefNotAdvertisedException) {
		LoggingUtils.saveLog(log, "error", "RefNotAdvertisedException");
	}
	else if(e instanceof NoHeadException) {
		LoggingUtils.saveLog(log, "error", "NoHeadException");
	}
	else if(e instanceof TransportException) {
		LoggingUtils.saveLog(log, "error", "TransportException");
	}
	else if(e instanceof GitAPIException) {
		LoggingUtils.saveLog(log, "error", "GitAPIException");
	}
	
	else if(e instanceof AbortedByHookException) {
		LoggingUtils.saveLog(log, "error", "AbortedByHookException");
	}
	else if(e instanceof NoMessageException) {
		LoggingUtils.saveLog(log, "error", "NoMessageException");
	}
	else if(e instanceof UnmergedPathsException) {
		LoggingUtils.saveLog(log, "error", "UnmergedPathsException");
	}
	
	else if(e instanceof CannotDeleteCurrentBranchException) {
		LoggingUtils.saveLog(log, "error", "CannotDeleteCurrentBranchException");
	}
	

	else if(e instanceof NotMergedException) {
		LoggingUtils.saveLog(log, "error", "CannotDeleteCurrentBranchException");
	}
	
	else if(e instanceof NullPointerException) {
		LoggingUtils.saveLog(log, "error", "Null Pointer Exception:GIT ");
	}
	else {
		LoggingUtils.saveLog(log, "error", "Error occured ");
	}
}


	
	

}
