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
package org.cruxframework.crux.core.declarativeui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.declarativeui.template.TemplatesPreProcessor;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;

/**
 * Process Crux view files, extracting metadata and generating the host html for
 * application pages.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ViewProcessor
{
	// Makes it easier to read the output files
	private static boolean forceIndent = false;
	private static String outputCharset;

	private static final Log log = LogFactory.getLog(ViewProcessor.class);
	private static DocumentBuilder documentBuilder = null;

	private static List<CruxXmlPreProcessor> preProcessors;
	private static final Lock lock = new ReentrantLock();

	/**
	 * 
	 * @param file
	 * @param device
	 * @return
	 */
	public static Document getView(InputStream file, String device)
	{
		init();
		
		try
		{
			return loadCruxPage(file, device);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Generate the HTML code from the view page.
	 * 
	 * @param screenId
	 * @param view
	 * @param out
	 */
	public static void generateHTML(String screenId, Document view, OutputStream out)
	{
		try
		{
			StringWriter buff = new StringWriter();
			ViewParser viewParser = new ViewParser(false, mustIndent());
			viewParser.generateHTMLHostPage(screenId, view, buff);
			String result = buff.toString();
			String outCharset = getOutputCharset();
			if (outCharset == null || outCharset.length() == 0)
			{
				throw new DeclarativeUITransformerException("Outputcharset is undefined. Check your web.xml file to ensure that DevModeInitializerListener is correctly configured.");
			}
			StreamUtils.write(new ByteArrayInputStream(result.getBytes(outCharset)), out, false);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Extract the widgets metadata from the  from the view page.
     *
	 * @param viewSource
	 * @return
	 */
	public static JSONObject extractWidgetsMetadata(Document viewSource)
	{
		try
		{
			ViewParser viewParser = new ViewParser(true, mustIndent());
			String metadata = viewParser.extractCruxMetaData(viewSource);
			return new JSONObject(metadata);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}	
	
	/**
	 * Makes it easier to read the output files
	 * @param force
	 */
	public static void setForceIndent(boolean force)
	{
		forceIndent = force;
	}

	/**
	 * @param outputCharset
	 */
	public static void setOutputCharset(String charset)
	{
		outputCharset = charset;
		CruxBridge.getInstance().registerPageOutputCharset(charset);
	}	

	/**
	 * @return
	 */
	public static String getOutputCharset()
	{
		if (outputCharset == null)
		{
			outputCharset = CruxBridge.getInstance().getOutputCharset();
		}
		return outputCharset;
	}
	
	/**
	 * Initializes the static resources
	 */
	private static void init()
	{
		if (documentBuilder == null)
		{
			lock.lock();

			if (documentBuilder == null)
			{
				try
				{
					DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
					builderFactory.setNamespaceAware(true);
					builderFactory.setIgnoringComments(true);
					
					documentBuilder = builderFactory.newDocumentBuilder();
					initializePreProcessors();
				}
				catch (Throwable e)
				{
					log.error("Error initializing cruxToHtmlTransformer.", e);
				}
				finally
				{
					lock.unlock();
				}
			}
		}
	}

	/**
	 * 
	 */
	private static void initializePreProcessors()
	{
		preProcessors = new ArrayList<CruxXmlPreProcessor>();
		preProcessors.add(new TemplatesPreProcessor());
	}

	/**
	 * @return
	 */
	private static boolean mustIndent()
	{
		return !Environment.isProduction() || forceIndent;
	}

	/**
	 * Loads Crux view page
	 * @param fileName
	 * @return
	 * @throws ViewParserException
	 */
	private static Document loadCruxPage(InputStream file, String device) throws ViewParserException
	{
		try
		{
			Document document = documentBuilder.parse(file);
			return preprocess(document, device);
		}
		catch (Exception e)
		{
			throw new ViewParserException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param doc
	 * @return
	 */
	private static Document preprocess(Document doc, String device)
	{
		for (CruxXmlPreProcessor preProcessor : preProcessors)
		{
			doc = preProcessor.preprocess(doc, device);
		}
		
		return doc;
	}
}