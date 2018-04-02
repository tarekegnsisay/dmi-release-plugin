package com.dmi.plugin.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

public class SystemCommandExecutor {
	public static SequenceInputStream executeCommand() throws IOException, InterruptedException {
		List<String> commands=new ArrayList<String>();
		commands.add("sh");
		commands.add("-c");
		commands.add("read");
		//commands.add("-a");
		ProcessBuilder pb=new ProcessBuilder(commands);
		pb.directory(new File(System.getProperty("user.home")));
		Process sp=pb.start();
		InputStream is=sp.getInputStream();
		InputStream es=sp.getErrorStream();
		sp.waitFor();
		SequenceInputStream sis=new SequenceInputStream(is,es);
		
		return sis;
	}
	
	public static boolean osCommandSupport() {
		boolean isWindows=System.getProperty("os.name").toLowerCase().startsWith("windows");
		return isWindows;
	}
}
