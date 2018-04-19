package com.dmi.plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class PomFileTest extends AbstractMojoTestCase {

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
	public void testPomIsNotNull() throws Exception
	{
		assertNotNull( pom );             
	}

	@Test
	public void testPomExists() throws Exception
	{       
		assertTrue( pom.exists() );
	}

}
