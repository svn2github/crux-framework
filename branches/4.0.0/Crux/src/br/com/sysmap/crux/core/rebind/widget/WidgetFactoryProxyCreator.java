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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.sysmap.crux.core.client.collection.Array;
import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyCondition;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.event.bind.EvtBinder;
import br.com.sysmap.crux.core.client.screen.DeclarativeWidgetFactory;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.LazyPanelFactory;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.WidgetFactory;
import br.com.sysmap.crux.core.client.screen.LazyPanelFactory.LazyPanelWrappingType;
import br.com.sysmap.crux.core.client.screen.children.AllChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.AnyWidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.SequenceChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.TextChildProcessor;
import br.com.sysmap.crux.core.client.screen.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.server.Environment;
import br.com.sysmap.crux.core.utils.ClassUtils;
import br.com.sysmap.crux.core.utils.RegexpPatterns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetFactoryProxyCreator extends AbstractInterfaceWrapperProxyCreator
{

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
	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	private static class EventBinderData
	{
		String evtBinderCalls;
		Map<String, String> evtBinderVariables;

		public EventBinderData(String evtBinderCalls, Map<String, String> evtBinderVariables)
		{
			this.evtBinderCalls = evtBinderCalls;
			this.evtBinderVariables = evtBinderVariables;
		}
	}
	private static final int UNBOUNDED = -1;
	private Map<String, String> attributesFromClass = new HashMap<String, String>();
	private Map<String, EventBinderData> eventsFromClass = new HashMap<String, EventBinderData>();
	private int variableNameSuffixCounter = 0;
	private final WidgetFactoryHelper factoryHelper;
	private Set<String> widgetProperties = new HashSet<String>();
	
	/**
	 * @param logger
	 * @param context
	 * @param factoryClass
	 */
	public WidgetFactoryProxyCreator(TreeLogger logger, GeneratorContext context, JClassType factoryClass)
	{
		super(logger, context, factoryClass);
		this.factoryHelper = new WidgetFactoryHelper(factoryClass);
		if (Environment.isProduction())
		{
			initializeWidgetPropertiesMap();
		}
	}

	/**
	 * 
	 */
	private void initializeWidgetPropertiesMap() 
	{
		List<br.com.sysmap.crux.core.rebind.scanner.screen.Screen> screens = getScreens();
		
		String widgetType = factoryHelper.getWidgetDeclarationType();
		
		for (br.com.sysmap.crux.core.rebind.scanner.screen.Screen screen : screens) 
		{
			Set<String> widgetPropertiesFromScreen = screen.getWidgetProperties(widgetType);
			if (widgetPropertiesFromScreen != null)
			{
				widgetProperties.addAll(widgetPropertiesFromScreen);
			}
		}
	}

	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	@Override
    protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
    {
		generateProcessAttributesMethod(srcWriter);
		generateProcessEventsMethod(srcWriter);
		generateProcessChildrenMethod(srcWriter);
		generateIsAttachMethod(srcWriter);
		generateIsPanelMethod(srcWriter);
		generateIsHtmlContainerMethod(srcWriter);
    }
	
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @return
	 */
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		ScreenFactory.class.getCanonicalName(),
    		Screen.class.getCanonicalName(),
    		CruxMetaDataElement.class.getCanonicalName(),
    		Array.class.getCanonicalName(),
    		FastList.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		InterfaceConfigException.class.getCanonicalName(),
    		StringUtils.class.getCanonicalName()
		};
	    return imports;
    }	
	
	@Override
    protected SourceWriter getSourceWriter()
    {
		JPackage pkg = factoryHelper.getFactoryClass().getPackage();
		String packageName = pkg == null ? "" : pkg.getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		composerFactory.setSuperclass(factoryHelper.getFactoryClass().getParameterizedQualifiedSourceName());
		composerFactory.addImplementedInterface(DeclarativeWidgetFactory.class.getCanonicalName());
		return composerFactory.createSourceWriter(context, printWriter);
    }
	
	/**
	 * @param methodsForInnerProcessing
	 * @param children
	 * @param childProcessor
	 * @param processorVariables
	 * @param first
	 * @return
	 */
	private String generateAgregatorTagProcessingBlock(Map<String, String> methodsForInnerProcessing, 
													   TagChildren children,
													   JClassType childProcessor, 
													   Map<String, String> processorVariables, boolean first)
	{
		StringBuilder source = new StringBuilder();
		
		if (children != null)
		{
			TagChildren tagChildren = factoryHelper.getChildrenAnnotationFromProcessor(childProcessor);
			source.append(generateChildrenBlockFromAnnotation(methodsForInnerProcessing, tagChildren, processorVariables, first));
		}
		return source.toString();
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param childProcessor
	 */
	private String generateAnyWidgetProcessingBlock(JClassType childProcessor)
	{
		StringBuilder source = new StringBuilder();
		TagChildAttributes processorAttributes = getChildtrenAttributesAnnotation(childProcessor);
		
		TagChildLazyConditions lazyConditions = childProcessor.getAnnotation(TagChildLazyConditions.class);
		if (lazyConditions == null)
		{
			source.append(Widget.class.getName()+" _w = createChildWidget(c.getChildElement());\n");
		}
		else
		{
			source.append(generateChildCreationForLazyConditions(lazyConditions));
		}
		if (processorAttributes != null && processorAttributes.widgetProperty().length() > 0)
		{
			source.append("c.getRootWidget()."+ClassUtils.getSetterMethod(processorAttributes.widgetProperty())+"(_w);\n");						
		}
		else
		{
			source.append("c.getRootWidget().add(_w);\n");						
		}
		
		return source.toString();
	}

	/**
	 * @param lazyConditions
	 * @return
	 */
	private String generateChildCreationForLazyConditions(TagChildLazyConditions lazyConditions)
    {
		StringBuilder source = new StringBuilder();
		if (lazyConditions.all().length > 0)
		{
            source.append("boolean _lazy = true;\n");
			for (TagChildLazyCondition lazyCondition : lazyConditions.all())
            {
				String varName = "prop_"+getVariableNameSuffixCounter();
	            source.append("String "+varName+" = c.readRootWidgetProperty(\""+lazyCondition.property()+"\");\n");
	            if (lazyCondition.equals().length() >0)
	            {
	            	 source.append("_lazy = _lazy && "+varName+"!=null && "+varName+".equals("+EscapeUtils.quote(lazyCondition.equals())+");\n"); 
	            }
	            else if (lazyCondition.notEquals().length() >0)
	            {
	            	 source.append("_lazy = _lazy && ("+varName+"==null || !"+varName+".equals("+EscapeUtils.quote(lazyCondition.notEquals())+"));\n"); 
	            }
            }
		}
		else if (lazyConditions.any().length > 0)
		{
            source.append("boolean _lazy = false;\n");
			for (TagChildLazyCondition lazyCondition : lazyConditions.any())
            {
				String varName = "prop_"+getVariableNameSuffixCounter();
	            source.append("String "+varName+" = c.readRootWidgetProperty(\""+lazyCondition.property()+"\")\n");
	            if (lazyCondition.equals().length() >0)
	            {
	            	 source.append("_lazy = lazy || ("+varName+"!=null && "+varName+".equals("+EscapeUtils.quote(lazyCondition.equals())+"));\n"); 
	            }
	            else if (lazyCondition.notEquals().length() >0)
	            {
	            	 source.append("_lazy = lazy || ("+varName+"==null || !"+varName+".equals("+EscapeUtils.quote(lazyCondition.equals())+"));\n"); 
	            }
            }
		}
		else
		{
			throw new CruxGeneratorException(messages.errorGeneratingWidgetFactoryInvalidLazyCondition());
		}
		source.append(Widget.class.getName()+" _w;\n");
		source.append("if (!_lazy){\n");
		source.append("_w = createChildWidget(c.getChildElement());\n");
		source.append("} else {\n");
		source.append(CruxMetaDataElement.class.getSimpleName()+" _elem = c.getChildElement();\n");
		source.append("String _lazyPanelId = c.getRootWidgetId();\n");
		source.append("_w = "+LazyPanelFactory.class.getCanonicalName()+".getLazyPanel(_elem, _lazyPanelId, "+ LazyPanelWrappingType.class.getCanonicalName()+".wrapChildren);\n");
		source.append("Screen.add("+LazyPanelFactory.class.getCanonicalName()+".getLazyPanelId(_lazyPanelId, "+ LazyPanelWrappingType.class.getCanonicalName()+".wrapChildren), _w);\n");
		source.append("}\n");
		 
		return source.toString();
    }

	/**
	 * @param methodsForInnerProcessing
	 * @param children
	 * @param processorVariables
	 * @param first
	 * @return
	 */
	private String generateChildrenBlockFromAnnotation(Map<String, String> methodsForInnerProcessing, 
			                                         TagChildren children,
			                                         Map<String, String> processorVariables, boolean first)
	{
		try
        {
	        StringBuilder source = new StringBuilder();
	        TypeOracle oracle = factoryHelper.getFactoryClass().getOracle();
	        for (TagChild child : children.value())
	        {
	        	if (child.autoProcess())
	        	{
					JClassType childProcessor = oracle.getType(child.value().getCanonicalName());
	        		JClassType textChildProcessorType = oracle.getType(TextChildProcessor.class.getCanonicalName());
	        		JClassType choiceChildProcessorType = oracle.getType(ChoiceChildProcessor.class.getCanonicalName());
	        		JClassType sequenceChildProcessorType = oracle.getType(SequenceChildProcessor.class.getCanonicalName());
	        		JClassType allChildProcessorType = oracle.getType(AllChildProcessor.class.getCanonicalName());
	        		JClassType anyWidgetChildProcessorType = oracle.getType(AnyWidgetChildProcessor.class.getCanonicalName());
	        		
	        		if (textChildProcessorType.isAssignableFrom(childProcessor))
	        		{
	        			source.append(generateTextProcessingBlock(childProcessor));
	        		}
	        		else if (choiceChildProcessorType.isAssignableFrom(childProcessor) ||
	        				sequenceChildProcessorType.isAssignableFrom(childProcessor) ||
	        				allChildProcessorType.isAssignableFrom(childProcessor))
	        		{
	        			source.append(generateAgregatorTagProcessingBlock(methodsForInnerProcessing, 
	        					                                          children, childProcessor, processorVariables, first));
	        		}
	        		else
	        		{
	        			source.append(generateTagIdentifierBlock(first, childProcessor));
	        			if (anyWidgetChildProcessorType.isAssignableFrom(childProcessor))
	        			{
	        				source.append(generateAnyWidgetProcessingBlock(childProcessor));
	        			}
	        			else
	        			{
	        				source.append(generateGenericProcessingBlock(methodsForInnerProcessing, 
	        						                                                  childProcessor, processorVariables));
	        			}
	        			source.append("}\n");
	        		}
	        		first = false;
	        	}
	        }
	        return source.toString();
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}
	
	/**
	 * @param methodsForInnerProcessing
	 * @param childProcessor
	 * @param processorVariables
	 * @return
	 */
	private String generateGenericProcessingBlock(Map<String, String> methodsForInnerProcessing, 
												  JClassType childProcessor, 
												  Map<String, String> processorVariables)
	{
		// TODO - Thiago - colocar todos as amarracoes de eventos do client em evtbinder 
		StringBuilder source = new StringBuilder();
		
		String processorName = childProcessor.getParameterizedQualifiedSourceName();
		String evtBinderVar = getEvtBinderVariableName("p", processorVariables, processorName);
		source.append(evtBinderVar+".processChildren(c);\n");
		
		JMethod processorMethod = factoryHelper.getChildProcessorMethod(childProcessor);
		String childrenProcessorMethodName = generateProcessChildrenBlockFromMethod(methodsForInnerProcessing, 
				                                            processorMethod, processorVariables);
		if (childrenProcessorMethodName != null)
		{
			source.append(childrenProcessorMethodName+"(c);\n");
		}
		return source.toString();
	}

	/**
	 * 
	 * @param sourceWriter
	 * @param processorVariables
	 */
	private void generateInnerProcessorsvariables(SourceWriter sourceWriter, Map<String, String> processorVariables)
	{
		for (String processorVar : processorVariables.keySet())
		{
			String binderClass = processorVariables.get(processorVar);
			sourceWriter.println(binderClass + " " + processorVar + "= new " + binderClass + "();");
		}
	}
	
	/**
	 * @param sourceWriter
	 */
	private void generateIsHtmlContainerMethod(SourceWriter sourceWriter)
	{
		DeclarativeFactory declarativeFactory = factoryHelper.getFactoryClass().getAnnotation(DeclarativeFactory.class);
		
		sourceWriter.println("public boolean isHtmlContainer(){"); 
		sourceWriter.indent();
		sourceWriter.println("return "+(declarativeFactory==null?false:declarativeFactory.htmlContainer())+";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * @param sourceWriter
	 */
	private void generateIsAttachMethod(SourceWriter sourceWriter)
	{
		DeclarativeFactory declarativeFactory = factoryHelper.getFactoryClass().getAnnotation(DeclarativeFactory.class);
		
		sourceWriter.println("public boolean isAttachToDOM(){"); 
		sourceWriter.indent();
		sourceWriter.println("return "+(declarativeFactory==null?false:declarativeFactory.attachToDOM())+";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	private void generateIsPanelMethod(SourceWriter sourceWriter)
	{
		JClassType panelType = factoryHelper.getFactoryClass().getOracle().findType(Panel.class.getCanonicalName());
		
		sourceWriter.println("public boolean isPanel(){"); 
		sourceWriter.indent();
		sourceWriter.println("return "+(factoryHelper.getWidgetType().isAssignableTo(panelType))+";");
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param contextDeclaration
	 * @param methodsForInnerProcessing
	 */
	private void generateMethodsForInnerProcessingChildren(SourceWriter sourceWriter, Map<String, String> methodsForInnerProcessing)
	{
        String contextDeclaration = factoryHelper.getWidgetChildProcessorContextType().getParameterizedQualifiedSourceName();
		
		for (String methodName : methodsForInnerProcessing.keySet())
		{
			sourceWriter.println("protected void "+ methodName+"("+contextDeclaration+" c) throws InterfaceConfigException{");
			sourceWriter.println(methodsForInnerProcessing.get(methodName));
			sourceWriter.println("}");
		}
	}
	

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param factoryClass
	 */
	private String generateProcessAttributesBlock(JClassType factoryClass)
	{
		try
        {
	        String factoryClassName = factoryClass.getQualifiedSourceName();
			if (attributesFromClass.containsKey(factoryClassName))
	        {
	        	return attributesFromClass.get(factoryClassName);
	        }
	        StringBuilder result = new StringBuilder();
	        
	        JMethod method = factoryClass.findMethod("processAttributes", new JType[]{factoryHelper.getWidgetFactoryContextType()});
	        if (method != null)
	        {
	        	TagAttributes attrs = method.getAnnotation(TagAttributes.class);
	        	if (attrs != null)
	        	{
	        		for (TagAttribute attr : attrs.value())
	        		{
	        			String attrName = attr.value();
	        			if (isValidName(attrName))
	        			{
	        				if (!Environment.isProduction() || widgetProperties.contains(attrName))
	        				{
	        					String setterMethod = ClassUtils.getSetterMethod(attrName);
	        					JClassType type = factoryClass.getOracle().getType(attr.type().getCanonicalName());
	        					if (ClassUtils.hasValidSetter(factoryHelper.getWidgetType(), setterMethod, type))
	        					{
	        						String expression;
	        						JClassType stringType = factoryClass.getOracle().findType(String.class.getCanonicalName());
	        						if (type.equals(stringType) && attr.supportsI18N())
	        						{
	        							expression = "ScreenFactory.getInstance().getDeclaredMessage("+attrName+")";
	        						}
	        						else
	        						{
	        							expression = ClassUtils.getParsingExpressionForSimpleType(attrName, type);
	        						}
	        						if (expression == null)
	        						{
	        							logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryInvalidProperty(attrName));
	        						}
	        						else
	        						{
	        							result.append("String "+attrName+" = context.readWidgetProperty(\""+attrName+"\");\n");
	        							if (attr.defaultValue().length() > 0)
	        							{
	        								result.append("if ("+attrName+" == null || "+attrName+".length() == 0){\n");
	        								result.append(attrName + " = \"" + attr.defaultValue() + "\";");
	        								result.append("}\n");
	        								result.append("else {\n");
	        							}
	        							else
	        							{
	        								result.append("if ("+attrName+" != null && "+attrName+".length() > 0){\n");
	        							}
	        							result.append("widget."+setterMethod+"("+expression+");\n");
	        							result.append("}\n");
	        						}
	        					}
	        					else
	        					{
	        						logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryInvalidProperty(attrName));
	        					}
	        				}
	        			}
	        			else
	        			{
	        				logger.log(TreeLogger.ERROR, messages.errorGeneratingWidgetFactoryInvalidAttrName(attrName));
	        			}
	        		}
	        	}
	        }
	        JClassType superclass = factoryClass.getSuperclass();
	        if (superclass!= null && !superclass.equals(superclass.getOracle().getJavaLangObject()))
	        {
	        	result.append(generateProcessAttributesBlock(superclass)+"\n");
	        }
	        JClassType[] interfaces = factoryClass.getImplementedInterfaces();
	        for (JClassType interfaceClass : interfaces)
	        {
	        	result.append(generateProcessAttributesBlock(interfaceClass)+"\n");
	        }
	        String attributes = result.toString();
	        attributesFromClass.put(factoryClassName, attributes);
	        return attributes;
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * 
	 * @param sourceWriter
	 */
	private void generateProcessAttributesMethod(SourceWriter sourceWriter)
	{
		sourceWriter.println("@Override");
		sourceWriter.println("public void processAttributes("+factoryHelper.getWidgetFactoryContextType().getParameterizedQualifiedSourceName()
		         +" context) throws InterfaceConfigException{"); 
		sourceWriter.indent();
		sourceWriter.println("super.processAttributes(context);");
		String attributesBlock = generateProcessAttributesBlock(factoryHelper.getFactoryClass());
		if (!StringUtils.isEmpty(attributesBlock))
		{
			sourceWriter.println(CruxMetaDataElement.class.getSimpleName()+" element = context.getElement();");
			sourceWriter.println(factoryHelper.getWidgetType().getParameterizedQualifiedSourceName()+" widget = context.getWidget();");
		}
		sourceWriter.print(attributesBlock);
		sourceWriter.outdent();
		sourceWriter.println("}");
	}
	
	/**
	 * @param methodsForInnerProcessing
	 * @param processChildrenMethod
	 * @param processorVariables
	 * @return
	 */
	private String generateProcessChildrenBlockFromMethod(Map<String, String> methodsForInnerProcessing, 
														   JMethod processChildrenMethod, Map<String, String> processorVariables)
	{
		String processingMethodName = null;
		TagChildren children = processChildrenMethod.getAnnotation(TagChildren.class);
		if (children != null)
		{
			StringBuilder source = new StringBuilder();
			processingMethodName = getProcessingMethodNameForProcessorMethod(processChildrenMethod);
			if (!methodsForInnerProcessing.containsKey(processingMethodName))
			{
				methodsForInnerProcessing.put(processingMethodName, null);
				AllowedOccurences allowedChildren = getAllowedChildrenNumber(children);
				boolean acceptNoChildren = (allowedChildren.minOccurs == 0);

				source.append(generateProcessingCallForAgregators(children, processorVariables));

				if (allowedChildren.maxOccurs == UNBOUNDED || allowedChildren.maxOccurs >= 1)
				{
					boolean hasChildElement = true;
					if (allowedChildren.maxOccurs == 1)
					{
						if(TextChildProcessor.class.isAssignableFrom(children.value()[0].value()))
						{
							source.append("String child = ensureTextChild(c.getChildElement(), "+acceptNoChildren+");\n");
							hasChildElement = false;
						}
						else
						{
							source.append(CruxMetaDataElement.class.getSimpleName()+" child = ensureFirstChild(c.getChildElement(), "+acceptNoChildren+");\n");
						}
						source.append("if (child != null){\n");
					}
					else
					{
						source.append("Array<"+CruxMetaDataElement.class.getSimpleName()+"> children = ensureChildren(c.getChildElement(), "+acceptNoChildren+");\n");
						source.append("if (children != null){\n");
						source.append("for(int _i_=0; _i_<children.size(); _i_++){\n");
						source.append(CruxMetaDataElement.class.getSimpleName()+" child = children.get(_i_);\n");
						source.append("if (child != null){\n");
					}
					if (hasChildElement)
					{
						source.append("c.setChildElement(child);\n");
					}

					source.append(generateChildrenBlockFromAnnotation(methodsForInnerProcessing, children, 
							                                                                       processorVariables, true));

					// TODO - Thiago - tratar validação de filhos obrigatorios .... espeficamente qdo parent for um agregador....
					if (allowedChildren.maxOccurs == UNBOUNDED || allowedChildren.maxOccurs > 1)
					{
						source.append("}\n");
						source.append("}\n");
					}
					source.append("}\n");
				}
				methodsForInnerProcessing.put(processingMethodName, source.toString());
			}
		}
		return processingMethodName;
	}

	/**
	 * @param sourceWriter
	 * @throws CruxGeneratorException
	 */
	private void generateProcessChildrenMethod(SourceWriter sourceWriter) throws CruxGeneratorException
	{
		try
        {
	        JMethod method = factoryHelper.getProcessChildrenMethod();
	        if (mustGenerateChildrenProcessMethod(method))
	        {
	        	// TODO - Thiago -  tratar instanciamento qdo for inner classe não estatica.
	        	Map<String, String> methodsForInnerProcessing = new HashMap<String, String>();
	        	Map<String, String> processorVariables = new HashMap<String, String>();

	        	generatingChildrenProcessingBlock(sourceWriter, method, methodsForInnerProcessing, processorVariables);
	        	generateMethodsForInnerProcessingChildren(sourceWriter, methodsForInnerProcessing);
	        	generateInnerProcessorsvariables(sourceWriter, processorVariables);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(messages.errorGeneratingWidgetFactory(e.getMessage()), e);
        }
	}

	/**
	 * @param factoryClass
	 * @param evtBinderVariables
	 * @return
	 */
	private String generateProcessEventsBlock(JClassType factoryClass, 
											   Map<String, String> evtBinderVariables) 
	{
		String factoryClassName = factoryClass.getQualifiedSourceName();
		if (eventsFromClass.containsKey(factoryClassName))
		{
			evtBinderVariables.putAll(eventsFromClass.get(factoryClassName).evtBinderVariables);
			return eventsFromClass.get(factoryClassName).evtBinderCalls;
		}
		
		StringBuilder result = new StringBuilder();
		
		JMethod method = factoryClass.findMethod("processEvents", new JType[]{factoryHelper.getWidgetFactoryContextType()});
		if (method != null)
		{
			TagEvents evts = method.getAnnotation(TagEvents.class);
			if (evts != null)
			{
				for (TagEvent evt : evts.value())
				{
					Class<? extends EvtBinder<?>> binderClass = evt.value();
					boolean generateEventBlock = true;
					if (Environment.isProduction())
					{
						try 
						{
							String eventName = binderClass.newInstance().getEventName();
							generateEventBlock = widgetProperties.contains(eventName);
						} 
						catch (Exception e) 
						{
							throw new CruxGeneratorException(e.getMessage(), e);//TODO message
						}
					}
					if (generateEventBlock)
					{
						String binderClassName = binderClass.getCanonicalName();
						String evtBinderVar = getEvtBinderVariableName("ev", evtBinderVariables, binderClassName);
						result.append(evtBinderVar+".bindEvent(element, widget);\n");
					}
				}
			}
		}
		JClassType superclass = factoryClass.getSuperclass();
		if (superclass!= null && superclass.getSuperclass() != null)
		{
			Map<String, String> evtBinderVariablesSubClasses = new HashMap<String, String>();
			String superClassBlock = generateProcessEventsBlock(superclass, evtBinderVariablesSubClasses);
			if (result.indexOf(superClassBlock) < 0)
			{
				result.append(superClassBlock+"\n");
				evtBinderVariables.putAll(evtBinderVariablesSubClasses);
			}
		}
		JClassType[] interfaces = factoryClass.getImplementedInterfaces();
		for (JClassType interfaceClass : interfaces)
		{
			Map<String, String> evtBinderVariablesSubClasses = new HashMap<String, String>();
			String superClassBlock = generateProcessEventsBlock(interfaceClass, evtBinderVariablesSubClasses);
			if (result.indexOf(superClassBlock) < 0)
			{
				result.append(superClassBlock+"\n");
				evtBinderVariables.putAll(evtBinderVariablesSubClasses);
			}
		}
		
		String events = result.toString();
		eventsFromClass.put(factoryClassName, new EventBinderData(events, evtBinderVariables));
		return events;
	}

	/**
	 * @param sourceWriter
	 * @throws CruxGeneratorException
	 */
	private void generateProcessEventsMethod(SourceWriter sourceWriter) throws CruxGeneratorException
	{
		Map<String, String> evtBinderVariables = new HashMap<String, String>();
		
		sourceWriter.println("@Override");
		sourceWriter.println("public void processEvents("+factoryHelper.getWidgetFactoryContextType().getParameterizedQualifiedSourceName()
				         +" context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processEvents(context);");
		String eventsBlock = generateProcessEventsBlock(factoryHelper.getFactoryClass(), evtBinderVariables);
		if (!StringUtils.isEmpty(eventsBlock))
		{
			sourceWriter.println(CruxMetaDataElement.class.getSimpleName()+" element = context.getElement();");
			sourceWriter.println(factoryHelper.getWidgetType().getParameterizedQualifiedSourceName()+" widget = context.getWidget();");
		}
		sourceWriter.print(eventsBlock);
		sourceWriter.println("}");
		
		generateInnerProcessorsvariables(sourceWriter, evtBinderVariables);
	}

	/**
	 * 
	 * @param logger
	 * @param sourceWriter
	 * @param children
	 */
	private String generateProcessingCallForAgregators(TagChildren children, Map<String, String> processorVariables)
	{
		StringBuilder source = new StringBuilder();
		for (TagChild child : children.value())
		{
			if (child.autoProcess())
			{
				Class<? extends WidgetChildProcessor<?>> childProcessor = child.value();
				
				if (ChoiceChildProcessor.class.isAssignableFrom(childProcessor) ||
					SequenceChildProcessor.class.isAssignableFrom(childProcessor) ||
					AllChildProcessor.class.isAssignableFrom(childProcessor))
				{
					String processorName = childProcessor.getCanonicalName();
					String evtBinderVar = getEvtBinderVariableName("p", processorVariables, processorName);
					source.append(evtBinderVar+".processChildren(c);\n");
				}
			}
		}
		return source.toString();
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param childrenSuffix
	 * @param first
	 * @param childProcessor
	 */
	private String generateTagIdentifierBlock(boolean first, JClassType childProcessor)
	{
		StringBuilder source = new StringBuilder();
		TagChildAttributes processorAttributes = getChildtrenAttributesAnnotation(childProcessor);
		if (first)
		{
			source.append("String  __tag = getChildName(child);\n");
		}
		else
		{
			source.append("else ");
		}
		
		if (processorAttributes == null || WidgetFactory.class.isAssignableFrom(processorAttributes.type())  
			|| processorAttributes.tagName().equals(""))
		{
			source.append("if (__tag == null || __tag.length() == 0){\n");
		}
		else
		{
			source.append("if (StringUtils.unsafeEquals(\""+processorAttributes.tagName()+"\",__tag)){\n");
		}
		
		return source.toString();
	}

	/**
	 * @param childProcessor
	 * @return
	 */
	private String generateTextProcessingBlock(JClassType childProcessor)
	{
		try
        {
	        TagChildAttributes processorAttributes = getChildtrenAttributesAnnotation(childProcessor);
	        JClassType hasTextType = childProcessor.getOracle().getType(HasText.class.getCanonicalName());
	        
	        if (processorAttributes.widgetProperty().length() > 0)
	        {
	        	return "if (child.length() > 0) c.getRootWidget()."+ClassUtils.getSetterMethod(processorAttributes.widgetProperty())+"(child);";
	        }
	        else if (hasTextType.isAssignableFrom(factoryHelper.getWidgetType()))
	        {
	        	return "if (child.length() > 0) c.getRootWidget().setText(child);";
	        	
	        }
	        return "";
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}

	/**
	 * @param sourceWriter
	 * @param method
	 * @param methodsForInnerProcessing
	 * @param processorVariables
	 */
	private void generatingChildrenProcessingBlock(SourceWriter sourceWriter, JMethod method, 
			Map<String, String> methodsForInnerProcessing, Map<String, String> processorVariables) 
	{
		String childrenProcessorMethodName = generateProcessChildrenBlockFromMethod(methodsForInnerProcessing, method, processorVariables);
        String contextDeclaration = factoryHelper.getWidgetChildProcessorContextType().getParameterizedQualifiedSourceName();

		sourceWriter.println("@Override");
		sourceWriter.println("public void processChildren("+factoryHelper.getWidgetFactoryContextType().getParameterizedQualifiedSourceName()
				               +" context) throws InterfaceConfigException{"); 
		sourceWriter.println("super.processChildren(context);");
		if (childrenProcessorMethodName != null)
		{
			sourceWriter.println(contextDeclaration+" c = new "+contextDeclaration+"(context);");
			sourceWriter.println("c.setChildElement(context.getElement());");
			sourceWriter.println(childrenProcessorMethodName+"(c);");
		}
		sourceWriter.println("}");
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
			TagChildAttributes processorAttributes = getChildtrenAttributesAnnotation(childProcessorType);

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

				JMethod processorMethod = childProcessorType.getMethod("processChildren", new JType[]{factoryHelper.getWidgetChildProcessorContextType()});
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
	 * 
	 * @param processorClass
	 * @return
	 */
	private TagChildAttributes getChildtrenAttributesAnnotation(JClassType processorClass)
	{
		TagChildAttributes attributes = processorClass.getAnnotation(TagChildAttributes.class);
		if (attributes == null)
		{
			JClassType superClass = processorClass.getSuperclass();
			if (superClass != null && superClass.getSuperclass() != null)
			{
				attributes = getChildtrenAttributesAnnotation(superClass);
			}
		}
		
		return attributes;
	}

	/**
	 * 
	 * @param variables
	 * @param binderClassName
	 * @return
	 */
	private String getEvtBinderVariableName(String varPrefix, Map<String, String> variables, String binderClassName)
	{
		String evtBinderVar = null;
		if (variables.containsValue(binderClassName))
		{
			for (String key : variables.keySet())
			{
				if (binderClassName.equals(variables.get(key)))
				{
					evtBinderVar = key;
					break;
				}
			}
		}
		else
		{
			evtBinderVar = varPrefix+getVariableNameSuffixCounter();
			variables.put(evtBinderVar, binderClassName);
		}
		return evtBinderVar;
	}
	
	/**
	 * @param processMethod
	 * @return
	 */
	private String getProcessingMethodNameForProcessorMethod(JMethod processMethod)
	{
		return RegexpPatterns.REGEXP_DOT.matcher(processMethod.getEnclosingType().getQualifiedSourceName()+processMethod.getName()).replaceAll("_");
	}
	
	/**
	 * 
	 * @return
	 */
	private int getVariableNameSuffixCounter()
	{
		return variableNameSuffixCounter++;
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
	 * @param method
	 * @return
	 */
	private boolean mustGenerateChildrenProcessMethod(JMethod method)
	{
		TagChildren children = method.getAnnotation(TagChildren.class);
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
}
