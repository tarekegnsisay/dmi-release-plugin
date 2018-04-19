package com.dmi.scm.git;

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;

public class GitServicesParallelTestSuit {
	
	@Test
	public void testAllClassesInParallel() {
		@SuppressWarnings("rawtypes")
		Class [] classes= {
				GitScmServiceTest.class,
				GitRepositoryTest.class,
				BranchServiceTest.class,
				CommitServiceTest.class
				};
		/*
		 * TBD, needs deeper look at to test cases and folder structure
		 */
		
		//JUnitCore.runClasses(ParallelComputer.classes(), classes);
		
		/*
		 * Is there 100% independence between all test cases?? all uses the same uri?? TBD
		 */
		JUnitCore.runClasses(new ParallelComputer(true,true), classes);
	}

}
