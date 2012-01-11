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
package org.cruxframework.crux.core.client.ioc;

import org.cruxframework.crux.core.ioc.IocContainerConfigurations;

import com.google.gwt.core.client.GWT;

/**
 * Crux IoC container accessor. Used to retrieve objects previously configured 
 * through {@link IocContainerConfigurations} class
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocContainer
{
	private static IocContainerHandler containerHandler = null;
	static
	{
		if (GWT.isClient())
		{
			containerHandler = GWT.create(IocContainerHandler.class);
		}
	}
	
	/**
	 * Retrieve from IoC container an object, based on the class provided. 
	 * @param <T>
	 * @param clazz base type for requested object.
	 * @return
	 */
	public static <T> T get(Class<T> clazz)
	{
		return containerHandler.get(clazz);
	}

	static interface IocContainerHandler
	{
		<T> T get(Class<T> clazz);
	}
}

//TODO: criar um generator para gerar os escopos de usuario.... Ele deve criar um subtipo
// paraescrita e leitura no context, via uma interface que herde Context

//TODO: os escopos shareable deve ter metodos commitChanges... para contextos controlados
//pelo container, esse commmitChanges deve ser chamado ao termino do loop de eventos do crux
//
