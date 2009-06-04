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
package br.com.sysmap.crux.core.rebind.screen.moduleshareable;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.sysmap.crux.core.client.component.ModuleShareable;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.screen.moduleshareable.annotation.ModuleShareableName;
import br.com.sysmap.crux.core.server.ServerMessages;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.server.scan.ScannerURLS;

public class Serializers 
{
	private static final Log logger = LogFactory.getLog(Serializers.class);
	private static ServerMessages messages = (ServerMessages)MessagesFactory.getMessages(ServerMessages.class);
	private static final Lock lock = new ReentrantLock();
	private static Map<String, Class<? extends ModuleShareable>> serializers;
	
	public static void initialize(URL[] urls)
	{
		if (serializers != null)
		{
			return;
		}
		try
		{
			lock.lock();
			if (serializers != null)
			{
				return;
			}
			
			initializeSerializers(urls);
		}
		finally
		{
			lock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void initializeSerializers(URL[] urls)
	{
		serializers = new HashMap<String, Class<? extends ModuleShareable>>();
		Set<String> serializerNames =  ClassScanner.getInstance(urls).searchClassesByInterface(ModuleShareable.class);
		if (serializerNames != null)
		{
			for (String serializer : serializerNames) 
			{
				try 
				{
					Class<? extends ModuleShareable> serializerClass = (Class<? extends ModuleShareable>) Class.forName(serializer);
					ModuleShareableName annot = serializerClass.getAnnotation(ModuleShareableName.class);
					if (annot != null)
					{
						serializers.put(annot.value(), serializerClass);
					}
					else
					{
						serializers.put(serializerClass.getSimpleName(), serializerClass);
					}
				} 
				catch (Throwable e) 
				{
					logger.error(messages.serializersSerializersInitializeError(e.getLocalizedMessage()),e);
				}
			}
		}
	}

	public static Class<? extends ModuleShareable> getModuleShareable(String name)
	{
		if (serializers == null)
		{
			initialize(ScannerURLS.getURLsForSearch());
		}
		
		if (name == null)
		{
			return null;
		}
		
		return serializers.get(name);
	}
}
