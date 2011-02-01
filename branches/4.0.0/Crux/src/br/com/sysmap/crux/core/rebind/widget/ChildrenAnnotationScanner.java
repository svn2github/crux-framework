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
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.screen.LazyPanelWrappingType;
import br.com.sysmap.crux.core.client.screen.ViewFactoryUtils;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.declarativeui.LazyWidgets;
import br.com.sysmap.crux.core.declarativeui.LazyWidgets.WidgetLazyChecker;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorAnnotationsProcessor.ChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorAnnotationsProcessor.ChildrenProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AllChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.SequenceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.TextChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.utils.ClassUtils;

import com.google.gwt.user.client.ui.HasText;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class ChildrenAnnotationScanner
{
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private static final int UNBOUNDED = -1;

	private WidgetCreatorHelper factoryHelper;
	private LazyPanelFactory lazyFactory;
	private Map<String, ChildrenProcessor> scannedProcessors;
	
	private final WidgetCreator<?> widgetCreator;

	/**
	 * @param widgetCreator
	 * @param type
	 */
	ChildrenAnnotationScanner(WidgetCreator<?> widgetCreator, Class<?> type)
    {
		this.factoryHelper = new WidgetCreatorHelper(type);
		this.widgetCreator = widgetCreator;
		this.lazyFactory = new LazyPanelFactory(widgetCreator.getViewFactory());
    }

	/**
	 * @return
	 */
	ChildrenProcessor scanChildren()
    {
		scannedProcessors = new HashMap<String, WidgetCreatorAnnotationsProcessor.ChildrenProcessor>();
		return scanChildren(factoryHelper.getFactoryClass(), false);
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
																  Class<?> childProcessorClass, boolean isAgregator,
																  WidgetChildProcessor<?> processor, final Method processorMethod)
    {
	    TagChildAttributes processorAttributes = this.factoryHelper.getChildtrenAttributesAnnotation(childProcessorClass);
	    final String widgetProperty = (processorAttributes!=null?processorAttributes.widgetProperty():"");
	    String tagName = (processorAttributes!=null?processorAttributes.tagName():"");

	    final boolean isAnyWidget = (AnyWidgetChildProcessor.class.isAssignableFrom(childProcessorClass));
	    final boolean isAnyWidgetType = (processorAttributes!=null && (AnyWidget.class.isAssignableFrom(processorAttributes.type()) ||
	    															   WidgetCreator.class.isAssignableFrom(processorAttributes.type())));
	    
	    TagChildLazyConditions lazyConditions = childProcessorClass.getAnnotation(TagChildLazyConditions.class);
	    final WidgetLazyChecker lazyChecker = (lazyConditions== null?null:LazyWidgets.initializeLazyChecker(lazyConditions));
	    
	    final String childName = getChildTagName(tagName, isAgregator, (isAnyWidget || isAnyWidgetType));

	    ChildProcessor childProcessor = createChildProcessor(acceptNoChildren, isAgregator, isAnyWidget, 
	    		widgetProperty, lazyChecker, processor, processorMethod);
	    if (!isAnyWidget && !isAnyWidgetType)
	    {
	    	childProcessor.setChildrenProcessor(scanChildren(childProcessorClass, isAgregator));
	    }

	    childrenProcessor.addChildProcessor(childName, childProcessor);
    }

	/**
	 * @param processorClass 
	 * @param child
	 * @param acceptNoChildren
	 * @return
	 */
	private ChildrenProcessor createChildProcessorForText(Class<?> processorClass, TagChild child, final boolean acceptNoChildren)
    {
		Class<?> childProcessor = child.value();
		TagChildAttributes processorAttributes = factoryHelper.getChildtrenAttributesAnnotation(childProcessor);
		final String widgetProperty = processorAttributes.widgetProperty();
		final boolean isHasText = HasText.class.isAssignableFrom(factoryHelper.getWidgetType());
		
	    ChildrenProcessor childrenProcessor = new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				String child = WidgetCreator.ensureTextChild(context.getChildElement(), acceptNoChildren);
				if (!StringUtils.isEmpty(child))
				{
					if (!StringUtils.isEmpty(widgetProperty))
					{
						out.println(context.getWidget()+"."+ClassUtils.getSetterMethod(widgetProperty)+"("+EscapeUtils.quote(child)+");");
					}
					else if (isHasText)
					{
						out.println(context.getWidget()+".setText("+EscapeUtils.quote(child)+");");
					}
					else 
					{
						throw new CruxGeneratorException();//TODO reportar o erro
					}
				}
			}
		};
		
		scannedProcessors.put(processorClass.getCanonicalName(), childrenProcessor);
		return childrenProcessor;
    }
	
	/**
	 * @param processorClass 
	 * @param children
	 * @param acceptNoChildren
	 * @param isAgregatorChild
	 * @return
	 */
	private ChildrenProcessor createChildrenProcessorForMultipleChildren(Class<?> processorClass, TagChildren children, boolean acceptNoChildren, boolean isAgregatorChild)
    {
		try
		{
			ChildrenProcessor childrenProcessor = doCreateChildrenProcessorForMultipleChildren(processorClass, acceptNoChildren, isAgregatorChild);

			boolean hasAgregator = false;
			for (TagChild child : children.value())
            {
				if (child.autoProcess())
				{
					Class<?> childProcessorClass = child.value();
					final boolean isTextProcessor = TextChildProcessor.class.isAssignableFrom(childProcessorClass);
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
					processor.setWidgetCreator(widgetCreator);

					final Method processorMethod = factoryHelper.getChildProcessorMethod(child.value());

					createChildProcessorForMultipleChildrenProcessor(acceptNoChildren, childrenProcessor, 
							childProcessorClass, isAgregator, 
							processor, processorMethod);
				}
            }
			return childrenProcessor;
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException(e);//TODO message
		}
    }

	/**
	 * @param processorClass 
	 * @param child
	 * @param acceptNoChildren
	 * @return
	 */
	private ChildrenProcessor createChildrenProcessorForSingleChild(Class<?> processorClass, TagChild child, final boolean acceptNoChildren)
    {
		try
		{
			if (!child.autoProcess())
			{
				return null;
			}
			Class<?> childProcessor = child.value();
			final boolean isTextProcessor = TextChildProcessor.class.isAssignableFrom(childProcessor);
			if (isTextProcessor)
			{
				return createChildProcessorForText(processorClass, child, acceptNoChildren);
			}

			WidgetChildProcessor<?> processor;
			processor = child.value().newInstance();
			processor.setWidgetCreator(widgetCreator);

			final Method processorMethod = factoryHelper.getChildProcessorMethod(child.value());

			ChildrenProcessor childrenProcessor = doCreateChildrenProcessorForSingleChild(processorClass, 
					acceptNoChildren, processor, processorMethod, childProcessor);

			return childrenProcessor;
		}
		catch (Exception e)
		{
			throw new CruxGeneratorException(e);//TODO message
		}
    }	
	
	/**
	 * @param processorClass 
	 * @param acceptNoChildren
	 * @param isAgregatorChild 
	 * @param processor
	 * @param processorMethod
	 * @param childProcessorClass
	 * @return
	 */
	private ChildrenProcessor doCreateChildrenProcessorForMultipleChildren(Class<?> processorClass, final boolean acceptNoChildren, final boolean isAgregatorChild)
    {
		ChildrenProcessor childrenProcessor = new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				String childName;
				if (isAgregatorChild)
				{
					childName = getChildName(context.getChildElement());
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

							childName = getChildName(child);
							context.setChildElement(child);
							processChild(out, context, childName);
						}
					}
				}
			}

			private String getChildName(JSONObject child)
            {
	            String childName;
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
	            return childName;
            }
		};
		scannedProcessors.put(processorClass.getCanonicalName(), childrenProcessor);

		return childrenProcessor;
    }

	/**
	 * @param processorClass 
	 * @param acceptNoChildren
	 * @param processor
	 * @param processorMethod
	 * @param childProcessorClass
	 * @return
	 */
	private ChildrenProcessor doCreateChildrenProcessorForSingleChild(Class<?> processorClass, final boolean acceptNoChildren, WidgetChildProcessor<?> processor, 
																	  Method processorMethod, Class<?> childProcessorClass)
    {
		TagChildAttributes processorAttributes = this.factoryHelper.getChildtrenAttributesAnnotation(childProcessorClass);
		final String widgetProperty = (processorAttributes!=null?processorAttributes.widgetProperty():"");
		String tagName = (processorAttributes!=null?processorAttributes.tagName():"");

		final boolean isAgregator = isAgregatorProcessor(childProcessorClass);
		final boolean isAnyWidget = (AnyWidgetChildProcessor.class.isAssignableFrom(childProcessorClass));
	    final boolean isAnyWidgetType = (processorAttributes!=null && (AnyWidget.class.isAssignableFrom(processorAttributes.type()) ||
				   WidgetCreator.class.isAssignableFrom(processorAttributes.type())));

		TagChildLazyConditions lazyConditions = childProcessorClass.getAnnotation(TagChildLazyConditions.class);
		final WidgetLazyChecker lazyChecker = (lazyConditions== null?null:LazyWidgets.initializeLazyChecker(lazyConditions));
		
		final String childName = getChildTagName(tagName, isAgregator, (isAnyWidget || isAnyWidgetType));
		
		ChildrenProcessor childrenProcessor = new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
/*				if (isAgregator)
				{
					processChild(out, context, childName);
				}
				else
				{*/
					JSONObject child = WidgetCreator.ensureFirstChild(context.getChildElement(), acceptNoChildren);
					if (child != null)
					{
						context.setChildElement(child);
						processChild(out, context, childName);
					}
//				}
			}
		};
		scannedProcessors.put(processorClass.getCanonicalName(), childrenProcessor);
		
		ChildProcessor childProcessor = createChildProcessor(acceptNoChildren, isAgregator, isAnyWidget, //TODO remover variaveis nao usadas neste metodo
																		  widgetProperty, lazyChecker, processor, processorMethod);
		if (!isAnyWidget && !isAnyWidgetType)
		{
			childProcessor.setChildrenProcessor(scanChildren(childProcessorClass, isAgregator));
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
			Class<?> childProcessorType = child.value();
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
				Method processorMethod = factoryHelper.getChildProcessorMethod(childProcessorType);
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
		catch (Exception e)
		{
			throw new CruxGeneratorException(e.getMessage(), e);
		}
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
	private boolean isAgregatorProcessor(Class<?> childProcessorClass)
    {
	    return (ChoiceChildProcessor.class.isAssignableFrom(childProcessorClass) ||
				SequenceChildProcessor.class.isAssignableFrom(childProcessorClass) ||
				AllChildProcessor.class.isAssignableFrom(childProcessorClass));
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
	 * @param children
	 * @return
	 */
	private boolean mustGenerateChildrenProcessMethod(TagChildren children)
	{
		if (children != null)
		{
			for (TagChild child : children.value())
			{
				if (child.autoProcess())
				{
					return true;
				}
			}
		}
		return false;
	} 
	
	
	/**
	 * @param processChildrenMethod
	 * @return
	 */
	private ChildrenProcessor scanChildren(Class<?> processorClass, boolean isAgregatorChild)
	{
		String processorName = processorClass.getCanonicalName();
		if (scannedProcessors.containsKey(processorName))
		{
			return scannedProcessors.get(processorClass.getCanonicalName());
		}
		ChildrenProcessor result = null;
		
		Method processChildrenMethod = factoryHelper.getChildProcessorMethod(processorClass);
		TagChildren children = processChildrenMethod.getAnnotation(TagChildren.class);
		
		if (children != null && mustGenerateChildrenProcessMethod(children))
		{
			AllowedOccurences allowedChildren = getAllowedChildrenNumber(children);
			boolean acceptNoChildren = (allowedChildren.minOccurs == 0);
			if (allowedChildren.maxOccurs == 1)
			{
				TagChild child = children.value()[0];
				result = createChildrenProcessorForSingleChild(processorClass, child, acceptNoChildren);
			}
			else
			{
				result = createChildrenProcessorForMultipleChildren(processorClass, children, acceptNoChildren, isAgregatorChild);
			}
		}
		return result;
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
