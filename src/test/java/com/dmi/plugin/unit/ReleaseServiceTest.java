package com.dmi.plugin.unit;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Ignore;
import org.junit.Test;

import com.dmi.plugin.mojo.ReleaseStartMojo;

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
    public void testPomIsNotNull() throws Exception
    {
        assertNotNull( pom );             
    }
    
    @Test
    public void testPomExists() throws Exception
    {       
        assertTrue( pom.exists() );
    }
   
   @Test
    public void ReleaseStart() throws Exception
    {
        ReleaseStartMojo releaseStartMojo = (ReleaseStartMojo) lookupMojo( "start", pom );
        assertNotNull( releaseStartMojo );
        releaseStartMojo.execute();
    }
   
}
