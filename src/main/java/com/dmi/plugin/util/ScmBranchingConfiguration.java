package com.dmi.plugin.util;

public class ScmBranchingConfiguration {
	private String productionBranch;
	private String developmentBranch;
	private String releaseBranchPrefix;
	private String featureBranchPrefix;
	private String hotFixBranchPrefix;
	private String versionPrefix;
	
	public ScmBranchingConfiguration(){
		
	}
	public String getProductionBranch() {
		return productionBranch;
	}
	public void setProductionBranch(String productionBranch) {
		this.productionBranch = productionBranch;
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
	public String getHotFixBranchPrefix() {
		return hotFixBranchPrefix;
	}
	public void setHotFixBranchPrefix(String hotFixBranchPrefix) {
		this.hotFixBranchPrefix = hotFixBranchPrefix;
	}
	public String getVersionPrefix() {
		return versionPrefix;
	}
	public void setVersionPrefix(String versionPrefix) {
		this.versionPrefix = versionPrefix;
	}
	

}
