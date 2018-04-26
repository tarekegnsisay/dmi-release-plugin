package com.dmi.plugin.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

import com.dmi.plugin.util.Constants;
import com.dmi.plugin.util.MavenCommandExecutor;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.UserConfiguration;

public class ReleaseService  extends AbstractApplicationService {

	final static Logger logger = Logger.getLogger(ReleaseService.class);

	public ReleaseService(MavenProject mavenProject,ScmBranchingConfiguration scmBranchingConfiguration, UserConfiguration userConfiguration) {
		super(mavenProject,scmBranchingConfiguration,userConfiguration);
	}

	public ReleaseService() {}

	public boolean startRelease(String releaseName) {

		String releaseFullName=getReleaseFullName(releaseName);
		String devBranch=scmBranchingConfiguration.getDevelopmentBranch();

		if(scmService.checkoutBranch(devBranch))
			if(scmService.pullRepo())
				if(scmService.createBranch(releaseFullName))
					if(scmService.checkoutBranch(releaseFullName))
						return true;
		return false;
	}

	public boolean publishRelease(String releaseName) {

		String releaseFullName = getReleaseFullName(releaseName);

		if (scmService.publishBranch(releaseFullName))
			return true;

		return false;
	}
	public boolean finishRelease(String releaseName){

		/*
		 * check all commits of release branches are in master
		 * scmService.compareBranches(master,release);
		 */
		String releaseFullName = getReleaseFullName(releaseName);

		String devBranch=scmBranchingConfiguration.getDevelopmentBranch();
		String masterBranch=scmBranchingConfiguration.getMasterBranch();
		/*
		 * TODO if developers can configure their email, it can be used as tagger email :) 
		 */
		PersonIdent tagger=new PersonIdent(userConfiguration.getGitUsername(),userConfiguration.getGitUsername());
		String tagName=project.getVersion();
		String tagMessage="tagging "+releaseName;

		String mergeMessage1 = "merging release [" + releaseName + "] to development stream before finishing release";
		String mergeMessage2 = "merging release [" + releaseName + "] to master before finishing release";

		String successMessage=" release is completed now [" + releaseFullName + "] branch is merged in to ["
				+ masterBranch+" and "+devBranch + "] and deleted from local and remote repositories.";


		String head=Constants.GIT_REFS_HEAD;
		Repository repo=scmService.getGit().getRepository();

		List<String> mavenGoals=new LinkedList<>();
		/*
		 * TODO options/parameters to version plugin should be provided by the user for more flexibility
		 */
		String replaceSNAPSHOTString="versions:force-releases -DprocessDependencies=true";
		String useLatestVersion="versions:use-latest-releases -DprocessDependencies=true";
		String commitChanges="versions:commit";

		mavenGoals.add(replaceSNAPSHOTString);
		mavenGoals.add(useLatestVersion);
		mavenGoals.add(commitChanges);


		MavenCommandExecutor mavenCommandExecutor=new MavenCommandExecutor();
		try{
			if(scmService.checkoutBranch(devBranch))
				if(scmService.mergeBranches(devBranch, releaseFullName,	mergeMessage1))
					if(scmService.pushBranch(devBranch))
						if(scmService.checkoutBranch(releaseFullName))
							if(mavenCommandExecutor.executeMavenGoals(mavenGoals))
								if(scmService.commitAllChangesToTackedFiles("setting latest dependency versions for release"))
									if(scmService.mergeBranches(masterBranch, releaseFullName,	mergeMessage2))
										if(scmService.createTag(repo.resolve(head), tagger, tagName, tagMessage))
											if(scmService.pushBranch(masterBranch))
												if(scmService.deleteBranchFromLocal(releaseFullName))
													if(scmService.deleteBranchFromRemote(releaseFullName)){
														logger.info(successMessage);
														return true;
													}
		}catch(Exception e){
			logger.error("error while finishing release:");
		}

		return false;
	}
	private String getReleaseFullName(String releaseName) {
		String releaseFullName=scmBranchingConfiguration.getReleaseBranchPrefix()+releaseName;
		return releaseFullName;
	}

}
