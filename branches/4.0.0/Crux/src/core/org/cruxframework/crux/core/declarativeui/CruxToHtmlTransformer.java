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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.server.CruxBridge;
import org.cruxframework.crux.core.server.Environment;
import org.cruxframework.crux.core.utils.StreamUtils;
import org.w3c.dom.Document;


/**
 * Generates HTML output based on Crux widget tags
 * @author Gesse S. F. Dafe
 */
public class CruxToHtmlTransformer
{
	// Makes it easier to read the output files
	private static boolean forceIndent = false;
	private static String outputCharset;

	private static final Log log = LogFactory.getLog(CruxToHtmlTransformer.class);
	private static DeclarativeUIMessages messages = (DeclarativeUIMessages)MessagesFactory.getMessages(DeclarativeUIMessages.class);
	private static DocumentBuilder documentBuilder = null;

	private static List<CruxXmlPreProcessor> preProcessors;
	private static final Lock lock = new ReentrantLock();


	/**
	 * Executes the transformation
	 * 
	 * @param screenId
	 * @param file
	 * @param out
	 * @param escapeXML
	 * @param generateWidgetsMetadata
	 */
	public static void generateHTML(String screenId, InputStream file, OutputStream out, boolean escapeXML, boolean generateWidgetsMetadata)
	{
		init();
		
		try
		{
			StringWriter buff = new StringWriter();
			Document source = loadCruxPage(file);
			HTMLBuilder htmlBuilder = new HTMLBuilder(escapeXML, generateWidgetsMetadata, mustIndent());
			htmlBuilder.build(screenId, source, buff);
			String result = buff.toString();
			StreamUtils.write(new ByteArrayInputStream(result.getBytes(getOutputCharset())), out, false);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param screenId
	 * @param filePath
	 * @param out
	 * @param escapeXML
	 * @param generateWidgetsMetadata
	 */
	public static void generateHTML(String screenId, String filePath, OutputStream out, boolean escapeXML, boolean generateWidgetsMetadata)
	{
		try
		{
			generateHTML(screenId, new FileInputStream(filePath), out, escapeXML, generateWidgetsMetadata);
		}
		catch (FileNotFoundException e)
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
					log.error(messages.cruxToHtmlTransformerInitializeError(e.getMessage()), e);
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
	 * Loads Crux page
	 * @param fileName
	 * @return
	 * @throws HTMLBuilderException
	 */
	private static Document loadCruxPage(InputStream file) throws HTMLBuilderException
	{
		try
		{
			Document document = documentBuilder.parse(file);
			return preprocess(document);
		}
		catch (Exception e)
		{
			throw new HTMLBuilderException(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param doc
	 * @return
	 */
	private static Document preprocess(Document doc)
	{
		for (CruxXmlPreProcessor preProcessor : preProcessors)
		{
			doc = preProcessor.preprocess(doc);
		}
		
		return doc;
	}
}