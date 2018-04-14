package com.dmi.plugin.service.git;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import com.dmi.plugin.util.Constants;

public class RepositoryAndCloneService {
	final static Logger logger = Logger.getLogger(RepositoryAndCloneService.class);

	public static Repository getRepository(String uri,String localPath) {
		//check conditions and return a repo
		if(!isGitRepo(localPath)) {
			return initializeGitDirectory(uri,localPath);
		}
		else {
			return getExistingLocalGitRepo(localPath);
		}
	}
	public static boolean isGitRepo(String localPath) {	
		boolean detected=RepositoryCache.FileKey.isGitRepository(new File(localPath+"/.git"), FS.DETECTED);
		return detected;
	}

	public static Repository initializeGitDirectory(String uri,String localPath) {
		Repository repo=null;
		try {
			Git git = Git.init().setDirectory(new File(localPath)).call();
			repo = git.getRepository();


			StoredConfig config = repo.getConfig();
			RemoteConfig remoteConfig = new RemoteConfig(config, Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME);
			remoteConfig.addURI(new URIish(uri));

			remoteConfig.addFetchRefSpec(new RefSpec(Constants.GIT_DEFAULT_SRC_REF_SPEC+":"+Constants.GIT_DEFAULT_DST_REF_SPEC));
			remoteConfig.update(config);
			config.save();

			git.fetch().call();
			git.branchCreate().setName(Constants.GIT_DEFAULT_MASTER_BRANCH_NAME)
			.setStartPoint(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME+"/" + Constants.GIT_DEFAULT_MASTER_BRANCH_NAME)
			.setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM).call();
			git.checkout().setName(Constants.GIT_DEFAULT_MASTER_BRANCH_NAME).call();


		}catch(Exception e) {
			logger.error("exception while initializeGitDirectory: "+localPath);
		}
		return repo;

	}
	public static  Repository cloneRepo(String uri,String localPath) {
		Repository repo=null;

		try {
			Git.cloneRepository()
			.setURI(uri)
			.setDirectory(new File(localPath))
			.call();
			logger.info("Repo was sucessfully cloned to: "+localPath);
			CheckoutCommand checkout=Git.open(new File(localPath+"/.git")).checkout();
			repo=checkout.getRepository();
		} catch (IOException | GitAPIException e) {
						logger.error("unable to clone repo from uri: [ "+uri+" ]"+e.getMessage());
		}
		return repo;
	}

	public static Repository getExistingLocalGitRepo(String localPath) {

		CheckoutCommand checkout=null;
		try {
			checkout = Git.open(new File(localPath+Constants.GIT_DEFAULT_GIT_DIR_LOCATION)).checkout();
			logger.info("Repository opened: "+localPath);
		} catch (IOException e) {
			logger.error("error while opening local repository at: "+localPath);
		}
		return checkout!=null?checkout.getRepository():null;

	}
	public static Repository createGitRepo(String localPath) {
		Repository repository=null;
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		try {
			repository = repositoryBuilder.setGitDir(new File(localPath))
					.readEnvironment() 
					.findGitDir()
					.setMustExist(true)
					.build();
		}catch (IOException e) {
			logger.error("unable to create git repo at: [ "+localPath+" ]"+e.getMessage());
		}
		return repository;
	}
	public static Git cloneAllBranches(String uri,String localPath) {
		Git git=null;
		try {
			git = Git.cloneRepository().setURI(uri).setDirectory(new File(localPath))
					.setCloneAllBranches(true)//!!!!
					.call();
					logger.info("Repo was sucessfully cloned to: "+localPath);
		}catch (GitAPIException e) {
					logger.error("unable to clone all branches: [ "+uri+" ]"+e.getMessage());
		}
		return git;
	}
	public static Repository cloneSpecificBranch(String uri, String localPath,String branchToClone) {
		Repository repo=null;
		try {
			Git.cloneRepository()
			.setURI(uri)
			.setDirectory(new File(localPath))
			.setBranchesToClone(Collections.singletonList(branchToClone))
			.setBranch(branchToClone)
			.call();
			logger.info("Repo was sucessfully cloned to: "+localPath);

			CheckoutCommand checkout=Git.open(new File(localPath+"/.git")).checkout();
			repo=checkout.getRepository();
		} catch (IOException | GitAPIException e) {
			logger.error("unable to clone branch: [ "+branchToClone+" ]"+e.getMessage());
		}
		return repo;
	}

}
