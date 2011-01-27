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
package br.com.sysmap.crux.core.rebind.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetCreatorHelper 
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	private final Class<?> factoryClass;
	private final Class<?> widgetType;
	private final Class<?> contextType;

	/**
	 * @param factoryClass
	 */
	public WidgetCreatorHelper(Class<?> factoryClass)
    {
		this.factoryClass = factoryClass;
		this.widgetType = getWidgetTypeFromClass();
		this.contextType = getContextTypeFromClass();
    }

	/**
	 * @return
	 */
	public Class<?> getFactoryClass()
    {
    	return factoryClass;
    }

	public String getWidgetDeclarationType()
	{
		DeclarativeFactory declarativeFactory = factoryClass.getAnnotation(DeclarativeFactory.class);
		if (declarativeFactory != null)
		{
			return declarativeFactory.library()+"_"+declarativeFactory.id();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return
	 */
	public Class<?> getWidgetType()
    {
    	return widgetType;
    }
	
	/**
	 * @return
	 */
	public Class<?> getContextType()
    {
    	return contextType;
    }

	/**
	 * @param childProcessor
	 * @return
	 */
	public TagChildren getChildrenAnnotationFromProcessor(Class<?> childProcessor)
	{
		Method processorMethod = getChildProcessorMethod(childProcessor);
		return processorMethod.getAnnotation(TagChildren.class);
	}

	/**
	 * @param childProcessor
	 * @return
	 */
	public Method getChildProcessorMethod(Class<?> childProcessor)
    {
		Method processorMethod;
        try
        {
	        processorMethod = childProcessor.getMethod("processChildren", new Class<?>[]{SourcePrinter.class, contextType});
        }
        catch (Exception e)
        {
        	processorMethod = null;
        }
	    return processorMethod;
    }

	/**
	 * @return
	 */
	public Method getProcessChildrenMethod()
    {
		Method processorMethod = getChildProcessorMethod(getFactoryClass());
	    return processorMethod;
    }
	
	/**
	 * 
	 * @param processorClass
	 * @return
	 */
	public TagChildAttributes getChildtrenAttributesAnnotation(Class<?> processorClass)
	{
		TagChildAttributes attributes = processorClass.getAnnotation(TagChildAttributes.class);
		if (attributes == null)
		{
			Class<?> superClass = processorClass.getSuperclass();
			if (superClass != null && superClass.getSuperclass() != null)
			{
				attributes = getChildtrenAttributesAnnotation(superClass);
			}
		}
		
		return attributes;
	}	
	
	/**
	 * 
	 * @return
	 */
	private Class<?> getWidgetTypeFromClass()
	{
		DeclarativeFactory declarativeFactory = factoryClass.getAnnotation(DeclarativeFactory.class);
		if (declarativeFactory != null)
		{
			return declarativeFactory.targetWidget();
		}
		else
		{
			return null;
		}
	}	

	/**
	 * 
	 * @return
	 */
	private Class<?> getContextTypeFromClass()
	{
		Field field;
        try
        {
	        field = factoryClass.getField("__contextInstance");
        }
        catch (Exception e)
        {
			throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryCanNotRealizeGenericType(factoryClass.getName()));
        }
		if (field == null)
		{
			throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryCanNotRealizeGenericType(factoryClass.getName()));
		}
		return field.getType();
	}	
}
