package com.dmi.plugin.util;

public class ScmBranchingConfiguration {
	private String masterBranch;
	private String developmentBranch;
	private String releaseBranchPrefix;
	private String featureBranchPrefix;
	private String hotfixBranchPrefix;
	private String tagPrefix;
	
	public ScmBranchingConfiguration(){
		
	}
	public String getMasterBranch() {
		return masterBranch;
	}
	public void setMasterBranch(String masterBranch) {
		this.masterBranch = masterBranch;
	}
	public String getDevelopmentBranch() {
		return developmentBranch;
	}
	public void setDevelopmentBranch(String developmentBranch) {
		this.developmentBranch = developmentBranch;
	}
	public String getReleaseBranchPrefix() {
		return releaseBranchPrefix;
	}
	public void setReleaseBranchPrefix(String releaseBranchPrefix) {
		this.releaseBranchPrefix = releaseBranchPrefix;
	}
	public String getFeatureBranchPrefix() {
		return featureBranchPrefix;
	}
	public void setFeatureBranchPrefix(String featureBranchPrefix) {
		this.featureBranchPrefix = featureBranchPrefix;
	}
	public String getHotfixBranchPrefix() {
		return hotfixBranchPrefix;
	}
	public void setHotfixBranchPrefix(String hotfixBranchPrefix) {
		this.hotfixBranchPrefix = hotfixBranchPrefix;
	}
	public String getTagPrefix() {
		return tagPrefix;
	}
	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
	}
	

}
