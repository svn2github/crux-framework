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

import org.cruxframework.crux.core.client.ioc.InjectionProvider;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocConfigClass<T> implements IocConfig
{
	private final Class<T> boundClass;
	private Class<? extends InjectionProvider<? extends T>> providerClass = null;
	private Class<? extends T> toClass;
	private IocScope scope = new IocScope.LocalScope();
	
	public IocConfigClass(Class<T> clazz)
    {
		this.boundClass = clazz;
    }

	public IocConfigClass<T> toProvider(Class<? extends InjectionProvider<? extends T>> providerClass)
	{
		if (this.toClass != null)
		{
			throw new IoCException("Invalid Ioc configuration. Class "+boundClass.getCanonicalName()+" is already bound to target class "+ toClass.getCanonicalName());//TODO message.
		}
		this.providerClass = providerClass;
		return this;
	}
	
	public IocConfigClass<T> toClass(Class<? extends T> toClass)
	{
		if (this.providerClass != null)
		{
			throw new IoCException("Invalid Ioc configuration. Class "+boundClass.getCanonicalName()+" is already bound to target provider "+ providerClass.getCanonicalName());//TODO message.
		}
		this.toClass = toClass;
		return this;
	}

	public void inLocalScope()
	{
		this.scope = new IocScope.LocalScope();
	}
	
	public void inDocumentScope()
	{
		this.scope = new IocScope.DocumentScope();
	}
	
	public IocShareableScope inScreenScope()
	{
		this.scope = new IocScreenScope();
		return (IocShareableScope) this.scope;
	}
	
	public IocShareableScope inUserScope(String scopeName)
	{
		this.scope = new IocUserScope(scopeName);
		return (IocShareableScope) this.scope;
	}

	Class<T> getBoundClass()
    {
    	return boundClass;
    }

	Class<? extends InjectionProvider<? extends T>> getProviderClass()
    {
    	return providerClass;
    }

	Class<? extends T> getToClass()
    {
    	return toClass;
    }

	IocScope getScope()
    {
    	return scope;
    }
}
