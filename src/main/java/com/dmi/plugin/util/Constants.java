package com.dmi.plugin.util;

public class Constants {
	
	public static final String GIT_CONFIG_BRANCH_SECTION="branch";
	public static final String GIT_REMOTE_CONFIG_SECTION="remote";
	public static final String GIT_DEFAULT_REMOTE_ALIAS_NAME="origin";
	
	public static final String GIT_DEFAULT_SRC_REF_SPEC="+refs/heads/*";
	public static final String GIT_DEFAULT_DST_REF_SPEC="refs/remotes/origin/*";
	
	public static final String GIT_DEFAULT_SRC_TAGS_REF_SPEC="+refs/tags/*";
	public static final String GIT_DEFAULT_DST_TAGS_REF_SPEC="refs/remotes/origin/tags/*";
	
	public static final String GIT_DEFAULT_REFS_HEADS="refs/heads/";
	public static final String GIT_DEFAULT_REFS_REMOTES="refs/remotes/origin/";
	
	
	
	public static final String GIT_DEFAULT_GIT_DIR_LOCATION="/.git";
	
	
	
	public static final String GIT_REFS_HEAD="HEAD";
	
	/*
	 * scm branching defaults
	 */
	public static final String WORKFLOW_DEFAULT_MASTER_BRANCH="master";
	public static final String WORKFLOW_DEFAULT_DEVELOPMENT_BRANCH="develop";
	
	public static final String WORKFLOW_DEFAULT_FEATURE_BRANCH_PREFIX="feature/";
	public static final String WORKFLOW_DEFAULT_RELEASE_BRANCH_PREFIX="release/";
	public static final String WORKFLOW_DEFAULT_HOTFIX_BRANCH_PREFIX="hotfix/";
	public static final String WORKFLOW_DEFAULT_TAG_PREFIX="v";
	
}
