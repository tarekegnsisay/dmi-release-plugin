package com.dmi.scm.git;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	GitScmServiceTest.class,
	GitRepositoryTest.class,
	BranchServiceTest.class,
	CommitServiceTest.class
})
public class GitSuite {

}
