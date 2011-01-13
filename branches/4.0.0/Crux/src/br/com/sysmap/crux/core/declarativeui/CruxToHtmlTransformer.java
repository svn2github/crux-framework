/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.declarativeui;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.w3c.dom.Document;

import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.declarativeui.template.TemplatesPreProcessor;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.Environment;
import br.com.sysmap.crux.core.utils.StreamUtils;

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
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws InterfaceConfigException 
	 */
	public static void generateHTML(String screenId, InputStream file, OutputStream out, boolean escapeXML)
	{
		init();
		
		try
		{
			StringWriter buff = new StringWriter();
			Document source = loadCruxPage(file);
			HTMLBuilder htmlBuilder = new HTMLBuilder(escapeXML, mustIndent());
			htmlBuilder.build(screenId, source, buff);
			String result = buff.toString();
			StreamUtils.write(new ByteArrayInputStream(result.getBytes(outputCharset)), out, false);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param filePath
	 * @param out
	 * @throws InterfaceConfigException
	 */
	public static void generateHTML(String screenId, String filePath, OutputStream out, boolean escapeXML)
	{
		try
		{
			generateHTML(screenId, new FileInputStream(filePath), out, escapeXML);
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
	 * @throws InterfaceConfigException
	 */
	private static Document loadCruxPage(InputStream file) throws InterfaceConfigException
	{
		try
		{
			Document document = documentBuilder.parse(file);
			return preprocess(document);
		}
		catch (Exception e)
		{
			throw new InterfaceConfigException(e.getMessage(), e);
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