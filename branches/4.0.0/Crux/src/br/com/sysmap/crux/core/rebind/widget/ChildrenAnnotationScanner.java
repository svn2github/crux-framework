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

import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.ViewFactoryUtils;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.declarativeui.LazyWidgets;
import br.com.sysmap.crux.core.declarativeui.LazyWidgets.WidgetLazyChecker;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.LazyPanelFactory.LazyPanelWrappingType;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorAnnotationsProcessor.ChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorAnnotationsProcessor.ChildrenProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AllChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.SequenceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.TextChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.ui.HasText;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class ChildrenAnnotationScanner
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private static final int UNBOUNDED = -1;

	private JClassType allChildProcessorType;
	
	private JClassType anyWidgetChildProcessorType;
	private JClassType choiceChildProcessorType;
	private WidgetCreatorHelper factoryHelper;
	private JClassType hasTextType;
	private LazyPanelFactory lazyFactory;
	private TypeOracle oracle;
	private JClassType sequenceChildProcessorType;
	private JClassType textChildProcessorType;
	private final WidgetCreator<?> widgetCreator;
	
	/**
	 * @param widgetCreator
	 * @param type
	 */
	ChildrenAnnotationScanner(WidgetCreator<?> widgetCreator, JClassType type)
    {
		this.factoryHelper = new WidgetCreatorHelper(type);
		this.widgetCreator = widgetCreator;
		this.lazyFactory = new LazyPanelFactory(widgetCreator.getViewFactory());

		oracle = type.getOracle();
		textChildProcessorType = oracle.findType(TextChildProcessor.class.getCanonicalName());
		choiceChildProcessorType = oracle.findType(ChoiceChildProcessor.class.getCanonicalName());
		sequenceChildProcessorType = oracle.findType(SequenceChildProcessor.class.getCanonicalName());
		allChildProcessorType = oracle.findType(AllChildProcessor.class.getCanonicalName());
		anyWidgetChildProcessorType = oracle.findType(AnyWidgetChildProcessor.class.getCanonicalName());
		hasTextType = oracle.findType(HasText.class.getCanonicalName());
    }

	/**
	 * @return
	 */
	ChildrenProcessor scanChildren()
    {
	    return scanChildren(factoryHelper.getProcessChildrenMethod(), false);
    }

	/**
	 * @param acceptNoChildren
	 * @param isAgregator
	 * @param isAnyWidget
	 * @param widgetProperty
	 * @param lazyChecker
	 * @param processor
	 * @param processorMethod
	 * @return
	 */
	private ChildProcessor createChildProcessor(final boolean acceptNoChildren, final boolean isAgregator, 
																	  final boolean isAnyWidget, final String widgetProperty, 
																	  final WidgetLazyChecker lazyChecker, final WidgetChildProcessor<?> processor, 
																	  final Method processorMethod)
    {
	    return new ChildProcessor()
		{
			public void processChild(SourcePrinter out, WidgetCreatorContext context)
			{
				if (isAnyWidget)
				{
					processAnyWidgetChild(out, context);
				}
				else
				{
					try
					{
						processorMethod.invoke(processor, out, context);
					}
					catch (Exception e)
					{
						throw new CruxGeneratorException(e);//TODO message
					}
					processChildren(out, context);
				}
			}

			/**
			 * @param out
			 * @param context
			 */
			private void processAnyWidgetChild(SourcePrinter out, WidgetCreatorContext context)
            {
				String childWidget; //TODO validar se o lazyCondition esta sendo usado sempre com widgets na factory
				if (lazyChecker != null && lazyChecker.isLazy(context.getWidgetElement()))
				{
					
					childWidget = lazyFactory.getLazyPanel(out, context.getChildElement(), context.getWidgetId(), LazyPanelWrappingType.wrapChildren);
					String lazyPanelId = ViewFactoryUtils.getLazyPanelId(context.getWidgetId(), LazyPanelWrappingType.wrapChildren);
					out.println("Screen.add("+EscapeUtils.quote(lazyPanelId)+", "+childWidget+");");
				}
				else
				{
					childWidget = widgetCreator.createChildWidget(out, context.getChildElement());
				}
				if (StringUtils.isEmpty(widgetProperty))
				{
					out.println(context.getWidget()+".add("+childWidget+");");
				}
				else
				{
					out.println(context.getWidget()+"."+ClassUtils.getSetterMethod(widgetProperty)+"("+childWidget+");");
				}
            }
		};
    }
		
	/**
	 * @param acceptNoChildren
	 * @param childrenProcessor
	 * @param childProcessorClass
	 * @param isAgregator
	 * @param processor
	 * @param processorMethod
	 */
	private void createChildProcessorForMultipleChildrenProcessor(boolean acceptNoChildren, ChildrenProcessor childrenProcessor, 
																  JClassType childProcessorClass, boolean isAgregator,
																  WidgetChildProcessor<?> processor, final Method processorMethod)
    {
	    TagChildAttributes processorAttributes = this.factoryHelper.getChildtrenAttributesAnnotation(childProcessorClass);
	    final String widgetProperty = (processorAttributes!=null?processorAttributes.widgetProperty():"");
	    String tagName = (processorAttributes!=null?processorAttributes.tagName():"");

	    final boolean isAnyWidget = (anyWidgetChildProcessorType.isAssignableFrom(childProcessorClass));

	    TagChildLazyConditions lazyConditions = childProcessorClass.getAnnotation(TagChildLazyConditions.class);
	    final WidgetLazyChecker lazyChecker = (lazyConditions== null?null:LazyWidgets.initializeLazyChecker(lazyConditions));
	    
	    final String childName = getChildTagName(tagName, isAgregator, isAnyWidget);

	    ChildProcessor childProcessor = createChildProcessor(acceptNoChildren, isAgregator, isAnyWidget, 
	    		widgetProperty, lazyChecker, processor, processorMethod);
	    if (!isAnyWidget)
	    {
	    	childProcessor.setChildrenProcessor(scanChildren(factoryHelper.getChildProcessorMethod(childProcessorClass), isAgregator));
	    }

	    childrenProcessor.addChildProcessor(childName, childProcessor);
    }

	/**
	 * @param child
	 * @param acceptNoChildren
	 * @return
	 */
	private ChildrenProcessor createChildProcessorForText(TagChild child, final boolean acceptNoChildren)
    {
		JClassType childProcessor = oracle.findType(child.value().getCanonicalName());
		TagChildAttributes processorAttributes = factoryHelper.getChildtrenAttributesAnnotation(childProcessor);
		final String widgetProperty = processorAttributes.widgetProperty();
		final boolean isHasText = hasTextType.isAssignableFrom(factoryHelper.getWidgetType());
		
	    return new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				String child = WidgetCreator.ensureTextChild(context.getChildElement(), acceptNoChildren);
				if (!StringUtils.isEmpty(child))
				{
					if (!StringUtils.isEmpty(widgetProperty))
					{
						out.println(context.getWidget()+"."+ClassUtils.getSetterMethod(widgetProperty)+"("+EscapeUtils.quote(child)+")");
					}
					else if (isHasText)
					{
						out.println(context.getWidget()+".setText("+EscapeUtils.quote(child)+")");
					}
					else 
					{
						throw new CruxGeneratorException();//TODO reportar o erro
					}
				}
			}
		};
    }

	/**
	 * @param children
	 * @param acceptNoChildren
	 * @param isAgregatorChild
	 * @return
	 */
	private ChildrenProcessor createChildrenProcessorForMultipleChildren(TagChildren children, boolean acceptNoChildren, boolean isAgregatorChild)
    {
		try
		{
			ChildrenProcessor childrenProcessor = doCreateChildrenProcessorForMultipleChildren(acceptNoChildren, isAgregatorChild);

			boolean hasAgregator = false;
			for (TagChild child : children.value())
            {
				JClassType childProcessorClass = oracle.findType(child.value().getCanonicalName());
				final boolean isTextProcessor = textChildProcessorType.isAssignableFrom(childProcessorClass);
				if (isTextProcessor)
				{
					throw new CruxGeneratorException();//TODO message para nao permitir textprocessor com irmaos
				}
				boolean isAgregator = isAgregatorProcessor(childProcessorClass);
				if (isAgregator)
				{
					if (hasAgregator)
					{
						throw new CruxGeneratorException();//TODO reportar que nao pode dois agregators alinhados
					}
					hasAgregator = true;
				}

				WidgetChildProcessor<?> processor;
				processor = child.value().newInstance();
				final Method processorMethod = getChildrenProcessorMethod(child.value());

				createChildProcessorForMultipleChildrenProcessor(acceptNoChildren, childrenProcessor, 
																childProcessorClass, isAgregator, 
																processor, processorMethod);

            }
			return childrenProcessor;
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException();//TODO message
		}
    }
	
	/**
	 * @param child
	 * @param acceptNoChildren
	 * @return
	 */
	private ChildrenProcessor createChildrenProcessorForSingleChild(TagChild child, final boolean acceptNoChildren)
    {
		try
		{
			JClassType childProcessor = oracle.findType(child.value().getCanonicalName());
			final boolean isTextProcessor = textChildProcessorType.isAssignableFrom(childProcessor);
			if (isTextProcessor)
			{
				return createChildProcessorForText(child, acceptNoChildren);
			}

			WidgetChildProcessor<?> processor;
			processor = child.value().newInstance();
			final Method processorMethod = getChildrenProcessorMethod(child.value());

			ChildrenProcessor childrenProcessor = doCreateChildrenProcessorForSingleChild(
					acceptNoChildren, processor, processorMethod, childProcessor);

			return childrenProcessor;
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException();//TODO message
		}
    }

	/**
	 * @param acceptNoChildren
	 * @param isAgregatorChild 
	 * @param processor
	 * @param processorMethod
	 * @param childProcessorClass
	 * @return
	 */
	private ChildrenProcessor doCreateChildrenProcessorForMultipleChildren(final boolean acceptNoChildren, final boolean isAgregatorChild)
    {
		ChildrenProcessor childrenProcessor = new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				String childName;
				if (isAgregatorChild)
				{
					childName = WidgetCreator.getChildName(context.getChildElement());
					processChild(out, context, childName);
				}
				else
				{
					JSONArray children = WidgetCreator.ensureChildren(context.getChildElement(), acceptNoChildren);
					if (children != null)
					{
						for (int i = 0; i < children.length(); i++)
						{
							JSONObject child = children.optJSONObject(i);

							if (widgetCreator.isWidget(child))
							{
								childName = "_innerWidget";
							}
							else
							{
								childName = WidgetCreator.getChildName(child);
							}
							if (!hasChildProcessor(childName))
							{
								childName = "_agregator";
							}
							context.setChildElement(child);
							processChild(out, context, childName);
						}
					}
				}
			}
		};
		
		return childrenProcessor;
    }	
	
	/**
	 * @param acceptNoChildren
	 * @param processor
	 * @param processorMethod
	 * @param childProcessorClass
	 * @return
	 */
	private ChildrenProcessor doCreateChildrenProcessorForSingleChild(final boolean acceptNoChildren, WidgetChildProcessor<?> processor, 
																	  Method processorMethod, JClassType childProcessorClass)
    {
		TagChildAttributes processorAttributes = this.factoryHelper.getChildtrenAttributesAnnotation(childProcessorClass);
		final String widgetProperty = (processorAttributes!=null?processorAttributes.widgetProperty():"");
		String tagName = (processorAttributes!=null?processorAttributes.tagName():"");

		final boolean isAgregator = isAgregatorProcessor(childProcessorClass);
		final boolean isAnyWidget = (anyWidgetChildProcessorType.isAssignableFrom(childProcessorClass));

		TagChildLazyConditions lazyConditions = childProcessorClass.getAnnotation(TagChildLazyConditions.class);
		final WidgetLazyChecker lazyChecker = (lazyConditions== null?null:LazyWidgets.initializeLazyChecker(lazyConditions));
		
		final String childName = getChildTagName(tagName, isAgregator, isAnyWidget);
		
		ChildrenProcessor childrenProcessor = new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				if (isAgregator)
				{
					processChild(out, context, childName);
				}
				else
				{
					JSONObject child = WidgetCreator.ensureFirstChild(context.getChildElement(), acceptNoChildren);
					if (child != null)
					{
						context.setChildElement(child);
						processChild(out, context, childName);
					}
				}
			}
		};
		
		ChildProcessor childProcessor = createChildProcessor(acceptNoChildren, isAgregator, isAnyWidget, 
																		  widgetProperty, lazyChecker, processor, processorMethod);
		if (!isAnyWidget)
		{
			childProcessor.setChildrenProcessor(scanChildren(factoryHelper.getChildProcessorMethod(childProcessorClass), isAgregator));
		}
		
		childrenProcessor.addChildProcessor(childName, childProcessor);
		return childrenProcessor;
    }

	/**
	 * 
	 * @param children
	 * @return
	 */
	private AllowedOccurences getAllowedChildrenNumber(TagChildren children)
	{
		AllowedOccurences allowed = new AllowedOccurences();
		
		for (TagChild child: children.value())
		{
			if (children.value().length > 1 && TextChildProcessor.class.isAssignableFrom(child.value()))
			{
				throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryMixedContentNotAllowed());
			}
			
			AllowedOccurences allowedForChild = getAllowedOccurrencesForChild(child);
			mergeAllowedOccurrences(allowed, allowedForChild);
		}
		return allowed;
	}

	/**
	 * 
	 * @param child
	 * @return
	 */
	private AllowedOccurences getAllowedOccurrencesForChild(TagChild child)
	{
		AllowedOccurences allowed = new AllowedOccurences();
		try
		{
			JClassType childProcessorType = factoryHelper.getFactoryClass().getOracle().getType(child.value().getCanonicalName());
			TagChildAttributes processorAttributes = factoryHelper.getChildtrenAttributesAnnotation(childProcessorType);

			if (processorAttributes != null)
			{
				String minOccurs = processorAttributes.minOccurs();
				if (minOccurs.equals("unbounded"))
				{
					allowed.minOccurs = UNBOUNDED;
				}
				else
				{
					allowed.minOccurs = Integer.parseInt(minOccurs);
				}

				String maxOccurs = processorAttributes.maxOccurs();
				if (maxOccurs.equals("unbounded"))
				{
					allowed.maxOccurs = UNBOUNDED;
				}
				else
				{
					allowed.maxOccurs = Integer.parseInt(maxOccurs);
				}
			}
			else if (AllChildProcessor.class.isAssignableFrom(child.value()) || SequenceChildProcessor.class.isAssignableFrom(child.value()))
			{

				JMethod processorMethod = childProcessorType.getMethod("processChildren", new JType[]{factoryHelper.getContextType()});
				TagChildren tagChildren = processorMethod.getAnnotation(TagChildren.class);
				if (tagChildren != null)
				{
					AllowedOccurences allowedChildren = getAllowedChildrenNumber(tagChildren);
					mergeAllowedOccurrences(allowed, allowedChildren);
				}
			}
			else
			{
				allowed.minOccurs = 1;
				allowed.maxOccurs = 1;
			}
			return allowed;
		}
		catch (NotFoundException e)
		{
			throw new CruxGeneratorException(e.getMessage(), e);
		}
	}

	/**
	 * @param processorClass
	 * @return
	 */
	private Method getChildrenProcessorMethod(Class<?> processorClass)
    {
	    Method[] methods = processorClass.getMethods();
		for (Method met : methods)
        {
	        if (met.getName().equals("processChildren") && met.getParameterTypes().length == 2)//TODO validar tipo dos parametros
	        {
	        	return met;
	        }
        }
		return null;
    }

	
	
	/**
	 * @param tagName
	 * @param isAgregator
	 * @param isAnyWidget
	 * @return
	 */
	private String getChildTagName(String tagName, final boolean isAgregator, final boolean isAnyWidget)
    {
	    final String childName;
	    if (isAnyWidget)
	    {
	    	childName = "_innerWidget";
	    }
	    else if (isAgregator)
	    {
	    	childName = "_agregator";
	    }
	    else
	    {
	    	childName = tagName;
	    }
		if (StringUtils.isEmpty(childName))
		{
			throw new CruxGeneratorException();//TODO message.
		}
	    return childName;
    }
	
	/**
	 * @param childProcessorClass
	 * @return
	 */
	private boolean isAgregatorProcessor(JClassType childProcessorClass)
    {
	    return (choiceChildProcessorType.isAssignableFrom(childProcessorClass) ||
				sequenceChildProcessorType.isAssignableFrom(childProcessorClass) ||
				allChildProcessorType.isAssignableFrom(childProcessorClass));
    }	
	
	/**
	 * @param allowed
	 * @param allowedForChild
	 */
	private void mergeAllowedOccurrences(AllowedOccurences allowed,
            AllowedOccurences allowedForChild)
    {
	    if (allowedForChild.minOccurs == UNBOUNDED)
	    {
	    	allowed.minOccurs = UNBOUNDED;
	    }
	    else if (allowed.minOccurs != UNBOUNDED)
	    {
	    	allowed.minOccurs += allowedForChild.minOccurs;	
	    }
	    if (allowedForChild.maxOccurs == UNBOUNDED)
	    {
	    	allowed.maxOccurs = UNBOUNDED;
	    }
	    else if (allowed.maxOccurs != UNBOUNDED)
	    {
	    	allowed.maxOccurs += allowedForChild.maxOccurs;	
	    }
    }
	
	/**
	 * @param processChildrenMethod
	 * @return
	 */
	private ChildrenProcessor scanChildren(JMethod processChildrenMethod, boolean isAgregatorChild)
	{
		TagChildren children = processChildrenMethod.getAnnotation(TagChildren.class);

		if (children != null)
		{
			AllowedOccurences allowedChildren = getAllowedChildrenNumber(children);
			boolean acceptNoChildren = (allowedChildren.minOccurs == 0);
			if (allowedChildren.maxOccurs == 1)
			{
				TagChild child = children.value()[0];
				return createChildrenProcessorForSingleChild(child, acceptNoChildren);
			}
			else
			{
				return createChildrenProcessorForMultipleChildren(children, acceptNoChildren, isAgregatorChild);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class AllowedOccurences
	{
		int maxOccurs = 0;
		int minOccurs = 0;
	}	
}
