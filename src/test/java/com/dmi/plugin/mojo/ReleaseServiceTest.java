package com.dmi.plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;
import org.junit.Test;

import com.dmi.plugin.mojo.release.ReleaseStartMojo;

public class ReleaseServiceTest extends AbstractMojoTestCase {
	private File pom;
	/** {@inheritDoc} */
	protected void setUp()
			throws Exception
	{
		super.setUp();
		pom=getTestFile( "src/test/resources/unit/plugin-test/pom.xml" );
	}

	/** {@inheritDoc} */
	protected void tearDown()
			throws Exception
	{
		super.tearDown();

	}
	@Test
	@Ignore
	public void testReleaseStart() throws Exception
	{
		ReleaseStartMojo releaseStartMojo = (ReleaseStartMojo) lookupMojo( "start", pom );
		assertNotNull( releaseStartMojo );
		//		releaseStartMojo.execute();
	}

}
