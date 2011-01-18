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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.ViewFactoryUtils;
import br.com.sysmap.crux.core.client.screen.children.AllChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.SequenceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.TextChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.declarativeui.LazyWidgets;
import br.com.sysmap.crux.core.declarativeui.LazyWidgets.WidgetLazyChecker;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.widget.LazyPanelFactory.LazyPanelWrappingType;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorAnnotationsProcessor.ChildrenProcessor;
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
	private static final int UNBOUNDED = -1;
	protected static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	private final WidgetCreator<?> widgetCreator;
	
	private WidgetCreatorHelper factoryHelper;
	private JClassType textChildProcessorType;
	private JClassType choiceChildProcessorType;
	private JClassType sequenceChildProcessorType;
	private JClassType allChildProcessorType;
	private JClassType anyWidgetChildProcessorType;
	private JClassType hasTextType;
	private TypeOracle oracle;
	private LazyPanelFactory lazyFactory;
	
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

	ChildrenProcessor scanChildren()
    {
	    ChildrenProcessor rootProcessor = new ChildrenProcessor()
		{
			@Override
			void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				processChild(out, context, childName);
			}
		};
	    
	    
	    
	    
	    
	    
	    scanChildren(factoryHelper.getProcessChildrenMethod(), rootProcessor);
    }

	/**
	 * @param processChildrenMethod
	 * @return
	 */
	private ChildrenProcessor scanChildren(JMethod processChildrenMethod)
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
				return createChildrenProcessorForMultipleChildren(children, acceptNoChildren);
			}
		}
		return null;
	}
	
	
	private ChildrenProcessor createChildrenProcessorForMultipleChildren(TagChildren children, boolean acceptNoChildren)
    {
	    // TODO Auto-generated method stub
	    return null;
    }

	/**
	 * @param child
	 * @param acceptNoChildren
	 * @return
	 */
	private ChildrenProcessor createChildrenProcessorForSingleChild(TagChild child, final boolean acceptNoChildren)
    {
		JClassType childProcessor = oracle.findType(child.value().getCanonicalName());
		final boolean isTextProcessor = textChildProcessorType.isAssignableFrom(childProcessor);
		if (isTextProcessor)
		{
			return createChildrenProcessorForText(child, acceptNoChildren);
		}
		final boolean isAgregator = (choiceChildProcessorType.isAssignableFrom(childProcessor) ||
				sequenceChildProcessorType.isAssignableFrom(childProcessor) ||
				allChildProcessorType.isAssignableFrom(childProcessor));
		
		final boolean isAnyWidget = (anyWidgetChildProcessorType.isAssignableFrom(childProcessor));
		
		TagChildAttributes processorAttributes = this.factoryHelper.getChildtrenAttributesAnnotation(childProcessor);
		final String widgetProperty = processorAttributes.widgetProperty();
		String tagName = processorAttributes.tagName();
		
		TagChildLazyConditions lazyConditions = childProcessor.getAnnotation(TagChildLazyConditions.class);
		final WidgetLazyChecker lazyChecker = (lazyConditions== null?null:LazyWidgets.initializeLazyChecker(lazyConditions));
		
	    ChildrenProcessor childrenProcessor = doCreateChildrenProcessorForSingleChild(acceptNoChildren, isAgregator, isAnyWidget, widgetProperty, lazyChecker);
		
	    
	    
	    
	    if (isAgregator)
		{
			childrenProcessor.addChildrenProcessor("_agregator", createGrandchildrenProcessor(child));

			TagChildren tagChildren = factoryHelper.getChildrenAnnotationFromProcessor(childProcessor);
			for (TagChild tagChild : tagChildren.value())
            {
				JClassType grandchildrenProcessor = oracle.findType(tagChild.value().getCanonicalName());
				scanChildren(factoryHelper.getChildProcessorMethod(grandchildrenProcessor));
            }
			
		}
		else if (!isAnyWidget)
		{
			if (StringUtils.isEmpty(tagName))
			{
				throw new CruxGeneratorException();//TODO message
			}
			
			childrenProcessor.addChildrenProcessor(tagName, createGrandchildrenProcessor(child));
		}
		
		return childrenProcessor;
    }

	/**
	 * @param child
	 * @return
	 */
	private ChildrenProcessor createGrandchildrenProcessor(TagChild child)
    {
	    try
	    {
	        final WidgetChildProcessor<?,?> processor = child.value().newInstance();
	        final Method method = getChildrenProcessorMethod(child.value());
	        return new ChildrenProcessor(){
	        	@Override
	        	void processChildren(SourcePrinter out, WidgetCreatorContext context)
	        	{
	        		try
	                {
	                    method.invoke(processor, out, context);
	                }
	                catch (Exception e)
	                {
	                	throw new CruxGeneratorException(e);//TODO message
	                }
	        	}
	        };
	    }
	    catch (Exception e)
	    {
	    	throw new CruxGeneratorException(e);//TODO message
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
	 * @param acceptNoChildren
	 * @param isAgregator
	 * @param isAnyWidget
	 * @param widgetProperty
	 * @param lazyChecker
	 * @return
	 */
	private ChildrenProcessor doCreateChildrenProcessorForSingleChild(final boolean acceptNoChildren, final boolean isAgregator, final boolean isAnyWidget, final String widgetProperty, final WidgetLazyChecker lazyChecker)
    {
	    return new ChildrenProcessor()
		{
			public void processChildren(SourcePrinter out, WidgetCreatorContext context)
			{
				JSONObject child = WidgetCreator.ensureFirstChild(context.getChildElement(), acceptNoChildren);
				if (child != null)
				{
					context.setChildElement(child);

					if (isAnyWidget)
					{
						processAnyWidgetChild(out, context);
					}
					else
					{
						String childName = WidgetCreator.getChildName(child);
						if (StringUtils.isEmpty(childName))
						{
							throw new CruxGeneratorException();//TODO message.
						}
						if (isAgregator)
						{
							processChild(out, context, "_agregator");
						}
						processChild(out, context, childName);
					}
				}
			}

			/**
			 * @param out
			 * @param context
			 */
			private void processAnyWidgetChild(SourcePrinter out, WidgetCreatorContext context)
            {
				String childWidget;
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
	 * @param child
	 * @param acceptNoChildren
	 * @return
	 */
	private ChildrenProcessor createChildrenProcessorForText(TagChild child, final boolean acceptNoChildren)
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
