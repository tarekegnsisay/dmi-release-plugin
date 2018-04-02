package com.dmi.plugin.scm;

public abstract class AbstractScmManagerService {
	
	public abstract void cloneRepo();
	public abstract void stageFiles();
	public abstract void commitChanges();
	public abstract void pushBranch();
	public abstract void pushAllBranches();

}
