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
package org.cruxframework.crux.core.rebind.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.server.ServerMessages;
import org.cruxframework.crux.core.server.scan.ClassScanner;


/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DataSources 
{
	private static final Log logger = LogFactory.getLog(DataSources.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, String> dataSources;
	
	/**
	 * 
	 */
	public static void initialize()
	{
		if (dataSources != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (dataSources != null)
			{
				return;
			}
			
			initializeDataSources();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected static void initializeDataSources()
	{
		dataSources = new HashMap<String, String>();
		Set<String> dataSourceNames =  ClassScanner.searchClassesByInterface(DataSource.class);
		if (dataSourceNames != null)
		{
			for (String dataSource : dataSourceNames) 
			{
				try 
				{
					Class<? extends DataSource<?>> dataSourceClass = (Class<? extends DataSource<?>>) Class.forName(dataSource);
					org.cruxframework.crux.core.client.datasource.annotation.DataSource annot = 
								dataSourceClass.getAnnotation(org.cruxframework.crux.core.client.datasource.annotation.DataSource.class);
					if (annot != null)
					{
						if (dataSources.containsKey(annot.value()))
						{
							throw new CruxGeneratorException(messages.dataSourcesDuplicatedDataSource(annot.value()));
						}
						dataSources.put(annot.value(), dataSourceClass.getCanonicalName());
					}
					else
					{
						String simpleName = dataSourceClass.getSimpleName();
						if (simpleName.length() >1)
						{
							simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
						}
						else
						{
							simpleName = simpleName.toLowerCase();
						}
						if (dataSources.containsKey(simpleName))
						{
							throw new CruxGeneratorException(messages.dataSourcesDuplicatedDataSource(simpleName));
						}
						dataSources.put(simpleName, dataSourceClass.getCanonicalName());
					}
				} 
				catch (Throwable e) 
				{
					logger.error(messages.dataSourcesDataSourceInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getDataSource(String name)
	{
		if (dataSources == null)
		{
			initialize();
		}
		
		return dataSources.get(name);
	}
	
	
	/**
	 * @return
	 */
	public static Iterator<String> iterateDataSources()
	{
		if (dataSources == null)
		{
			initialize();
		}
		
		return dataSources.keySet().iterator();
	}
}
