package com.dmi.plugin.util;

public class UserConfiguration {
	private String gitUsername;
	private String gitPassword;
	private String artifactoryUsername;
	private String artifactoryPassword;
	
	public UserConfiguration() {}
	
	public String getGitUsername() {
		return gitUsername;
	}
	public void setGitUsername(String gitUsername) {
		this.gitUsername = gitUsername;
	}
	public String getGitPassword() {
		return gitPassword;
	}
	public void setGitPassword(String gitPassword) {
		this.gitPassword = gitPassword;
	}
	public String getArtifactoryUsername() {
		return artifactoryUsername;
	}
	public void setArtifactoryUsername(String artifactoryUsername) {
		this.artifactoryUsername = artifactoryUsername;
	}
	public String getArtifactoryPassword() {
		return artifactoryPassword;
	}
	public void setArtifactoryPassword(String artifactoryPassword) {
		this.artifactoryPassword = artifactoryPassword;
	}
	

}
