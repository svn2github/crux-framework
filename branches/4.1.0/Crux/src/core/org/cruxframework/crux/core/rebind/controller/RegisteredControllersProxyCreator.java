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
package org.cruxframework.crux.core.rebind.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.crossdoc.CrossDocument;
import org.cruxframework.crux.core.client.event.CrossDocumentInvoker;
import org.cruxframework.crux.core.client.event.RegisteredControllers;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.ioc.IocContainerRebind;
import org.cruxframework.crux.core.rebind.module.Modules;
import org.cruxframework.crux.core.rebind.screen.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;

/**
 * Generates a RegisteredControllers class. 
 * 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class RegisteredControllersProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Map<String, String> controllerClassNames = new HashMap<String, String>();
	private Map<String, String> crossDocsClassNames = new HashMap<String, String>();
	private final View view;
	private final String module;
	private String iocContainerClassName;
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public RegisteredControllersProxyCreator(TreeLogger logger, GeneratorContextExt context, View view, String module)
    {
	    super(logger, context, context.getTypeOracle().findType(RegisteredControllers.class.getCanonicalName()), false);
		this.view = view;
		this.module = module;
    }

	/**
	 * 
	 * @param sourceWriter
	 * @throws CruxGeneratorException
	 */
	@Override
	protected void generateProxyContructor(SourcePrinter sourceWriter) throws CruxGeneratorException
	{
		sourceWriter.println("public "+getProxySimpleName()+"("+org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName()+" view){");
		sourceWriter.println("this.view = view;");
		for (String controller : controllerClassNames.keySet()) 
		{
			JClassType controllerClass = getControllerClass(controller);
			if (!isControllerLazy(controllerClass))
			{
				String controllerVar = createController(sourceWriter, controller);
				sourceWriter.println("controllers.put(\""+controller+"\", "+controllerVar+");");
			}
		}
		sourceWriter.println("}");
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private FastMap<ControllerInvoker> controllers = new FastMap<ControllerInvoker>();");
		srcWriter.println("private "+org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName()+" view;");
		srcWriter.println("private "+iocContainerClassName+" iocContainer = new "+iocContainerClassName+"();");
    }	

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyMethods(SourcePrinter sourceWriter) throws CruxGeneratorException
    {
		generateControllerInvokeMethod(sourceWriter);
		generateCrossDocInvokeMethod(sourceWriter);
		generateGetControllertMethod(sourceWriter);
    }

	/**
	 * @see org.cruxframework.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateSubTypes(SourcePrinter srcWriter) throws CruxGeneratorException
	{
	    iocContainerClassName = new IocContainerRebind(logger, context, view).create();
		Set<String> usedWidgets = new HashSet<String>();
		generateControllersForView(view);
		Iterator<org.cruxframework.crux.core.rebind.screen.Widget> screenWidgets = view.iterateWidgets();
		while (screenWidgets.hasNext())
		{
			String widgetType = screenWidgets.next().getType();
			usedWidgets.add(widgetType);
		}
		if (module != null)
		{
			generateControllersForWidgets(usedWidgets, module);
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("deprecation")
    @Override
	protected String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		RunAsyncCallback.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.EventProcessor.class.getCanonicalName(),
    		Crux.class.getCanonicalName(), 
    		FastMap.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.ControllerInvoker.class.getCanonicalName(),
    		CrossDocumentInvoker.class.getCanonicalName(), 
    		StringUtils.class.getCanonicalName()
		};
	    return imports;
    }	
	
	/**
	 * Generate the block to include controller object.
	 * @param controller
	 * @param module
	 */
	private void generateControllerBlock(String controller, String module)
	{
		try
		{
			JClassType controllerClass = getControllerClass(controller);
			if (!controllerClassNames.containsKey(controller) && controllerClass!= null)
			{
				String controllerClassName = controllerClass.getQualifiedSourceName();
				if (Modules.getInstance().isClassOnModulePath(controllerClassName, module))
				{
					//TODO passar a view pra dentro da controller pra poder fazer bind de valores na tela....
					// fazer a controller proxy criada receber uma variavel com a view associada para referenciar por ela
					String genClass = new ControllerProxyCreator(logger, context, controllerClass).create();
					controllerClassNames.put(controller, genClass);
					JClassType crossDocumentType = controllerClass.getOracle().getType(CrossDocument.class.getCanonicalName());
					if (crossDocumentType.isAssignableFrom(controllerClass))
					{
						crossDocsClassNames.put(controller, genClass);
					}
				}
			}
		}
		catch (Throwable e) 
		{
			throw new CruxGeneratorException("Error for register client event handler. Controller: ["+controller+"].", e);
		}
	}	
	
	/**
	 * 
	 * @param sourceWriter
	 * @param controllerClassNames
	 * @throws CruxGeneratorException 
	 */
	private void generateControllerInvokeMethod(SourcePrinter sourceWriter) throws CruxGeneratorException
	{
		sourceWriter.println("public void invokeController(final String controllerName, final String method, final boolean fromOutOfModule, final Object sourceEvent, final EventProcessor eventProcessor){");

		if (isCrux2OldInterfacesCompatibilityEnabled())
		{
		    sourceWriter.println("ControllerInvoker controller = getController(controllerName);");
			sourceWriter.println("if (controller != null){");
			sourceWriter.println("try{");
			sourceWriter.println("controller.invoke(method, sourceEvent, fromOutOfModule, eventProcessor);");
			sourceWriter.println("}");
			sourceWriter.println("catch (Exception e)"); 
			sourceWriter.println("{");
			sourceWriter.println("eventProcessor.setException(e);");
			sourceWriter.println("}");
			sourceWriter.println("return;");
			sourceWriter.println("}");
			sourceWriter.println("else {");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(controllerName));");
			sourceWriter.println("}");
		}
		else
		{
			sourceWriter.println("Crux.getErrorHandler().handleError("+EscapeUtils.quote("To use this feature you need to enable crux2 old interfaces compatibility.")+");");
		}
		sourceWriter.println("}");
	}

	/**
	 * Generate wrapper classes for event handling.
	 * @param view
	 */
	private void generateControllersForView(View view)
	{
		Iterator<String> controllers = view.iterateControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			generateControllerBlock(controller, module);
		}		

		controllers = ClientControllers.iterateGlobalControllers();
		
		while (controllers.hasNext())
		{
			String controller = controllers.next();
			JClassType controllerClass = getControllerClass(controller);
			if (controllerClass != null)
			{
				generateControllerBlock(controller, module);
			}
		}		
	}

	/**
	 * @param usedWidgets
	 * @param module
	 */
	private void generateControllersForWidgets(Set<String> usedWidgets, String module)
	{
		
		Iterator<String> widgets = usedWidgets.iterator();
		while (widgets.hasNext())
		{
			Iterator<String> controllers = ClientControllers.iterateWidgetControllers(widgets.next());
			if (controllers != null)
			{
				while (controllers.hasNext())
				{
					String controller = controllers.next();
					JClassType controllerClass = getControllerClass(controller);
					if (controllerClass != null)
					{
						generateControllerBlock(controller, module);
					}
				}
			}		
		}
	}

	/**
	 * @param sourceWriter
	 */
	private void generateCrossDocInvokeMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public String invokeCrossDocument(String serializedData){");

		if (!this.crossDocsClassNames.isEmpty())
		{
			sourceWriter.println("if (serializedData != null){");

			sourceWriter.println("int idx = serializedData.indexOf('|');");
			sourceWriter.println("if (idx > 0){");

			sourceWriter.println("String controllerName = null;");
			sourceWriter.println("try{");

			sourceWriter.println("controllerName = serializedData.substring(0,idx);");
			sourceWriter.println("serializedData = serializedData.substring(idx+1);");
			sourceWriter.println("CrossDocumentInvoker crossDoc = (CrossDocumentInvoker)getController(controllerName);");
			sourceWriter.println("if (crossDoc==null){");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().eventProcessorClientControllerNotFound(controllerName));");
			sourceWriter.println("return null;");
			sourceWriter.println("} else {");

			sourceWriter.println("return crossDoc.invoke(serializedData);");

			sourceWriter.println("}");

			sourceWriter.println("} catch(ClassCastException ex){");
			sourceWriter.println("Crux.getErrorHandler().handleError(Crux.getMessages().crossDocumentInvalidCrossDocumentController(controllerName));");
			sourceWriter.println("return null;");
			sourceWriter.println("}");

			sourceWriter.println("}");

			sourceWriter.println("}");
		}
		sourceWriter.println("return null;");
		sourceWriter.println("}");
	}

	/**
	 * @param sourceWriter
	 */
	private void generateGetControllertMethod(SourcePrinter sourceWriter)
	{
		sourceWriter.println("public <T> T getController(String controller){");
		sourceWriter.println("T ret = (T)controllers.get(controller);");
		sourceWriter.println("if (ret == null){");
		
		boolean first = true;
		for (String controller : controllerClassNames.keySet())
        {
			JClassType controllerClass = getControllerClass(controller);
			if (isControllerLazy(controllerClass))
			{
				if (!first)
				{
					sourceWriter.print("else ");
				}
				first = false;
				sourceWriter.println("if ("+StringUtils.class.getCanonicalName()+".unsafeEquals(controller, "+EscapeUtils.quote(controller)+")){");

				String controllerVar = createController(sourceWriter, controller);
				if (isControllerStatefull(controllerClass))
				{
					sourceWriter.println("controllers.put("+EscapeUtils.quote(controller)+", "+controllerVar+");");
				}
				else
				{
					sourceWriter.println("ret = (T) "+controllerVar+";");
				}

				sourceWriter.println("}");
			}
        }
		
		sourceWriter.println("if (ret == null){");
		sourceWriter.println("ret = (T)controllers.get(controller);");
		sourceWriter.println("}");
		
		sourceWriter.println("}");
		
		sourceWriter.println("return ret;");

		sourceWriter.println("}");
	}
	
	/**
	 * 
	 * @param sourceWriter
	 * @param controller
	 * @return
	 */
	private String createController(SourcePrinter sourceWriter, String controller)
	{
		String controllerClassName = controllerClassNames.get(controller);
		sourceWriter.println(controllerClassName+" __cont  = new "+controllerClassName+"(this.view);");
		JClassType controllerClass = getControllerClass(controller);
		if (controllerClass == null)
		{
			throw new CruxGeneratorException("Can not found the controller ["+controllerClassName+"]. Check your classpath and the inherit modules");
		}
		IocContainerRebind.injectFields(sourceWriter, controllerClass, "__cont", "iocContainer");
		return "__cont";
	}

	/**
	 * @param controller
	 * @return
	 */
	private JClassType getControllerClass(String controller) 
	{
		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);

		return typeOracle.findType(ClientControllers.getController(controller));
	}

	/**
	 * @param controllerClass
	 * @return true if this controller can be loaded in lazy mode
	 * @throws CruxGeneratorException 
	 */
	private boolean isControllerLazy(JClassType controllerClass) throws CruxGeneratorException
    {
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
        return (controllerAnnot == null || controllerAnnot.lazy());
    }
	
	/**
	 * @param controllerClass
	 * @return true if this controller can be loaded in lazy mode
	 * @throws CruxGeneratorException 
	 */
	private boolean isControllerStatefull(JClassType controllerClass) throws CruxGeneratorException
    {
		Controller controllerAnnot = controllerClass.getAnnotation(Controller.class);
        return (controllerAnnot == null || controllerAnnot.stateful());
    }

	@Override
	public String getProxySimpleName()
	{
		String className = view.getId(); 
		className = className.replaceAll("[\\W]", "_");
		return "RegisteredControllers_"+className;
	}	
}
