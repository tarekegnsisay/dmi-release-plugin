package com.dmi.plugin.service;

import com.dmi.plugin.scm.GitScmManagerService;

public class AbstractApplicationService {
	protected GitScmManagerService scmService;
	public boolean createBranch(String branchNName) {
		return false;
	}
	public boolean deleteBranch(String branchName) {
		return false;
		
	}

}
