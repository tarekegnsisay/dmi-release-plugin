package com.dmi.plugin.mojo;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import com.dmi.plugin.mojo.feature.FeatureStartMojo;
import com.dmi.plugin.mojo.feature.PublishFeatureMojo;

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
	public void testStartFeature() throws Exception
	{
		FeatureStartMojo featureStartMojo = (FeatureStartMojo) lookupMojo( "feature-start", pom );
		assertNotNull( featureStartMojo );
		featureStartMojo.execute();
	}
	@Test
	public void testPublishFeature() throws Exception
	{
		PublishFeatureMojo publishFeatureMojo = (PublishFeatureMojo) lookupMojo( "feature-publish", pom );
		assertNotNull( publishFeatureMojo );
		publishFeatureMojo.execute();
	}

}
