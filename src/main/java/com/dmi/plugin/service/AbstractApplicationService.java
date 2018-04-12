package com.dmi.plugin.service;

import com.dmi.plugin.scm.GitScmManagerService;
import com.dmi.plugin.util.ScmBranchingConfiguration;
import com.dmi.plugin.util.UserConfiguration;

public class AbstractApplicationService {
	protected GitScmManagerService scmService=null;
	protected ScmBranchingConfiguration scmBranchingConfiguration;
	protected UserConfiguration userConfiguration;
	public AbstractApplicationService() {}
	public AbstractApplicationService(ScmBranchingConfiguration scmBranchingConfiguration,
			UserConfiguration userConfiguration) {
		this.scmBranchingConfiguration=scmBranchingConfiguration;
		this.userConfiguration=userConfiguration;
	}
	public boolean createBranch(String branchNName) {
		return false;
	}
	public boolean deleteBranch(String branchName) {
		return false;
		
	}

}
