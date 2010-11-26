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

import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.WidgetFactory.WidgetFactoryContext;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessorContext;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetFactoryHelper 
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	private final JClassType factoryClass;
	private final JClassType widgetChildProcessorContextType;
	private final JClassType widgetFactoryContextType;
	private final JClassType widgetType;

	/**
	 * @param factoryClass
	 */
	public WidgetFactoryHelper(JClassType factoryClass)
    {
		this.factoryClass = factoryClass;
		this.widgetType = getWidgetTypeFromClass();
		this.widgetFactoryContextType = factoryClass.getOracle().findType(WidgetFactoryContext.class.getCanonicalName());
		this.widgetChildProcessorContextType = factoryClass.getOracle().findType(WidgetChildProcessorContext.class.getCanonicalName());
    }

	/**
	 * @return
	 */
	public JClassType getFactoryClass()
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
	public JClassType getWidgetType()
    {
    	return widgetType;
    }
	
	/**
	 * @param childProcessor
	 * @return
	 */
	public TagChildren getChildrenAnnotationFromProcessor(JClassType childProcessor)
	{
		JMethod processorMethod = getChildProcessorMethod(childProcessor);
		return processorMethod.getAnnotation(TagChildren.class);
	}

	/**
	 * @param childProcessor
	 * @return
	 */
	public JMethod getChildProcessorMethod(JClassType childProcessor)
    {
	    JMethod processorMethod = ClassUtils.getMethod(childProcessor, "processChildren", new JType[]{widgetChildProcessorContextType});
	    return processorMethod;
    }

	/**
	 * @return
	 */
	public JMethod getProcessChildrenMethod()
    {
		JMethod processorMethod = ClassUtils.getMethod(getFactoryClass(), "processChildren", new JType[]{widgetFactoryContextType});
	    return processorMethod;
    }
	
	/**
	 * 
	 * @param logger
	 * @param factoryClass
	 * @return
	 */
	private JClassType getWidgetTypeFromClass()
	{
		JClassType cruxMetaDataType = factoryClass.getOracle().findType(CruxMetaDataElement.class.getCanonicalName());
		JClassType stringType = factoryClass.getOracle().findType(String.class.getCanonicalName());
		
		JType returnType = ClassUtils.getReturnTypeFromMethodClass(factoryClass, "instantiateWidget", new JType[]{cruxMetaDataType, stringType});
		if (returnType == null)
		{
			throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryCanNotRealizeGenericType(factoryClass.getName()));
		}
		return (JClassType) returnType;
	}	
}
