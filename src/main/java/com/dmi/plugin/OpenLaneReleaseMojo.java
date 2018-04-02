package com.dmi.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which releases artifact.
 *
 * @author musema
 */
@Mojo( name = "release", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class OpenLaneReleaseMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
    	getLog().info("Started openlane release mojo");
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        File muse = new File( f, "muse.txt" );

        FileWriter w = null;
        try
        {
            w = new FileWriter(muse );

            w.write( "this is file about release process" );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error creating file muse " + muse, e );
        }
        finally
        {
            if ( w != null )
            {
                try
                {
                    w.close();
                }
                catch ( IOException e )
                {
                    // ignore
                }
            }
        }
    }
}
