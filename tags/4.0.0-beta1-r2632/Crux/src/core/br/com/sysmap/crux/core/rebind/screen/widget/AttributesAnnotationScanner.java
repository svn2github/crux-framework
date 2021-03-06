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
package br.com.sysmap.crux.core.rebind.screen.widget;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorAnnotationsProcessor.AttributeCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class AttributesAnnotationScanner
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	
	private final WidgetCreator<?> widgetCreator;

	private WidgetCreatorHelper factoryHelper;

	
	AttributesAnnotationScanner(WidgetCreator<?> widgetCreator, Class<?> type)
    {
		this.factoryHelper = new WidgetCreatorHelper(type);
		this.widgetCreator = widgetCreator;
    }
	
	/**
	 * @param factoryClass
	 * @throws CruxGeneratorException
	 */
	List<AttributeCreator> scanAttributes() throws CruxGeneratorException
	{
		ArrayList<AttributeCreator> attributes = new ArrayList<AttributeCreator>();
		scanAttributes(factoryHelper.getFactoryClass(), attributes, new HashSet<String>());
		return attributes;
	}
	
	/**
	 * @param factoryClass
	 * @param attributes
	 * @param added
	 * @throws CruxGeneratorException
	 */
	private void scanAttributes(Class<?> factoryClass, List<AttributeCreator> attributes, Set<String> added) throws CruxGeneratorException
	{
		try
        {
			TagAttributes attrs = factoryClass.getAnnotation(TagAttributes.class);
			if (attrs != null)
			{
				for (TagAttribute attr : attrs.value())
				{
					String attrName = attr.value();
					if (!added.contains(attrName))
					{
						added.add(attrName);
						if (isValidName(attrName))
						{
							if (AttributeProcessor.NoParser.class.isAssignableFrom(attr.processor()))
							{
								attributes.add(createAutomaticAttributeProcessor(factoryClass, attr));
							}
							else
							{
								attributes.add(createAttributeProcessorWithParser(attr));
							}
						}
						else
						{
							throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryInvalidAttrName(attrName));
						}
					}
				}
			}
	        Class<?> superclass = factoryClass.getSuperclass();
	        if (superclass!= null && !superclass.equals(Object.class))
	        {
	        	scanAttributes(superclass, attributes, added);
	        }
	        Class<?>[] interfaces = factoryClass.getInterfaces();
	        for (Class<?> interfaceClass : interfaces)
	        {
	        	scanAttributes(interfaceClass, attributes, added);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}	

	/**
	 * @param attr
	 * @return
	 */
	private AttributeCreator createAttributeProcessorWithParser(TagAttribute attr)
    {
		final String attrName = attr.value();
		Class<?> processorClass = attr.processor();
		final Method method = getAtributeProcessorMethod(processorClass);
		final AttributeProcessor<?> processor;
        try
        {
        	processor = (AttributeProcessor<?>) processorClass.getConstructor(new Class<?>[]{WidgetCreator.class}).newInstance(widgetCreator);
        }
        catch (Exception e)
        {
	        throw new CruxGeneratorException(messages.widgetCreatorErrorCreatingAttributeProcessor(), e);
        }
		
		return doCreateAttributeProcessorWithParser(attrName, method, processor);
    }

	/**
	 * @param attrName
	 * @param method
	 * @param processor
	 * @return
	 */
	private AttributeCreator doCreateAttributeProcessorWithParser(final String attrName, final Method method, final AttributeProcessor<?> processor)
    {
	    return new AttributeCreator()
		{
			public void createAttribute(SourcePrinter out, WidgetCreatorContext context)
			{
				String attrValue = context.readWidgetProperty(attrName);
				if (!StringUtils.isEmpty(attrValue))
				{
					try
                    {
	                    method.invoke(processor, out, context, attrValue);
                    }
                    catch (Exception e)
                    {
            	        throw new CruxGeneratorException(messages.widgetCreatorRunningAttributeProcessor(attrName), e);
                    }
				}
			}
		};
    }

	/**
	 * @param processorClass
	 * @return
	 */
	private Method getAtributeProcessorMethod(Class<?> processorClass)
    {
	    try 
	    {
			return processorClass.getMethod("processAttributeInternal", new Class<?>[]{SourcePrinter.class, WidgetCreatorContext.class, String.class});
		}
	    catch (Exception e) 
		{
			return null;
		}
    }

	/**
	 * @param factoryClass
	 * @param attr
	 * @return
	 */
	private AttributeCreator createAutomaticAttributeProcessor(Class<?> factoryClass, TagAttribute attr)
    {
		final String attrName = attr.value();
		final String setterMethod;
		boolean nestedProperty = false;
		if (!StringUtils.isEmpty(attr.property()))
		{
			nestedProperty = attr.property().contains(".");
			if (nestedProperty)
			{
				String[] properties = RegexpPatterns.REGEXP_DOT.split(attr.property());
				StringBuilder expression = new StringBuilder();
				for(int i=0; i< properties.length-1;i++)
				{
					expression.append(ClassUtils.getGetterMethod(properties[i])+"().");
				}
				expression.append(ClassUtils.getSetterMethod(properties[properties.length-1]));
				setterMethod = expression.toString();
			}
			else
			{
				setterMethod = ClassUtils.getSetterMethod(attr.property());
			}
		}
		else
		{
			setterMethod = ClassUtils.getSetterMethod(attrName);
		}
		Class<?> type = attr.type();
		if (type == null ||  !(nestedProperty || ClassUtils.hasValidSetter(factoryHelper.getWidgetType(), setterMethod, type)))
		{//TODO: implement method check for nested property.
			throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryInvalidProperty(attrName));
		}
		final boolean isStringExpression = String.class.isAssignableFrom(type);
		final boolean supportsI18N = isStringExpression && attr.supportsI18N();
		final boolean isEnumExpression = type.isEnum();
		final boolean isPrimitiveExpression = type.isPrimitive();
			
		return doCreateAutomaticAttributeProcessor(attrName, setterMethod, type.getCanonicalName(), 
												   isStringExpression, supportsI18N, isEnumExpression, 
												   isPrimitiveExpression);
    }

	/**
	 * @param attrName
	 * @param setterMethod
	 * @param typeName
	 * @param isStringExpression
	 * @param supportsI18N
	 * @param isEnumExpression
	 * @param isPrimitiveExpression
	 * @return
	 */
	private AttributeCreator doCreateAutomaticAttributeProcessor(final String attrName, final String setterMethod, 
																 final String typeName, final boolean isStringExpression, 
																 final boolean supportsI18N, final boolean isEnumExpression, 
																 final boolean isPrimitiveExpression)
    {
	    return new AttributeCreator()
		{
			public void createAttribute(SourcePrinter out, WidgetCreatorContext context)
			{
					String attrValue = context.readWidgetProperty(attrName);
					String expression = getExpression(typeName, isStringExpression, isEnumExpression, isPrimitiveExpression, attrValue);
					if (expression != null)
					{
						out.println(context.getWidget()+"."+setterMethod+"("+expression+");");
					}
			}

			private String getExpression(String typeName, 
										 boolean isStringExpression, 
										 boolean isEnumExpression, boolean isPrimitiveExpression, 
										 String attrValue)
            {
				String expression;

				if (StringUtils.isEmpty(attrValue))
				{
					expression = null;
				}
				else if (supportsI18N)
				{
					expression = widgetCreator.getDeclaredMessage(attrValue);
				}
				else if (isStringExpression)
				{
					expression = EscapeUtils.quote(attrValue);
				}
				else if (isEnumExpression)
				{
					expression = typeName+".valueOf("+EscapeUtils.quote(attrValue)+")";
				}
				else if (isPrimitiveExpression)
				{
					expression = "("+typeName+")"+attrValue;
				}
				else
				{
					expression = attrValue;
				}
			
				return expression;
            }
		};
    }
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	private boolean isValidName(String name)
	{
		return name != null && name.length() > 0 && RegexpPatterns.REGEXP_WORD.matcher(name).matches() 
		                                         && !Character.isDigit(name.charAt(0));
	}	
}
