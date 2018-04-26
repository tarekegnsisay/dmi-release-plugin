### `gitflow based *dmi* workflow maven plugin`
 - What is this
	 - **gitflow** branching model based, maven plugin
	 - **java** application to manage your git repo
	 - **costomizable** workflow
 - What goals are implemented?

	a. **Feature**

		mvn go:feature-start
		mvn go:feature-publish
		mvn go:feature-finish

	b. **Release**

		mvn go:release-start
		mvn go:release:publish
		mvn go:release:abort
		mvn go:release-finish

	  c. **Hotfix**

		mvn go:hotfix-start
		mvn go:hotfix-publish
		mvn go:hotfix-finish

 3. What are the **pre-requisites** to use the plugin?
		 - Install Java, Maven, Git
		 - Install it since it is a maven project you can do: 
					

 - [ ] Install Java, Maven, Git
 - [ ] Install it since it is a maven project you can do: `mvn clean install`
 - [ ] Put the following code to `<pluginGroups>…</pluginGroups>` section of maven **settings.xml** file, since the plugin is not published to remote repository 
 

	    <pluginGroup>com.dmi.maven.plugins</pluginGroup>

 

Once this is done any of the **goals** mentioned above can be used, however `<scm></scm>` section of pom.xml file must contain valid **git** repository.

Example

    <scm>
    	<connection>scm:git:https://github.com/musema/dmi-workflow-test.git</connection>
    </scm> 
