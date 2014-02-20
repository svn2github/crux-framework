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
package org.cruxframework.crux.core.rebind.screen.wrapper;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewContainer;
import org.cruxframework.crux.core.client.screen.views.ViewFactory.CreateCallback;
import org.cruxframework.crux.core.rebind.AbstractWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewAccessorProxyCreator extends AbstractWrapperProxyCreator
{
	private final JClassType viewType;
	private JClassType createCallbackType;

	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public ViewAccessorProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType invokerIntf)
    {
	    super(logger, context, invokerIntf);
	    try
        {
	        viewType = invokerIntf.getOracle().getType(View.class.getCanonicalName());
	        createCallbackType = invokerIntf.getOracle().getType(CreateCallback.class.getCanonicalName());
        }
        catch (NotFoundException e)
        {
        	throw new CruxGeneratorException(e.getMessage(), e);
        }
    }

	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				View.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				Window.class.getCanonicalName()
		};
		return imports;       
    }

	@Override
	protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    super.generateProxyMethods(srcWriter);
	    generateViewGetterMethod(srcWriter);
	    generateRootViewMethod(srcWriter);
	}
	
	/**
	 * @param method
	 * @param sourceWriter
	 * @param interfaceClass
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter)
	{
		String name = method.getName();
		JParameter[] parameters = method.getParameters();
		if (name.equals("getRootView") && parameters.length == 0)
		{
			return; //Ignore
		}
		JType returnType = method.getReturnType();
		JClassType returnTypeClass = returnType.isClassOrInterface();
		if (returnTypeClass!= null && viewType.isAssignableFrom(returnTypeClass))
		{	
			if(parameters.length == 0)
			{
				String widgetName;
				if (name.startsWith("get"))
				{
					widgetName = name.substring(3);
					if (widgetName.length() > 0)
					{
						generateWrapperMethodForGetter(sourceWriter, name, widgetName);
					}
				}
				else
				{
					widgetName = name;
					generateWrapperMethod(sourceWriter, name, widgetName);
				}
			}
			else
			{
				throw new CruxGeneratorException("The method ["+method.getName()+"] from ViewAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must have no parameters.");
			}
		}
		else if (returnTypeClass == null && parameters.length == 1 && parameters[0].getType().isInterface() != null && parameters[0].getType().isInterface().isAssignableTo(createCallbackType))
		{
			generateLoaderMethod(sourceWriter, name);
		}
		else
		{
			throw new CruxGeneratorException("The method ["+method.getName()+"] from ViewAccessor ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a subclass of View.");
		}
	}

	/**
	 * @param sourceWriter
	 * @param name
	 * @param viewName
	 */
	private void generateWrapperMethod(SourcePrinter sourceWriter, String name, String viewName)
    {
		sourceWriter.println("public View " + name+"(){");
		sourceWriter.println("return View.getView(\""+viewName+"\");");
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 * @param name
	 * @param viewName
	 */
	private void generateLoaderMethod(SourcePrinter sourceWriter, String name)
    {
		sourceWriter.println("public void " + name+"("+CreateCallback.class.getCanonicalName()+" callback){");
		sourceWriter.println("if (callback != null) {");
		sourceWriter.println("View ret = View.getView(\""+name+"\");");
		sourceWriter.println("if (ret != null) {");
		sourceWriter.println("callback.onViewCreated(ret);");
		sourceWriter.println("} else {");
		sourceWriter.println(ViewContainer.class.getCanonicalName()+".createView(\""+name+"\", callback);");
		sourceWriter.println("}");
		sourceWriter.println("}");
		sourceWriter.println("}");
    }

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param viewName
	 */
	private void generateWrapperMethodForGetter(SourcePrinter sourceWriter,
			String name, String viewName)
	{
		String widgetNameFirstLower = Character.toLowerCase(viewName.charAt(0)) + viewName.substring(1);
		sourceWriter.println("public View " + name+"(){");
		sourceWriter.println("return _getView(\""+widgetNameFirstLower+"\");");
		sourceWriter.println("}");
	}
	
	private void generateRootViewMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public View getRootView(){");
		sourceWriter.println("return "+Screen.class.getCanonicalName()+".getRootView();");
		sourceWriter.println("}");
	}

	protected void generateViewGetterMethod(SourcePrinter srcWriter)
	{
		srcWriter.println("public View _getView(String viewName){");
		srcWriter.println("View ret = View.getView(viewName);");
		srcWriter.println("if (ret == null){");
		srcWriter.println("String viewNameFirstUpper;");
		srcWriter.println("if (viewName.length() > 1){"); 
		srcWriter.println("viewNameFirstUpper = Character.toUpperCase(viewName.charAt(0)) + viewName.substring(1);");
		srcWriter.println("}");
		srcWriter.println("else{"); 
		srcWriter.println("viewNameFirstUpper = \"\"+Character.toUpperCase(viewName.charAt(0));");
		srcWriter.println("}");
		srcWriter.println("ret = View.getView(viewNameFirstUpper);");
		srcWriter.println("}");
		srcWriter.println("return ret;");
		srcWriter.println("}");
	}
	
}
