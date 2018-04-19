package com.dmi.plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import com.dmi.plugin.mojo.feature.FeatureStartMojo;

public class FeatureServiceTest extends AbstractMojoTestCase {
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
	public void testFeatureStart() throws Exception
	{
		FeatureStartMojo featureStartMojo = (FeatureStartMojo) lookupMojo( "feature-start", pom );
		assertNotNull( featureStartMojo );
		featureStartMojo.execute();
	}

}
