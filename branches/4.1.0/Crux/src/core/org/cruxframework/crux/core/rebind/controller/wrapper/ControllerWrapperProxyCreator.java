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
package org.cruxframework.crux.core.rebind.controller.wrapper;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractViewBindableProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ControllerWrapperProxyCreator extends AbstractViewBindableProxyCreator
{
	public ControllerWrapperProxyCreator(TreeLogger logger, GeneratorContextExt context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
    }

	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName()
		};
		return imports;       
    }

	/**
	 * @param method
	 * @param sourceWriter
	 * @param interfaceClass
	 */
	@Override
	protected void generateWrapperMethod(JMethod method, SourcePrinter sourceWriter)
	{
		JType returnType = method.getReturnType();
		
		JClassType returnTypeClass = returnType.isClassOrInterface();
		if (returnTypeClass != null)
		{
			Controller controller = returnTypeClass.getAnnotation(Controller.class);
			if (controller != null)
			{
				if(method.getParameters().length == 0)
				{
						generateWrapperMethod(sourceWriter, returnTypeClass, controller, method.getName());
				}
				else
				{
					throw new CruxGeneratorException("The method ["+method.getName()+"] from ControllerWrapper ["+method.getEnclosingType().getQualifiedSourceName()+"] must have no parameters.");
				}
			}
			else
			{
				throw new CruxGeneratorException("The method ["+method.getName()+"] from ControllerWrapper ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a class annotated with @Controller.");
			}
		}
		else
		{
			throw new CruxGeneratorException("The method ["+method.getName()+"] from ControllerWrapper ["+method.getEnclosingType().getQualifiedSourceName()+"] must return a class annotated with @Controller.");
		}
	}

	/**
	 * @param sourceWriter
	 * @param returnType
	 * @param name
	 * @param widgetName
	 */
	private void generateWrapperMethod(SourcePrinter sourceWriter, JClassType returnType, Controller controller, String methodName)
    {
		String classSourceName = returnType.getParameterizedQualifiedSourceName();
		sourceWriter.println("public "+classSourceName+" " + methodName+"(){");
		sourceWriter.println("assert(view != null):"+EscapeUtils.quote("View was not loaded. Ensure that setView method was called.")+";");
		sourceWriter.println("return view.getController("+EscapeUtils.quote(controller.value())+");");
		sourceWriter.println("}");
    }
}
