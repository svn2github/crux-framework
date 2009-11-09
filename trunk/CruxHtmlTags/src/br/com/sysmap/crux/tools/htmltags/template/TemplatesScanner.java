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
package br.com.sysmap.crux.tools.htmltags.template;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;
import br.com.sysmap.crux.scannotation.archiveiterator.Filter;
import br.com.sysmap.crux.scannotation.archiveiterator.IteratorFactory;
import br.com.sysmap.crux.tools.htmltags.HTMLTagsMessages;

/**
 * 
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class TemplatesScanner 
{
	private static final Log logger = LogFactory.getLog(TemplatesScanner.class);
	private static final TemplatesScanner instance = new TemplatesScanner();
	private HTMLTagsMessages messages = MessagesFactory.getMessages(HTMLTagsMessages.class);
	private DocumentBuilder documentBuilder;
	private static URL[] urlsForSearch = null;
	private static final Lock lock = new ReentrantLock(); 
	
	/**
	 * 
	 */
	private TemplatesScanner() 
	{
		try
		{
			this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new TemplateException(messages.templatesScannerErrorBuilderCanNotBeCreated(), e);
		}
	}
	
	protected transient String[] ignoredPackages = {"javax", "java", "sun", "com.sun", "org.apache", 
													"net.sf.saxon", "javassist", "org.json", "com.extjs",
													"com.metaparadigm", "junit"};

	/**
	 * 
	 * @return
	 */
	public String[] getIgnoredPackages()
	{
		return ignoredPackages;
	}

	/**
	 * 
	 * @param ignoredPackages
	 */
	public void setIgnoredPackages(String[] ignoredPackages)
	{
		this.ignoredPackages = ignoredPackages;
	}

	/**
	 * 
	 * @param urls
	 */
	private void scanArchives(URL... urls)
	{
		for (URL url : urls)
		{
			Filter filter = new Filter()
			{
				public boolean accepts(String fileName)
				{
					if (fileName.endsWith(".template.xml"))
					{
						if (fileName.startsWith("/")) fileName = fileName.substring(1);
						if (!ignoreScan(fileName.replace('/', '.')))
						{
							Document template;
							try
							{
								template = documentBuilder.parse(getClass().getResourceAsStream(fileName));
								Templates.registerTemplate(getTemplateId(fileName), template);
							}
							catch (Exception e)
							{
								logger.error(messages.templatesScannerErrorParsingTemplateFile(fileName));
								return false;
							}
							return true;
						}
					}
					return false;
				}
			};

			try
			{
				IteratorFactory.create(url, filter);
			}
			catch (IOException e)
			{
				throw new TemplateException(messages.templatesScannerInitializationError(e.getLocalizedMessage()), e);
			}
		}
	}

	/**
	 * 
	 */
	public void scanArchives()
	{
		if (urlsForSearch == null)
		{
			initialize(ScannerURLS.getURLsForSearch());
		}
		scanArchives(urlsForSearch);
	}
	
	/**
	 * 
	 * @param urls
	 */
	public static void initialize(URL[] urls)
	{
		lock.lock();
		try
		{
			urlsForSearch = urls;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	private String getTemplateId(String fileName)
	{
		fileName = fileName.substring(0, fileName.length() - 13);
		int indexStartId = fileName.lastIndexOf('/');
		if (indexStartId > 0)
		{
			fileName = fileName.substring(indexStartId);
		}
		indexStartId = fileName.lastIndexOf('.');
		if (indexStartId > 0)
		{
			fileName = fileName.substring(indexStartId);
		}
		
		return fileName;
	}
	
	/**
	 * 
	 * @param intf
	 * @return
	 */
	private boolean ignoreScan(String intf)
	{
		for (String ignored : ignoredPackages)
		{
			if (intf.startsWith(ignored + "."))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return
	 */
	public static TemplatesScanner getInstance()
	{
		return instance;
	}
	
}
