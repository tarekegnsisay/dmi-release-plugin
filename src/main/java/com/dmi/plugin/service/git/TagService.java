package com.dmi.plugin.service.git;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.dmi.plugin.util.Constants;

public class TagService {
	
	final static Logger logger = Logger.getLogger(TagService.class);

	public static boolean createTag(Git git, UsernamePasswordCredentialsProvider userCredential, ObjectId objectId,
			PersonIdent tagger, String tagName, String tagMessage) {

		boolean isTagCreated = false;
		Ref tagRef = null;
		Repository repository = git.getRepository();

		if (!RepositoryAndCloneService.hasCommits(repository)) {

		}

		if (tagger == null) {
			tagger = new PersonIdent("No tagger specified", "no_tagger@company.com");
		}

		boolean isValid = isValidNewTagName(git, tagName);

		if (!isValid) {
			logger.info("tag already exist or not valid name: " + tagName);
			return false;
		}

		TagCommand tagCommand = git.tag();
		tagCommand.setObjectId(CommitService.getCommitByObjectId(git, objectId));
		tagCommand.setName(tagName);
		tagCommand.setTagger(tagger);
		tagCommand.setMessage(tagMessage);
		try {
			tagRef = tagCommand.call();
			isTagCreated = tagRef == null ? false : true;

			RefSpec pushSpec = new RefSpec();
			pushSpec.setSourceDestination("refs/tags/" + tagName, "refs/remotes/origin/" + tagName);
			git.push().setRefSpecs(pushSpec).setPushTags().setCredentialsProvider(userCredential).call();

		} catch (Exception e) {
			logger.info("something went wrong creating a tag: [" + tagName + "] " + e.getMessage());
		}

		if (isTagCreated)
			logger.info("new tag was created with tag name: " + tagName);

		return isTagCreated;
	}

	public static boolean deleteTag(Git git, String tagName, UsernamePasswordCredentialsProvider userCredential) {
		RefSpec deleteSpec = new RefSpec(":" + "refs/tags/" + tagName);

		try {
			git.tagDelete().setTags(tagName).call();
			git.push().setRefSpecs(deleteSpec).setRemote(Constants.GIT_DEFAULT_REMOTE_ALIAS_NAME)
					.setCredentialsProvider(userCredential).call();
			logger.info("tag deleted: [" + tagName + "]");
			return true;
		} catch (Exception e) {
			logger.error("error deleting tag: [" + tagName + "] " + e.getMessage());
		}

		return false;
	}

	public static List<String> getAllTags(Git git) {
		List<String> tags = new ArrayList<>();
		try {
			Collection<Ref> tagRefs = git.lsRemote().setTags(true).call();
			for (Ref ref : tagRefs) {
				tags.add(ref.getName());
			}
		} catch (Exception e) {
			logger.error("error retrieving tags");
		}
		return tags;
	}

	private static boolean isValidNewTagName(Git git, String tagName) {
		/*
		 * check if we already have a tag [branch]? with this name
		 */
		boolean isValid = false;

		isValid = isTagExists(git, tagName) ? false : true;
		/*
		 * more validation criteria here
		 */
		return isValid;
	}

	public static boolean isTagExists(Git git, String tagName) {
		List<String> tags = getAllTags(git);
		for (String tag : tags) {
			int index = tag.lastIndexOf("/");
			String temp = tag.substring(index + 1);
			if (temp.equalsIgnoreCase(tagName)) {
				return true;
			}
		}
		return false;

	}

}
