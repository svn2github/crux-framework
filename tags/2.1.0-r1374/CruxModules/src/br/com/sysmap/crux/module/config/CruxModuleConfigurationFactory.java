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
package br.com.sysmap.crux.module.config;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import br.com.sysmap.crux.core.config.AbstractPropertiesFactory;
import br.com.sysmap.crux.core.config.ConstantsInvocationHandler;
import br.com.sysmap.crux.core.i18n.MessageException;

/**
 * Factory for configuration parameters. Receive an Interface and use it's name to look for 
 * resource bundles in the classpath. Each interface's method is used as key 
 * in that property file.
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gess� S. F. Daf� <code>gessedafe@gmail.com</code>
 *
 */
public class CruxModuleConfigurationFactory extends AbstractPropertiesFactory
{
	protected static final CruxModuleConfigurationFactory instance = new CruxModuleConfigurationFactory();
	
	private CruxModuleConfigurationFactory() 
	{
	}
	
	/**
	 * Create a message helper class that access a resource bundle using the methods present in the given interface.
	 * @param targetInterface
	 * @return
	 * @throws MessageException
	 */
	public static CruxModuleConfig getConfigurations() throws MessageException
	{
		return instance.getConstantsFromProperties(CruxModuleConfig.class);
	}

	@Override
	protected ConstantsInvocationHandler getInvocationHandler(Class<?> targetInterface) 
	{
		return new ConfigurationInvocationHandler(targetInterface);
	}

}

/**
 * Dynamic proxy for message resources.
 * @author Thiago da Rosa de Bustamante <code>tr_bustamante@yahoo.com.br</code>
 * @author Gess� S. F. Daf� <code>gessedafe@gmail.com</code>
 */
class ConfigurationInvocationHandler extends ConstantsInvocationHandler
{
	public ConfigurationInvocationHandler(Class<?> targetInterface) 
	{
		super(targetInterface);
	}
	
	@Override
	protected <T> PropertyResourceBundle getPropertiesForLocale(final Class<T> targetInterface) 
	{
		return loadProperties(targetInterface, Locale.getDefault());
	}
}