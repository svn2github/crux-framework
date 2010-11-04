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
package br.com.sysmap.crux.core.rebind.screen;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.sysmap.crux.core.client.collection.FastMap;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyCondition;
import br.com.sysmap.crux.core.client.declarative.TagChildLazyConditions;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.screen.WidgetLazyRuntimeCheckers;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaData;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.config.ConfigurationFactory;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.scanner.screen.Screen;
import br.com.sysmap.crux.core.rebind.scanner.screen.Widget;
import br.com.sysmap.crux.core.rebind.scanner.screen.config.WidgetConfig;
import br.com.sysmap.crux.core.rebind.widget.WidgetFactoryHelper;
import br.com.sysmap.crux.core.server.Environment;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class WidgetLazyRuntimeCheckersProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public WidgetLazyRuntimeCheckersProxyCreator(TreeLogger logger, GeneratorContext context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
    }
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				com.google.gwt.user.client.ui.Widget.class.getCanonicalName(),
				FastMap.class.getCanonicalName(),
				CruxMetaData.class.getCanonicalName(),
				WidgetLazyRuntimeCheckers.class.getCanonicalName()
		};
		return imports;       
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public "+getProxySimpleName()+"(){");
		srcWriter.indent();

		List<Screen> screens = getScreens();
		
		try
        {
	        if (Environment.isProduction() || !Boolean.parseBoolean(ConfigurationFactory.getConfigurations().enableHotDeploymentForWidgetFactories()))
	        {
		        Set<String> added = new HashSet<String>();
	        	for (Screen screen : screens)
	        	{
	        		Iterator<Widget> iterator = screen.iterateWidgets();
	        		while (iterator.hasNext())
	        		{
	        			Widget widget = iterator.next();
	        			generateCreateCheckerBlock(srcWriter, widget.getType(), added);
	        		}
	        	}
	        }
	        else
	        {
	        	generateCreateCheckerBlockForAllWidgets(srcWriter);
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }		
		
		srcWriter.outdent();
		srcWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	protected void generateCreateCheckerBlockForAllWidgets(SourceWriter sourceWriter)
	{
		try
        {
	        Set<String> libraries = WidgetConfig.getRegisteredLibraries();
	        Set<String> added = new HashSet<String>();
	        for (String library : libraries)
	        {
	        	Set<String> registeredLibraryFactories = WidgetConfig.getRegisteredLibraryFactories(library);
	        	for (String factory : registeredLibraryFactories)
	        	{
	        		String type = library+"_"+factory;
	        		generateCreateCheckerBlock(sourceWriter, type, added);
	        	}
	        }
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
	}
	
	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("FastMap<WidgetLazyChecker> checkers = new FastMap<WidgetLazyChecker>();");
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public WidgetLazyChecker getWidgetLazyChecker(String widgetType){");
		srcWriter.indent();
		srcWriter.println("return checkers.get(widgetType);");
		srcWriter.outdent();
		srcWriter.println("}");
	}

	private void generateCreateCheckerBlock(SourceWriter srcWriter, String type, Set<String> added) throws NotFoundException
    {
		if (!added.contains(type))
		{
			generateLazyChecker(srcWriter, type);
			added.add(type);
		}
    }	

	/**
	 * @param type
	 * @throws NotFoundException 
	 */
	private void generateLazyChecker(SourceWriter srcWriter, String type) throws NotFoundException 
	{
		String widgetFactoryClass = WidgetConfig.getClientClass(type);
		JClassType factoryType = context.getTypeOracle().getType(widgetFactoryClass);
		WidgetFactoryHelper factoryHelper = new WidgetFactoryHelper(factoryType);
		
		JMethod childrenMethod = factoryHelper.getProcessChildrenMethod();
		
		generateLazyChecker(childrenMethod, factoryHelper, type, srcWriter, new HashSet<String>());
	}

	/**
	 * @param childrenMethod
	 * @param factoryHelper
	 * @param declaredCheckers
	 * @throws NotFoundException
	 */
	private void generateLazyChecker(JMethod childrenMethod, WidgetFactoryHelper factoryHelper, String type, SourceWriter srcWriter, Set<String> processedMethods) throws NotFoundException
    {
		if (!processedMethods.contains(childrenMethod.getJsniSignature()))
		{
			processedMethods.add(childrenMethod.getJsniSignature());
			TagChildren tagChildren = childrenMethod.getAnnotation(TagChildren.class);
			if (tagChildren != null)
			{
				for (TagChild child : tagChildren.value())
				{
					JClassType childProcessor = context.getTypeOracle().getType(child.value().getCanonicalName());
					TagChildLazyConditions lazyConditions = childProcessor.getAnnotation(TagChildLazyConditions.class);
					if (lazyConditions != null)
					{
						generateLazyChecker(lazyConditions, type, srcWriter);
					}
					JMethod childProcessorMethod = factoryHelper.getChildProcessorMethod(childProcessor);
					generateLazyChecker(childProcessorMethod, factoryHelper, type, srcWriter, processedMethods);
				}
			}
		}
    }

	/**
	 * @param lazyConditions
	 * @return
	 */
	private void generateLazyChecker(final TagChildLazyConditions lazyConditions, String type, SourceWriter srcWriter)
    {
	    if (lazyConditions.all().length > 0)
	    {
	    	srcWriter.print("checkers.put(\""+type+"\", ");
	    	srcWriter.println("new WidgetLazyChecker(){");
	    	srcWriter.indent();
	    	srcWriter.println("public boolean isLazy(CruxMetaData widget){");
	    	srcWriter.indent();
	    	srcWriter.println("boolean lazy = true;");
	    	srcWriter.println("String property;");
	    	for (TagChildLazyCondition lazyCondition : lazyConditions.all())
	    	{
	    		srcWriter.println("property = widget.getProperty("+EscapeUtils.quote(lazyCondition.property())+");");
	    		if (lazyCondition.equals().length() > 0)
	    		{
	    			srcWriter.println("lazy = lazy && (property != null && property.equals("+EscapeUtils.quote(lazyCondition.equals())+"));");
	    		}
	    		else if (lazyCondition.notEquals().length() > 0)
	    		{
	    			srcWriter.println("lazy = lazy && (property == null || !property.equals("+EscapeUtils.quote(lazyCondition.notEquals())+"));");
	    		}
	    	}
	    	srcWriter.println("return lazy;");
	    	srcWriter.outdent();
	    	srcWriter.println("}");
	    	srcWriter.outdent();
	    	srcWriter.println("});"); 
	    }
	    else if (lazyConditions.any().length > 0)
	    {
	    	srcWriter.print("checkers.put(\""+type+"\", ");
	    	srcWriter.println("new WidgetLazyChecker(){");
	    	srcWriter.indent();
	    	srcWriter.println("public boolean isLazy(CruxMetaData widget){");
	    	srcWriter.indent();
	    	srcWriter.println("boolean lazy = false;");
	    	srcWriter.println("String property;");
	    	for (TagChildLazyCondition lazyCondition : lazyConditions.all())
	    	{
	    		srcWriter.println("property = widget.getProperty("+EscapeUtils.quote(lazyCondition.property())+");");
	    		if (lazyCondition.equals().length() > 0)
	    		{
	    			srcWriter.println("lazy = lazy || (property != null && property.equals("+EscapeUtils.quote(lazyCondition.equals())+"));");
	    		}
	    		else if (lazyCondition.notEquals().length() > 0)
	    		{
	    			srcWriter.println("lazy = lazy || (property == null || !property.equals("+EscapeUtils.quote(lazyCondition.notEquals())+"));");
	    		}
	    	}
	    	srcWriter.println("return lazy;");
	    	srcWriter.outdent();
	    	srcWriter.println("}");
	    	srcWriter.outdent();
	    	srcWriter.println("});"); 
	    }
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
	{
	}
}
