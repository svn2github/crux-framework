/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Gesse S. F. Dafe - <code>gesse@sysmap.com.br</code>
 */
public class FileUtils
{
	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException
    {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int read = 0;
		byte[] buff = new byte[1024];
		while((read = in.read(buff)) > 0)
		{
			out.write(buff, 0, read);
		}
		in.close();
		out.flush();
		out.close();
				
		return new String(out.toByteArray());
    }
	
	/**
	 * @param text
	 * @param f
	 * @throws IOException
	 */
	public static void write(String text, File f) throws IOException
    {
		FileOutputStream out = new FileOutputStream(f);
		out.write(text.getBytes());
		out.close();
    }
	
	/**
	 * @param in
	 * @param f
	 * @throws IOException
	 */
	public static void write(InputStream in, File f) throws IOException
    {
		FileOutputStream out = new FileOutputStream(f);
		
		int read = 0;
		byte[] buff = new byte[1024];
		while((read = in.read(buff)) > 0)
		{
			out.write(buff, 0, read);
		}
		in.close();
		out.close();
    }

	/**
	 * @param file
	 */
	public static boolean recursiveDelete(File file)
	{
		boolean success = true;
		
		if(file != null && file.exists())
		{
			if(file.isDirectory())
			{
				File[] files = file.listFiles();
				
				if(files != null)
				{
					for (File child : files)
					{
						recursiveDelete(child);
					}
				}
			}
			
			success = file.delete();
		}
		
		return success;
	}
	
	/**
	 * @param sourceDir
	 * @param destDir
	 * @throws IOException 
	 */
	public static void copyFilesFromDir(File sourceDir, File destDir) throws IOException
	{
		copyFilesFromDir(sourceDir, destDir, null, 0);
	}			

	/**
	 * @param sourceDir
	 * @param destDir
	 * @param includes
	 * @param excludes
	 * @throws IOException
	 */
	public static void copyFilesFromDir(File sourceDir, File destDir, String includes, String excludes) throws IOException
	{
		FilePatternHandler handler = new FilePatternHandler(includes, excludes);
		copyFilesFromDir(sourceDir, destDir, handler, sourceDir.getCanonicalPath().length());
	}
	
	/**
	 * @param sourceDir
	 * @param destDir
	 * @param handler
	 * @throws IOException
	 */
	private static void copyFilesFromDir(File sourceDir, File destDir, FilePatternHandler handler, int inputDirNameLength) throws IOException
	{
		if(!destDir.exists())
		{
			destDir.mkdirs();
		}
		
		File[] files = sourceDir.listFiles();
		for (File file : files)
		{
			if (handler == null || handler.isValidEntry(getEntryName(file, inputDirNameLength)))
			{
				if(!file.isDirectory())
				{
					File destFile = new File(destDir, file.getName());
					FileInputStream stream = new FileInputStream(file);
					write(stream, destFile);
				}
				else
				{
					File dir = new File(destDir, file.getName());
					copyFilesFromDir(file, dir, handler, inputDirNameLength);
				}
			}
		}
	}

	/**
	 * @param source
	 * @param inputDirNameLength
	 * @return
	 * @throws IOException
	 */
	private static String getEntryName(File source, int inputDirNameLength) throws IOException
	{
		String name = source.getCanonicalPath().substring(inputDirNameLength).replace("\\", "/");
		if (name.startsWith("/"))
		{
			name = name.substring(1);
		}
		if (source.isDirectory() && !name.endsWith("/"))
		{
			name += "/";
		}
			
		return name;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getTempDir()
	{
		String tmpDir = System.getProperty("java.io.tmpdir");
		if (!tmpDir.endsWith("/") && !tmpDir.endsWith("\\"))
		{
			tmpDir += File.separator;
		}
		return tmpDir;
	}
	
	/**
	 * 
	 * @return
	 */
	public static File getTempDirFile()
	{
		return new File(getTempDir());
	}
	
	/**
	 * @param sourceLocation
	 * @param targetLocation
	 * @throws IOException
	 */
	public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException 
	{
		if (sourceLocation.isDirectory()) 
		{
			if (!targetLocation.exists()) 
			{
				targetLocation.mkdirs();
			}

			String[] children = sourceLocation.list();
			for (int i=0; i<children.length; i++) 
			{
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} 
		else 
		{
			StreamUtils.write(new FileInputStream(sourceLocation), new FileOutputStream(targetLocation), true);
		}
	}
	
	/**
	 * Recursively gets all files with a given extension contained in a directory
	 * @param path
	 * @param extension
	 * @param result
	 */
	public static List<File> scanFiles(File path, final String extension, ArrayList<File> result)
	{
		if(path.isDirectory())
		{
			File[] found = path.listFiles(new FileFilter()
			{
				public boolean accept(File file)
				{
					return file.getName().endsWith(extension) || file.isDirectory();
				}
			});
			
			for (File file : found)
			{
				scanFiles(file, extension, result);
			}
		}
		else
		{
			if(path.getName().endsWith(extension))
			{
				result.add(path);
			}
		}
		
		return result;
	}
	
	/**
	 * Unzips a file to an output directory 
	 * @param zippedFile
	 * @param outputDirectory
	 * @throws IOException
	 */
	public static void unzip(File zippedFile, File outputDirectory) throws IOException
	{
		Set<String> added = new HashSet<String>();
		
		ZipInputStream inStream = null;
		
		try
		{
			inStream = new ZipInputStream(new FileInputStream(zippedFile));

			ZipEntry entry;
			byte[] buffer = new byte[1024];

			while ((entry = inStream.getNextEntry()) != null) 
			{
				String name = entry.getName();
				if (name != null && name.length() > 0 && !added.contains(name))
				{
					File outputFile = new File(outputDirectory, name);

					if (entry.isDirectory())
					{
						outputFile.mkdirs();
					}
					else
					{
						extractFileFromZip(inStream, buffer, outputFile);
					}
				
					added.add(name);	        		 
				}
			}

			inStream.close();
		}
		finally
		{
			if (inStream != null)
			{
				inStream.close();
			}
		}
	}

	/**
	 * Extracts a zip entry for a physical file
	 * @param inStream
	 * @param buffer
	 * @param outputFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void extractFileFromZip(ZipInputStream inStream, byte[] buffer, File outputFile) throws FileNotFoundException, IOException
    {
	    int nrBytesRead;
	    if(!outputFile.getParentFile().exists())
	    {
	    	outputFile.getParentFile().mkdirs();
	    }
	    
	    OutputStream outStream = new FileOutputStream(outputFile);
	    try
	    {
	    	while ((nrBytesRead = inStream.read(buffer)) > 0) 
	    	{
	    		outStream.write(buffer, 0, nrBytesRead);
	    	}
	    }
	    finally
	    {
	    	if (outStream != null)
	    	{
	    		outStream.close();
	    	}
	    }
    }
	
	/**
	 * Copies a file;
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException
	{
		FileOutputStream out = null;
		FileInputStream in = null;
		
		try
		{
			destination.getParentFile().mkdirs();
			out = new FileOutputStream(destination);
			in = new FileInputStream(source);
			byte[] buff = new byte[1024];
			int read = 0;
			while((read = in.read(buff)) > 0)
			{
				out.write(buff, 0, read);
			}
			out.flush();
		}
		finally
		{
			if(in != null) {in.close();}
			if(out != null) {out.close();}
		}
	}
}