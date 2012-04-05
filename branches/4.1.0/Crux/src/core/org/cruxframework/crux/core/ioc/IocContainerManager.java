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
package org.cruxframework.crux.core.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.i18n.MessagesFactory;
import org.cruxframework.crux.core.server.ServerMessages;
import org.cruxframework.crux.core.server.scan.ClassScanner;
import org.cruxframework.crux.core.utils.ClassUtils;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocContainerManager
{
	private static final Log logger = LogFactory.getLog(IocContainerManager.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);

	private static boolean initialized = false;
	
	public synchronized static void initialize()
	{
		if (!initialized)
		{
			initialized = true;
			try
			{
				Set<String> configurations =  ClassScanner.searchClassesByInterface(IocConfiguration.class);
				if (configurations != null)
				{
					for (String configurationClassName : configurations)
					{
						Class<?> configurationClass = Class.forName(configurationClassName);
						if (!Modifier.isAbstract(configurationClass.getModifiers()) && IocContainerConfigurations.class.isAssignableFrom(configurationClass))
						{
							IocContainerConfigurations configuration = (IocContainerConfigurations)configurationClass.newInstance();
							if (configuration.isEnabled())
							{
								if (logger.isInfoEnabled())
								{
									logger.info(messages.iocContainerConfiguringModule(configurationClassName));
								}
								configuration.configure();
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				logger.error(messages.iocContainerManagerError(), e);
			}
		}
	}

	//TODO transformar o metodo abaixo em um metdo que vasculhe os controllers e datasources
	// checando por loops e montando configuracoes automaticamente para injecoes simples 
	//(@Inject simples, sem substituicao de classes ou uso de providers)
	private static void preventCreateLopping(Class<?> type, Set<String> added, Set<String> path)
    {
        for (Field field : type.getFields()) 
        {
        	String fieldName = field.getName();
			if (!added.contains(fieldName))
        	{
				added.add(fieldName);
				Class<?> fieldType = field.getType();
				Inject inject = field.getAnnotation(Inject.class);
				if ((inject != null) && (!Modifier.isStatic(field.getModifiers())) && ClassUtils.isPropertyVisibleToWrite(type, field));
				{
					if (path.contains(fieldType.getCanonicalName()))
					{
						throw new IoCException(messages.iocCreateLoopingError(type.getCanonicalName(), fieldType.getCanonicalName()));
					}
		        	Set<String> fieldPath = new HashSet<String>();
		        	fieldPath.addAll(path);
		        	fieldPath.add(fieldType.getCanonicalName());
					preventCreateLopping(fieldType, added, fieldPath);
				}
        	}
        }
        if (type.getSuperclass() != null)
        {
        	preventCreateLopping(type.getSuperclass(), added, path);
        }
    }

	
	
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	public static IocConfigImpl<?> getConfigurationForType(String className)
	{
		if (!initialized)
		{
			initialize();
		}
		
		return (IocConfigImpl<?>) IocContainerConfigurations.getConfigurationForType(className);
	}
	
	/**
	 * 
	 * @return
	 */
	public static Iterator<String> iterateClasses()
	{
		if (!initialized)
		{
			initialize();
		}
		return IocContainerConfigurations.iterateClasses();
	}
}
