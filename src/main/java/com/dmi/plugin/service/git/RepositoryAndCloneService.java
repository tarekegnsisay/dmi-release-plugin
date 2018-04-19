package com.dmi.plugin.service.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
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
			return cloneRepository(uri,localPath);
		}
		else {
			return getExistingLocalGitRepo(localPath);
		}
	}
	public static boolean isGitRepo(String localPath) {	
		boolean isDetected=RepositoryCache.FileKey.isGitRepository(new File(localPath+Constants.GIT_DEFAULT_GIT_DIR_LOCATION), FS.DETECTED);
		if(isDetected) {
			logger.info("directory: [ "+localPath+" ] is detected as git repository");
		}
		else {
			logger.info("directory: [ "+localPath+" ] isn't a git repository");
		}
		return isDetected;
	}

	public static Repository initializeGitDirectory(String uri,String localPath) {
		Repository repository=null;
		try {
			Git git = Git.init().setDirectory(new File(localPath)).call();
			repository = git.getRepository();
			setDefaultGitConfig(repository,uri);

			logger.info("local directory [ "+localPath+" ] is initialized as git directory");

		}catch(Exception e) {
			logger.error("exception while initializeGitDirectory: "+localPath+ " Message:"+e.getMessage());
		}
		return repository;

	}
	public static  Repository cloneRepository(String uri,String localPath) {
		Repository repository=null;
		Path path=Paths.get(localPath);
		if(Files.exists(path)) {
			logger.info("cloning to existing non empty directory");
		}

		try {
			Git.cloneRepository()
			.setURI(uri)
			.setDirectory(path.toFile())
			.call();
			logger.info("Repo was sucessfully cloned to: "+localPath);

			Path gitDir=Paths.get(localPath,".git");
			if(!Files.exists(gitDir)) {
				logger.error("GIT_DIR doesn't exist");
			}
			CheckoutCommand checkout=Git.open(gitDir.toFile()).checkout();
			repository=checkout.getRepository();
			setDefaultGitConfig(repository,uri);
			logger.info("\nrepository configuration is:\n\n"+repository.getConfig()+"\n\n");
		} catch (Exception e) {
			logger.error("unable to clone repo from uri: [ "+uri+" ]"+e.getMessage());
		}

		return repository;
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
			logger.error("unable to clone all branches: ["+uri+"] "+e.getMessage());
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

	public static boolean validReferenceExists(Repository repository) {
		boolean isExists=false;
		Collection<Ref> refs=repository.getAllRefs().values();
		for(Ref ref:refs) {
			if(ref.getObjectId()!=null) {
				isExists=true;
				break;
			}
		}
		return isExists;

	}
	public static boolean hasCommits(Repository repository) {
		
		boolean anyCommits=false;
		
		if(repository==null || !repository.getDirectory().exists()) {
			return false;
		}
		else {

			String[] uncompressedRefs=(new File(repository.getDirectory(),"objects")).list();
			String[] compressedRefs=(new File(repository.getDirectory(),"objects/pack")).list();

			if(uncompressedRefs!=null) {
				anyCommits= uncompressedRefs.length>2?true:false;
			}
			if(anyCommits) {
				return true;
			}
			if(compressedRefs!=null) {
				return compressedRefs.length>0?true:false;
			}
		}
		return false;
	}
	public static void setDefaultGitConfig(Repository repository, String uri) {
		try {
			StoredConfig config = repository.getConfig();
			RemoteConfig remoteConfig = new RemoteConfig(config, Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME);
			remoteConfig.addURI(new URIish(uri));
			remoteConfig.addFetchRefSpec(new RefSpec(Constants.GIT_DEFAULT_SRC_REF_SPEC+":"+Constants.GIT_DEFAULT_DST_REF_SPEC));
			remoteConfig.addFetchRefSpec(new RefSpec(Constants.GIT_DEFAULT_SRC_TAGS_REF_SPEC+":"+Constants.GIT_DEFAULT_DST_TAGS_REF_SPEC));
			remoteConfig.update(config);
			config.save();
			logger.info("git configuration has been set up:\n "+config.toText());
		} catch (Exception e) {
			logger.error("unable to set default configuration for the repository at: [ "+repository.getDirectory().getParentFile()+" ]");
		}

	}

}
