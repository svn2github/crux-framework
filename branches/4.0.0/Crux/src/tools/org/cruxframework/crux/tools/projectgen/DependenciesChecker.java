/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.tools.projectgen;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.utils.FileUtils;
import org.cruxframework.crux.tools.parameters.ConsoleParameter;
import org.cruxframework.crux.tools.parameters.ConsoleParameterOption;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessingException;
import org.cruxframework.crux.tools.parameters.ConsoleParametersProcessor;

/**
 * A program to check for Crux required Jar and, eventually, 
 * download and install them.
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DependenciesChecker
{
	private static final String REPO_GWT_SERVLET_DEPS_JAR = "repo/gwt-servlet-deps.jar";
	private static final String REPO_GWT_SERVLET_JAR = "http://repo1.maven.org/maven2/com/google/gwt/gwt-servlet/2.2.0/gwt-servlet-2.2.0.jar";
	private static final String REPO_GWT_USER_JAR = "http://repo1.maven.org/maven2/com/google/gwt/gwt-user/2.2.0/gwt-user-2.2.0.jar";
	private static final String REPO_GWT_DEV_JAR = "http://repo1.maven.org/maven2/com/google/gwt/gwt-dev/2.2.0/gwt-dev-2.2.0.jar";

	private static final int GWT_DEV_TOTAL_BYTES = 27914742;
	private static final int GWT_USER_TOTAL_BYTES = 10682696;
	private static final int GWT_SERVLET_TOTAL_BYTES = 4380952;
	private static final int GWT_SERVLET__DEPS_TOTAL_BYTES = 84088;
	
	/**
     * Check all crux dependencies. If needed (and requested) install the jars.
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
    {
		try
		{
			ConsoleParametersProcessor parametersProcessor = createParametersProcessor();
			Map<String, ConsoleParameter> parameters = parametersProcessor.processConsoleParameters(args);
			if (parameters.containsKey("-help") || parameters.containsKey("-h"))
			{
				parametersProcessor.showsUsageScreen();
				System.exit(1);
			}
			else
			{
				boolean downloadDependenciesIfNeeded = parameters.containsKey("-downloadDependencies");
				String gwtFolder = parameters.containsKey("gwtFolder")?parameters.get("gwtFolder").getValue():null;
				checkDependencies(downloadDependenciesIfNeeded, gwtFolder);
			}
		}
		catch (ConsoleParametersProcessingException e)
		{
			System.out.println("Program aborted");
			System.exit(1);
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			System.exit(1);
		}
    }

	/**
	 * Ensure that all required jars are present. If not try to install them or raise an error.
	 * @param downloadDependenciesIfNeeded - if true, makes Crux to try retrieve the jars from a maven repository.
	 * @param gwtFolder - If not null, try to install the jars from a folder on disk.
	 */
	private static void checkDependencies(boolean downloadDependenciesIfNeeded, String gwtFolder)
    {
		List<Dependency> requiredDeps = new ArrayList<Dependency>();
		
		File jarFile = new File("./lib/build/gwt-dev.jar");
		if (!jarFile.exists())
		{
			requiredDeps.add(new Dependency("gwt-dev.jar", "./lib/build", REPO_GWT_DEV_JAR, GWT_DEV_TOTAL_BYTES));
		}
		jarFile = new File("./lib/build/gwt-user.jar");
		if (!jarFile.exists())
		{
			requiredDeps.add(new Dependency("gwt-user.jar", "./lib/build", REPO_GWT_USER_JAR, GWT_USER_TOTAL_BYTES));
		}
		jarFile = new File("./lib/web-inf/gwt-servlet.jar");
		if (!jarFile.exists())
		{
			requiredDeps.add(new Dependency("gwt-servlet.jar", "./lib/web-inf", REPO_GWT_SERVLET_JAR, GWT_SERVLET_TOTAL_BYTES));
		}
/*		jarFile = new File("./lib/web-inf/gwt-servlet-deps.jar");
		if (!jarFile.exists())
		{
			requiredDeps.add(new Dependency("gwt-servlet-deps.jar", "./lib/web-inf", REPO_GWT_SERVLET_DEPS_JAR));
		}
*/		
		if (requiredDeps.size() > 0)
		{
			if (!downloadDependenciesIfNeeded && (gwtFolder == null || gwtFolder.length() == 0))
			{
				throw new RuntimeException("Crux required jars are missing! Please run again passing [-downloadDependencies]" +
						" or [gwtFolder] option. For help, call passing [-h] option.");
			}

			if (gwtFolder != null && gwtFolder.length() > 0)
			{
				copyCruxDependencies(requiredDeps, new File (gwtFolder));
			}
			else if (downloadDependenciesIfNeeded)
			{
				downloadCruxDependencies(requiredDeps);
			}
		}
    }

	/**
	 * Install the jars from a folder on disk.
	 * @param requiredDeps Dependencies to install
	 * @param gwtFolder The folder
	 */
	private static void copyCruxDependencies(List<Dependency> requiredDeps, File gwtFolder)
    {
	    System.out.println("Copying required jars from folder "+gwtFolder.getName()+"...");
		if (!gwtFolder.exists())
	    {
	    	throw new RuntimeException("gwtFolder does not exist!");
	    }
	    
	    for (Dependency dependency : requiredDeps)
        {
	    	try
            {
	    	    System.out.println("Copying file: "+dependency.getJarName());
	            FileUtils.copyFilesFromDir(gwtFolder, dependency.getDestFolder(), dependency.getJarName(), null);
            }
            catch (IOException e)
            {
            	throw new RuntimeException("Error copying required jar from gwtFolder.", e);
            }
        }
	    System.out.println("All required jars installed.");
    }

	/**
	 * Retrieve the jars from the web.
	 * @param requiredDeps Dependencies to install
	 */
	private static void downloadCruxDependencies(List<Dependency> requiredDeps)
    {
	    System.out.println("Downloading required jars...");
	    for (Dependency dependency : requiredDeps)
        {
	    	downloadDependency(dependency);
        }
	    
	    System.out.println("All required jars installed.");
    }

	/**
	 * Retrieve a jar file from the web.
	 * @param requiredDeps Dependencies to install
	 */
	private static void downloadDependency(Dependency dependency)
	{
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try
		{
			System.out.println("Downloading file "+ dependency.getJarName()+"...");
			URL url = new URL(dependency.getResourceURL());
			URLConnection urlc = url.openConnection();

			in = new BufferedInputStream( urlc.getInputStream() );
			out = new BufferedOutputStream(new FileOutputStream(new File(dependency.getDestFolder(), dependency.getJarName())));
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			int blockSize = 4 * 1024;
			byte[] buf = new byte[blockSize]; // 4K buffer
			int bytesRead;
			int progressBytes = 0;
			int updateProgressBar = 0;
			while ((bytesRead = in.read(buf)) != -1) 
			{
				out.write(buf, 0, bytesRead);
				progressBytes+=bytesRead;
				updateProgressBar++;
				if (updateProgressBar > 10)
				{
					double percent = (progressBytes / dependency.getSize());
					System.out.print("\r"+percentFormat.format(percent));
					updateProgressBar = 0;
				}
			}
			System.out.println("\r100%  ");
		}
		catch (Exception e) 
		{
			throw new RuntimeException("Error downloading file "+ dependency.getJarName());
		}
		finally
		{
			if (in != null)
			{
				try{in.close();}catch (IOException ioe){}
			}
			if (out != null)
			{
				try{out.close();}catch (IOException ioe){}
			}
		}
	}

	/** Create a processor for command line parameters
	 * @return
	 */
	private static ConsoleParametersProcessor createParametersProcessor()
	{
		ConsoleParametersProcessor parametersProcessor = new ConsoleParametersProcessor("start");
		ConsoleParameter parameter = new ConsoleParameter("gwtFolder", "The folder containing GWT jars.", false, true);
		parameter.addParameterOption(new ConsoleParameterOption("folderName", "The name of the folder"));
		parametersProcessor.addSupportedParameter(parameter);
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-downloadDependencies", "Download and install dependencies before start.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-help", "Display the usage screen.", false, true));
		parametersProcessor.addSupportedParameter(new ConsoleParameter("-h", "Display the usage screen.", false, true));
		return parametersProcessor;
	}
	
	/**
	 * Represents a jar dependency from Crux project
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class Dependency
	{
		private final String jarName;
		private final File destFolder;
		private final String resourceURL;
		private final int size;

		Dependency(String jarName, String destFolder, String resourceURL, int size)
        {
			this.jarName = jarName;
			this.size = size;
			this.destFolder = new File(destFolder);
			this.resourceURL = resourceURL;
        }
		
		String getJarName()
        {
        	return jarName;
        }
		String getResourceURL()
        {
        	return resourceURL;
        }
		File getDestFolder()
		{
			return destFolder;
		}
		double getSize()
		{
			return size;
		}
	}
}
