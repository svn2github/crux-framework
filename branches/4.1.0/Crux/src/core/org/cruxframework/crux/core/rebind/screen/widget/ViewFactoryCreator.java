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
package org.cruxframework.crux.core.rebind.screen.widget;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.datasource.DataSource;
import org.cruxframework.crux.core.client.datasource.RegisteredDataSources;
import org.cruxframework.crux.core.client.event.RegisteredControllers;
import org.cruxframework.crux.core.client.screen.InterfaceConfigException;
import org.cruxframework.crux.core.client.screen.LazyPanelWrappingType;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.eventadapter.TapEventAdapter;
import org.cruxframework.crux.core.client.screen.views.ViewAttachEvent;
import org.cruxframework.crux.core.client.screen.views.ViewAttachHandler;
import org.cruxframework.crux.core.client.screen.views.ViewDetachEvent;
import org.cruxframework.crux.core.client.screen.views.ViewDetachHandler;
import org.cruxframework.crux.core.client.screen.views.ViewFactory;
import org.cruxframework.crux.core.client.screen.views.ViewFactoryUtils;
import org.cruxframework.crux.core.client.screen.views.ViewLoadEvent;
import org.cruxframework.crux.core.client.screen.views.ViewLoadHandler;
import org.cruxframework.crux.core.client.screen.views.ViewUnloadEvent;
import org.cruxframework.crux.core.client.screen.views.ViewUnloadHandler;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.config.ConfigurationFactory;
import org.cruxframework.crux.core.declarativeui.ViewParser;
import org.cruxframework.crux.core.i18n.MessageClasses;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.controller.ClientControllers;
import org.cruxframework.crux.core.rebind.controller.ControllerProxyCreator;
import org.cruxframework.crux.core.rebind.controller.RegisteredControllersProxyCreator;
import org.cruxframework.crux.core.rebind.datasource.RegisteredDataSourcesProxyCreator;
import org.cruxframework.crux.core.rebind.ioc.IocContainerRebind;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoryCreator extends AbstractProxyCreator
{
	private static NameFactory nameFactory = new NameFactory();
	private static String viewVariable = "__view";;

	private Map<String, Boolean> attachToDOMfactories = new HashMap<String, Boolean>();
	private Map<String, String> declaredMessages = new HashMap<String, String>();
	private Map<String, WidgetCreator<?>> creators = new HashMap<String, WidgetCreator<?>>();
	private Map<String, WidgetCreatorHelper> creatorHelpers = new HashMap<String, WidgetCreatorHelper>();
	private final LazyPanelFactory lazyFactory;
	private final Set<String> lazyPanels = new HashSet<String>();
	private final LinkedList<PostProcessingPrinter> postProcessingCode = new LinkedList<PostProcessingPrinter>();
	private String loggerVariable;
	private String viewPanelVariable;
	private String device;
	private Set<String> rootPanelChildren;
	protected final String module;
	protected final View view;
	protected ControllerAccessHandler controllerAccessHandler;
	protected WidgetConsumer widgetConsumer;

	protected String iocContainerClassName;

	/**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public interface WidgetConsumer
	{
		public static EmptyWidgetConsumer EMPTY_WIDGET_CONSUMER = new EmptyWidgetConsumer();

		void consume(SourcePrinter out, String widgetId, String widgetVariableName);
	}

	/**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public interface LazyCompatibleWidgetConsumer extends WidgetConsumer
	{
		void handleLazyWholeWidgetCreation(SourcePrinter out, String widgetId);
		void handleLazyWrapChildrenCreation(SourcePrinter out, String widgetId);
	}

	/**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class EmptyWidgetConsumer implements WidgetConsumer
	{
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName)
		{
		}
	}

	/**
	 *
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public class ViewWidgetConsumer implements LazyCompatibleWidgetConsumer
	{
		public void consume(SourcePrinter out, String widgetId, String widgetVariableName)
		{
			out.println(getViewVariable()+".addWidget("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +");");
			if (Boolean.parseBoolean(ConfigurationFactory.getConfigurations().renderWidgetsWithIDs()))
			{
				out.println("ViewFactoryUtils.updateWidgetElementId("+EscapeUtils.quote(widgetId)+", "+ widgetVariableName +", "+viewVariable+");");
			}
		}

		@Override
        public void handleLazyWholeWidgetCreation(SourcePrinter out, String widgetId)
        {
			out.println(getViewVariable()+".checkRuntimeLazyDependency("+EscapeUtils.quote(widgetId)+", "+
					EscapeUtils.quote(ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget)) +");");
        }

		@Override
        public void handleLazyWrapChildrenCreation(SourcePrinter out, String widgetId)
        {
			out.println(getViewVariable()+".checkRuntimeLazyDependency("+EscapeUtils.quote(widgetId)+", "+
					EscapeUtils.quote(ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapChildren)) +");");
        }
	}

	/**
	 * Constructor
	 *
	 * @param context
	 * @param logger
	 * @param view
	 * @param device
	 * @param module
	 */
	public ViewFactoryCreator(GeneratorContextExt context, TreeLogger logger, View view, String device, String module)
    {
		super(logger, context);
		this.view = view;
		this.device = device;
		this.module = module;
		this.lazyFactory = new LazyPanelFactory(this);
		this.loggerVariable = createVariableName("logger");
		this.viewPanelVariable = createVariableName("viewPanel");
		this.widgetConsumer = new ViewWidgetConsumer();
		this.rootPanelChildren = new HashSet<String>();
		this.controllerAccessHandler = new DefaultControllerAccessor(viewVariable);
    }

	/**
     * Creates a new unique name based off of{@code varName} and adds it to
     * the list of known names.
	 *
	 * @param varName
	 * @return
	 */
	public static String createVariableName(String varName)
	{
		return nameFactory.createName(varName);
	}

	/**
	 * Retrieve the object responsible for print controller access expressions on client JS
	 * @return
	 */
	protected ControllerAccessHandler getControllerAccessHandler()
	{
		return this.controllerAccessHandler;
	}

	/**
	 * Generate the View fields
	 *
	 * @param printer
	 */
	protected void generateProxyFields(SourcePrinter printer)
    {

		printer.println("private "+RegisteredControllers.class.getCanonicalName()+" registeredControllers;");
		printer.println("private "+RegisteredDataSources.class.getCanonicalName()+" registeredDataSources;");

		for (String messageClass: declaredMessages.keySet())
	    {
	    	printer.println("private "+messageClass+" "+declaredMessages.get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
		printer.println("private final View "+viewVariable+" = this;");
		printer.println("private static "+Logger.class.getCanonicalName()+" "+loggerVariable+" = "+
	    		Logger.class.getCanonicalName()+".getLogger("+getProxySimpleName()+".class.getName());");
		printer.println("private "+HTMLPanel.class.getCanonicalName()+" "+viewPanelVariable+" = null;");
    }

	/**
	 * Generate the View Constructor
	 */
	@Override
	protected void generateProxyContructor(SourcePrinter printer) throws CruxGeneratorException
	{
		String regsiteredControllersClass = new RegisteredControllersProxyCreator(logger, context, view, module, iocContainerClassName).create();
		String regsiteredDataSourcesClass = new RegisteredDataSourcesProxyCreator(logger, context, view, iocContainerClassName).create();

		printer.println("protected "+getProxySimpleName()+"(String id, String title){");
		printer.println("super(id, title);");
		printer.println(iocContainerClassName +" iocContainer = new "+iocContainerClassName+"(this);");
		printer.println("this.registeredControllers = new "+regsiteredControllersClass+"(this, iocContainer);");
		printer.println("this.registeredDataSources = new "+regsiteredDataSourcesClass+"(this, iocContainer);");
		printer.println("}");
	}

	/**
	 * Generate the View methods.
	 *
     * @param printer
     */
    protected void generateProxyMethods(SourcePrinter printer)
    {
    	generateGetRegisteredControllersMethod(printer);
    	generateCreateDataSourceMethod(printer);
    	generateCreateWidgetsMethod(printer);
    	generateRenderMethod(printer);
    	generateInitializeLazyDependenciesMethod(printer);
    }

    @Override
    protected void generateSubTypes(SourcePrinter srcWriter) throws CruxGeneratorException
    {
	    iocContainerClassName = new IocContainerRebind(logger, context, view).create();
    }

	/**
	 * Return the type of a given crux meta tag. This type could be {@code "screen"} or
	 * another string referencing a registered {@code WidgetFactory}.
	 *
	 * @param cruxMetaElement
	 * @return
	 */
	protected String getMetaElementType(JSONObject cruxMetaElement)
	{
		return cruxMetaElement.optString("_type");
	}

	/**
	 * Returns the creator of the widgets of the given type.
	 * @param widgetType
	 * @return the creator of the widgets of the given type.
	 */
	protected WidgetCreator<?> getWidgetCreator(String widgetType)
	{
		try
        {
	        if (!creators.containsKey(widgetType))
	        {
	        	String creatorClassName = WidgetConfig.getClientClass(widgetType);
	        	Class<?> widgetCreator = Class.forName(creatorClassName);
	        	WidgetCreator<?> factory = (WidgetCreator<?>) widgetCreator.newInstance();
	        	factory.setViewFactory(this);
	        	creators.put(widgetType, factory);
	        	WidgetCreatorHelper creatorHelper = new WidgetCreatorHelper(widgetCreator);
				creatorHelpers.put(widgetType, creatorHelper);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException("Error retrieveing widgetFactory for type ["+widgetType+"].",e);
        }
		return creators.get(widgetType);
	}

	/**
     * Returns a helper object to create the code of the widgets of the given type.
	 * @param widgetType
	 * @return a helper object to create the code of the widgets of the given type.
	 */
	protected WidgetCreatorHelper getWidgetCreatorHelper(String widgetType)
	{
		if (!creatorHelpers.containsKey(widgetType))
		{
			if (getWidgetCreator(widgetType) == null)
			{
				return null;
			}
		}
		return creatorHelpers.get(widgetType);
	}

	/**
	 * Creates a new widget based on its meta-data element.
	 *
	 * @param printer
	 * @param metaElem
	 * @param widgetId
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType) throws CruxGeneratorException
	{
		return newWidget(printer, metaElem, widgetId, widgetType, this.widgetConsumer, true);
	}

	/**
	 * Creates a new widget based on its meta-data element.
	 *
	 * @param printer
	 * @param metaElem
	 * @param widgetId
	 * @param consumer
	 * @param allowWrapperForCreatedWidget
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType,
			WidgetConsumer consumer, boolean allowWrapperForCreatedWidget)
				throws CruxGeneratorException
	{
		WidgetCreator<?> widgetFactory = getWidgetCreator(widgetType);
		if (widgetFactory == null)
		{
			throw new CruxGeneratorException("Can not found widgetFactory for type: ["+widgetType+"].");
		}

		String widget;
		if (consumer != null && consumer instanceof LazyCompatibleWidgetConsumer && mustRenderLazily(widgetType, metaElem, widgetId))
		{
			String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
			lazyPanels.add(lazyPanelId);
			widget = lazyFactory.getLazyPanel(printer, metaElem, widgetId, LazyPanelWrappingType.wrapWholeWidget);
			consumer.consume(printer, lazyPanelId, widget);
			((LazyCompatibleWidgetConsumer)consumer).handleLazyWholeWidgetCreation(printer, widgetId);
		}
		else
		{
			widget = widgetFactory.createWidget(printer, metaElem, widgetId, consumer);
		}
		if (widget == null)
		{
			throw new CruxGeneratorException("Can not create widget ["+widgetId+"]. Verify the widget type.");
		}

		if (allowWrapperForCreatedWidget)
		{
			Class<?> widgetClass = getWidgetCreatorHelper(widgetType).getWidgetType();
			widget = getEventTapWrapperForHasClickWidget(printer, widget, widgetClass);
		}

		return widget;
	}

	/**
	 * Create a TapEventAdapter for touch devices, once click events has a delay to be fired
	 * @param printer
	 * @param widget
	 * @param widgetClass
	 * @return
	 */
	protected String getEventTapWrapperForHasClickWidget(SourcePrinter printer, String widget, Class<?> widgetClass)
    {
		if (getView().isTouchEventAdaptersEnabled())
		{
			Device currentDevice = Device.valueOf(getDevice());
			if (currentDevice.getInput().equals(Input.touch))
			{
				if (HasClickHandlers.class.isAssignableFrom(widgetClass) && HasAllTouchHandlers.class.isAssignableFrom(widgetClass))
				{
					String wrapperWidget = createVariableName("widget");
					printer.println(TapEventAdapter.class.getCanonicalName()+" "+wrapperWidget+" = new "+TapEventAdapter.class.getCanonicalName()+"("+widget+");" );
					widget = wrapperWidget;
				}
			}
		}
	    return widget;
    }

	/**
	 * Close the current postProcessing scope and schedule the execution of all scope commands.
	 *
	 * @param printer
	 */
	protected void commitPostProcessing(SourcePrinter printer)
	{
		PostProcessingPrinter postProcessingPrinter = this.postProcessingCode.removeLast();
		String postProcessingCode = postProcessingPrinter.toString();
		if (!StringUtils.isEmpty(postProcessingCode))
		{
			printer.println(Scheduler.class.getCanonicalName()+".get().scheduleDeferred(new "+
					ScheduledCommand.class.getCanonicalName()+"(){");
			printer.println("public void execute(){");
			printer.print(postProcessingCode);
			printer.println("}");
			printer.println("});");
		}
	}

	/**
	 * Create a new scope for the post processing commands. All commands added by
	 * {@code printlnPostProcessing} method will be added to this same scope, what means
	 * that they will be fired together. When {@code commitPostProcessing} method is called,
	 * the scope is closed and all scope commands are programmed for execution.
	 */
	protected void createPostProcessingScope()
	{
		this.postProcessingCode.add(new PostProcessingPrinter());
	}

	/**
	 * @return
	 */
	protected TreeLogger getLogger()
	{
		return this.logger;
	}

	/**
	 * Retrieves the view variable name
	 * @return
	 */
	public static String getViewVariable()
	{
		return viewVariable;
	}

	/**
	 * Retrieves the logger variable name
	 * @return
	 */
	protected String getLoggerVariable()
	{
		return loggerVariable;
	}

	/**
	 * @return
	 */
	protected View getView()
	{
		return this.view;
	}

	/**
     * Gets the code necessary to access a i18n declared property or the own property, if
     * it is not in declarative i18n format.
     *
	 * @param title
	 * @return
	 */
	protected String getDeclaredMessage(String property)
    {
	    if (isKeyReference(property))
	    {
			String[] messageParts = getKeyMessageParts(property);
			String messageClassName = MessageClasses.getMessageClass(messageParts[0]);

			// Checks if declared message is valid
			this.checkDeclaredMessage(messageClassName, messageParts[0], messageParts[1]);

			String messageVariable;

			if (!declaredMessages.containsKey(messageClassName))
			{
				messageVariable= createVariableName("mesg");
				declaredMessages.put(messageClassName, messageVariable);
			}
			else
			{
				messageVariable = declaredMessages.get(messageClassName);
			}
			return messageVariable+"."+messageParts[1]+"()";
	    }
	    else
	    {
	    	return property==null?null:EscapeUtils.quote(property);
	    }
    }

	/**
	 * Checks if declared message is valid
	 * @param messageClassName
	 * @param messageKey
	 * @param messageMethod
	 * @throws CruxGeneratorException
	 */
	private void checkDeclaredMessage(String messageClassName, String messageKey, String messageMethod) throws CruxGeneratorException
	{
		if (StringUtils.isEmpty(messageClassName))
		{
			throw new CruxGeneratorException("Message ["+messageKey+"] , declared on view ["+view.getId()+"], not found.");
		}
		else
		{
			JClassType messageClass = context.getTypeOracle().findType(messageClassName);
			if (messageClass == null)
			{
				String message = "Message class ["+messageKey+"] , declared on view ["+view.getId()+"], could not be loaded. "
							   + "\n Possible causes:"
							   + "\n\t 1. Check if any type or subtype used by message refers to another module and if this module is inherited in the .gwt.xml file."
							   + "\n\t 2. Check if your message or its members belongs to a client package."
							   + "\n\t 3. Check the versions of all your modules."
							   ;
				throw new CruxGeneratorException(message);
			}
			else
			{
				if (!hasMethod(messageClass, messageMethod, new JType[]{}))
				{
					throw new CruxGeneratorException("Method ["+messageMethod+"] of message ["+messageKey+"], declared on view ["+view.getId()+"], does not exist in message class ["+messageClassName+"].");
				}
			}
		}
	}

	/**
	 * Verifies if a method exists into a interface
	 * @param clazz
	 * @param methodName
	 * @param params
	 * @return
	 */
	private boolean hasMethod(JClassType clazz, String methodName, JType[] params)
	{
		if (clazz != null && methodName != null)
		{
			JMethod method = clazz.findMethod(methodName, params);
			if (method != null)
			{
				return true;
			}

			JClassType[] interfaces = clazz.getImplementedInterfaces();
			if (interfaces != null)
			{
				for (JClassType intf : interfaces)
				{
					if (hasMethod(intf, methodName, params))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

    /**
     * Returns <code>true</code> if the given text is an internationalization key.
	 * @param text
	 * @return <code>true</code> if the given text is an internationalization key.
	 */
	protected boolean isKeyReference(String text)
	{
		return text!= null && text.matches("\\$\\{\\w+\\.\\w+\\}");
	}

	/**
	 * Gets all messages declared on this screen
	 * @return
	 */
	protected Map<String, String> getDeclaredMessages()
	{
		return declaredMessages;
	}

	/**
	 * @param widgetId
	 * @return
	 */
	protected boolean containsWidget(String widgetId)
	{
		return view.getWidget(widgetId) != null;
	}

	/**
	 * @return
	 */
	protected GeneratorContextExt getContext()
	{
		return context;
	}

    /**
	 * Gets the list of classes used by the ViewFactory.
	 *
     * @return
     */
    String[] getImports()
    {
	    @SuppressWarnings("deprecation")
        String[] imports = new String[] {
    		GWT.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.screen.Screen.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName(),
    		StringUtils.class.getCanonicalName(),
    		Window.class.getCanonicalName(),
    		ViewFactoryUtils.class.getCanonicalName(),
    		ViewFactory.CreateCallback.class.getCanonicalName(),
    		RootPanel.class.getCanonicalName(),
    		RootLayoutPanel.class.getCanonicalName(),
    		Element.class.getCanonicalName(),
    		Node.class.getCanonicalName(),
    		org.cruxframework.crux.core.client.event.Event.class.getCanonicalName(),
    		ViewLoadEvent.class.getCanonicalName(),
    		Panel.class.getCanonicalName(),
    		InterfaceConfigException.class.getCanonicalName(),
    		Widget.class.getCanonicalName(),
    		Crux.class.getCanonicalName()
		};
	    return imports;
	}

	/**
	 * Create a new printer for a subType. That subType will be declared on the same package of the
	 * {@code ViewFactory}.
	 *
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @return
     */
    protected SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports)
    {
    	return getSubTypeWriter(subType, superClass, interfaces, imports, false);
    }

    /**
	 * Create a new printer for a subType. That subType will be declared on the same package of the
	 * {@code ViewFactory}.
	 *
     * @param subType
     * @param superClass
     * @param interfaces
     * @param imports
     * @param isInterface
     * @return
     */
    protected SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports, boolean isInterface)
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, subType);

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, subType);
		if (isInterface)
		{
			composerFactory.makeInterface();
		}

		if (imports != null)
		{
			for (String imp : imports)
			{
				composerFactory.addImport(imp);
			}
		}

		if (superClass != null)
		{
			composerFactory.setSuperclass(superClass);
		}

		if (interfaces != null)
		{
			for (String intf : interfaces)
			{
				composerFactory.addImplementedInterface(intf);
			}
		}

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}

	/**
	 * Check if the given metaElement refers to a valid widget
	 *
	 * @param element
	 * @return
	 */
    protected boolean isValidWidget(JSONObject metaElem)
	{
		String type =  metaElem.optString("_type");
		if (type != null && type.length() > 0 && !StringUtils.unsafeEquals("screen",type))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param context
	 * @param logger
	 * @param device
	 */
    protected void prepare(GeneratorContextExt context, TreeLogger logger, String device)
	{
		this.context = context;
		this.logger = logger;
		this.lazyPanels.clear();
		this.declaredMessages.clear();
		this.postProcessingCode.clear();
		this.device = device;
		this.rootPanelChildren.clear();
	}

	/**
	 *
	 * @return
	 */
    protected String getDevice()
	{
		return this.device;
	}

	/**
	 * Print code that will be executed after the viewFactory completes the widgets construction.
	 * Note that this code will be executed from inside a Command class. Any variable accessed in
	 * this code and declared outside need to be declared as final.
	 *
	 * @param s code string
	 */
    protected void printlnPostProcessing(String s)
	{
		this.postProcessingCode.getLast().println(s);
	}

	/**
	 * Generate the code for the View events creation
	 *
	 * @param printer
	 */
	protected void processViewEvents(SourcePrinter printer)
	{
		processHistoryChangedEvt(printer);
		processClosingEvt(printer);
		processCloseEvt(printer);
		processResizedEvt(printer);
		processLoadEvt(printer);
		processUnloadEvt(printer);
		processAttachEvt(printer);
		processDetachEvt(printer);
	}

	/**
	 * Processes the close event.
	 *
	 * @param printer
	 */
	private void processCloseEvt(SourcePrinter printer)
    {
	    Event onClose = view.getEvent("onClose");
		if (onClose != null)
		{
			printer.println(viewVariable+".addWindowCloseHandler(new "+CloseHandler.class.getCanonicalName()+"<Window>(){");
			printer.println("public void onClose("+CloseEvent.class.getCanonicalName()+"<Window> event){");

			EvtProcessor.printEvtCall(printer, onClose.getController()+"."+onClose.getMethod(), "onClose",
					CloseEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the closing event.
	 *
	 * @param printer
	 */
	private void processClosingEvt(SourcePrinter printer)
    {
	    Event onClosing = view.getEvent("onClosing");
		if (onClosing != null)
		{
			printer.println(viewVariable+".addWindowClosingHandler(new Window.ClosingHandler(){");
			printer.println("public void onWindowClosing("+ClosingEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onClosing.getController()+"."+onClosing.getMethod(), "onClosing",
						ClosingEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the historyChanged event.
	 *
	 * @param printer
	 */
	private void processHistoryChangedEvt(SourcePrinter printer)
    {
	    Event onHistoryChanged = view.getEvent("onHistoryChanged");
		if (onHistoryChanged != null)
		{
			printer.println(viewVariable+".addWindowHistoryChangedHandler(new "+ValueChangeHandler.class.getCanonicalName()+"<String>(){");
			printer.println("public void onValueChange("+ValueChangeEvent.class.getCanonicalName()+"<String> event){");

			EvtProcessor.printEvtCall(printer, onHistoryChanged.getController()+"."+onHistoryChanged.getMethod(),
					"onHistoryChanged", ValueChangeEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the load event.
	 *
	 * @param printer
	 */
	private void processLoadEvt(SourcePrinter printer)
	{
	    Event onLoad = view.getEvent("onLoad");
		if (onLoad != null)
		{
			printer.println(viewVariable+".addViewLoadHandler(new "+ViewLoadHandler.class.getCanonicalName()+"(){");
			printer.println("public void onLoad("+ViewLoadEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onLoad.getController()+"."+onLoad.getMethod(),
					"onLoad", ViewLoadEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the unload event.
	 *
	 * @param printer
	 */
	private void processUnloadEvt(SourcePrinter printer)
	{
	    Event onUnload = view.getEvent("onUnload");
		if (onUnload != null)
		{
			printer.println(viewVariable+".addViewUnloadHandler(new "+ViewUnloadHandler.class.getCanonicalName()+"(){");
			printer.println("public void onUnload("+ViewUnloadEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onUnload.getController()+"."+onUnload.getMethod(),
					"onUnload", ViewUnloadEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the attach event.
	 *
	 * @param printer
	 */
	private void processAttachEvt(SourcePrinter printer)
	{
	    Event onAttach = view.getEvent("onAttach");
		if (onAttach != null)
		{
			printer.println(viewVariable+".addViewAttachHandler(new "+ViewAttachHandler.class.getCanonicalName()+"(){");
			printer.println("public void onAttach("+ViewAttachEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onAttach.getController()+"."+onAttach.getMethod(),
					"onAttach", ViewAttachEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the attach event.
	 *
	 * @param printer
	 */
	private void processDetachEvt(SourcePrinter printer)
	{
	    Event onDetach = view.getEvent("onDetach");
		if (onDetach != null)
		{
			printer.println(viewVariable+".addViewDetachHandler(new "+ViewDetachHandler.class.getCanonicalName()+"(){");
			printer.println("public void onDetach("+ViewDetachEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onDetach.getController()+"."+onDetach.getMethod(),
					"onDetach", ViewDetachEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Processes the resized event.
	 *
	 * @param printer
	 * @return
	 */
	private Event processResizedEvt(SourcePrinter printer)
    {
	    Event onResized = view.getEvent("onResized");
		if (onResized != null)
		{
			printer.println("screen.addResizeHandler(new "+ResizeHandler.class.getCanonicalName()+"(){");
			printer.println("public void onResize("+ResizeEvent.class.getCanonicalName()+" event){");

			EvtProcessor.printEvtCall(printer, onResized.getController()+"."+onResized.getMethod(), "onResized",
					ResizeEvent.class, "event", context, view, getControllerAccessHandler());

			printer.println("}");
			printer.println("});");
		}
	    return onResized;
    }

	/**
	 * Generate the code for a widget creation, based on its metadata.
	 *
	 * @param printer
	 * @param metaElem
	 * @param widgetType
	 * @return
	 */
	private String createWidget(SourcePrinter printer, JSONObject metaElem, String widgetType)
	{
		if (!metaElem.has("id"))
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+view.getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}
		String widget;

		String widgetId = metaElem.optString("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new CruxGeneratorException("The id attribute is required for CRUX Widgets. " +
					"On page ["+view.getId()+"], there is an widget of type ["+widgetType+"] without id.");
		}

		widget = newWidget(printer, metaElem, widgetId, widgetType);
		if (isAttachToDOM(widgetType))
		{
			this.rootPanelChildren.add(widgetId);
		}
		return widget;
	}

	/**
	 *
	 * @param printer
	 */
	private void generateInitializeLazyDependenciesMethod(SourcePrinter printer)
    {
    	printer.println("protected native "+org.cruxframework.crux.core.client.collection.Map.class.getCanonicalName()+"<String> initializeLazyDependencies()/*-{");
    	printer.println("return "+view.getLazyDependencies().toString()+";");
    	printer.println("}-*/;");
    }

	/**
	 * @param printer
	 */
	private void generateCreateWidgetsMethod(SourcePrinter printer)
    {
	    createPostProcessingScope();
		printer.println("protected void createWidgets(){");

		JSONArray elementsMetaData = this.view.getElements();
		processViewEvents(printer);
		for (int i = 0; i < elementsMetaData.length(); i++)
		{
			JSONObject metaElement = elementsMetaData.optJSONObject(i);

			if (!metaElement.has("_type"))
			{
				throw new CruxGeneratorException("Crux Meta Data contains an invalid meta element (without type attribute). View ID["+view.getId()+"]");
			}
			String type = getMetaElementType(metaElement);
			if (!StringUtils.unsafeEquals("screen",type))
			{
				try
				{
					createWidget(printer, metaElement, type);
				}
				catch (Throwable e)
				{
					throw new CruxGeneratorException("Error Creating widget. See Log for more detail.", e);
				}
			}
		}

		printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
		printer.println(loggerVariable+".info(Crux.getMessages().viewContainerViewCreated(getId()));");
		printer.println("}");

		printer.println("}");
    }

	/**
	 * @param printer
	 */
	private void generateRenderMethod(SourcePrinter printer)
    {
	    String rootPanelVariable = createVariableName("rootPanel");
    	printer.println("protected void render("+Panel.class.getCanonicalName()+" "+rootPanelVariable+") throws InterfaceConfigException{");

		printer.println("if (this."+viewPanelVariable+" == null){");
		printer.println("this."+viewPanelVariable+" = new " +
				HTMLPanel.class.getCanonicalName() + "("+getViewHTML()+");");
		printer.println(rootPanelVariable+".add(this."+viewPanelVariable+");");
    	for (String widgetId : rootPanelChildren)
        {
    		printer.println("this."+viewPanelVariable+".add(widgets.get("+EscapeUtils.quote(widgetId)+"), " +
    				"ViewFactoryUtils.getEnclosingPanelId("+EscapeUtils.quote(widgetId)+", "+viewVariable+"));");
        }

		commitPostProcessing(printer);
		printer.println("}");
		printer.println("else {");
		printer.println(rootPanelVariable+".add(this."+viewPanelVariable+");");
		printer.println("}");

		printer.println("if ("+LogConfiguration.class.getCanonicalName()+".loggingIsEnabled()){");
		printer.println(loggerVariable+".info(Crux.getMessages().viewContainerViewRendered(getId()));");
		printer.println("}");

		printer.println("}");
    }

	private String getViewHTML()
    {
	    String html = EscapeUtils.quote(view.getHtml()).replace(ViewParser.CRUX_VIEW_PREFIX, "\"+"+getViewVariable()+".getPrefix()+\"");
		return html;
    }

	/**
	 * @param printer
	 */
	private void generateGetRegisteredControllersMethod(SourcePrinter printer)
    {
    	printer.println("public "+RegisteredControllers.class.getCanonicalName()+" getRegisteredControllers(){");
    	printer.println("return this.registeredControllers;");
    	printer.println("}");
    }

	/**
	 * @param printer
	 */
	private void generateCreateDataSourceMethod(SourcePrinter printer)
    {
    	printer.println("public "+DataSource.class.getCanonicalName()+"<?> createDataSource(String dataSource){");
    	printer.println("return this.registeredDataSources.getDataSource(dataSource);");
    	printer.println("}");
    }

	/**
	 * Split the i18n message and separate the messageClass alias from the message method
	 *
	 * @param text
	 * @return
	 */
	protected  String[] getKeyMessageParts(String text)
	{
		text = text.substring(2, text.length()-1);
		return text.split("\\.");
	}

	/**
	 * Return the qualified name of the ViewFactory class created for the associated screen
	 * @return
	 */
	public String getProxyQualifiedName()
    {
	    return ViewFactory.class.getPackage().getName() + "." + getProxySimpleName();
    }

	/**
	 * Return the simple name of the ViewFactory class created for the associated screen
	 * @return
	 */
	public String getProxySimpleName()
    {
		String className = view.getId()+"_"+this.device;
		className = className.replaceAll("[\\W]", "_");
		return className;
    }

    /**
	 * Creates and returns a new {@link SourceWriter}
     * @return a new {@link SourceWriter}
     */
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getProxySimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getProxySimpleName());

		composerFactory.setSuperclass(org.cruxframework.crux.core.client.screen.views.View.class.getCanonicalName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
	}

    /**
     * Returns <code>true</code> if widgets of the given type should be attached to DOM after instantiated.
	 * @param widgetType
	 * @return <code>true</code> if widgets of the given type should be attached to DOM after instantiated.
	 */
	private boolean isAttachToDOM(String widgetType)
	{
		if (!attachToDOMfactories.containsKey(widgetType))
		{
			DeclarativeFactory declarativeFactory = getWidgetCreator(widgetType).getClass().getAnnotation(DeclarativeFactory.class);
			attachToDOMfactories.put(widgetType, declarativeFactory.attachToDOM());
		}
		return attachToDOMfactories.get(widgetType);
	}

	/**
	 * Returns <code>true</code> if the given widget should be rendered lazily
	 * @param widgetType
	 * @param metaElem
	 * @param widgetId
	 * @return <code>true</code> if the given widget should be rendered lazily
	 */
	private boolean mustRenderLazily(String widgetType, JSONObject metaElem, String widgetId)
	{
		Class<?> widgetClass = getWidgetCreatorHelper(widgetType).getWidgetType();
		if (Panel.class.isAssignableFrom(widgetClass))
		{
			if (!metaElem.optBoolean("visible", true))
			{
				String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
				return !lazyPanels.contains(lazyPanelId);
			}
		}
		return false;
	}


    /**
     * Printer for code that should be executed after the screen creation.
     *
     * @author Thiago da Rosa de Bustamante
     */
    private static class PostProcessingPrinter
    {
		private StringBuilder builder = new StringBuilder();
		private String indentation = "";

    	/**
    	 * @see java.lang.Object#toString()
    	 */
    	public String toString()
    	{
    		return builder.toString();
    	}

    	/**
    	 * Indents the next line to be printed
    	 */
    	void indent()
    	{
    		indentation+="\t";
    	}

    	/**
    	 * Outdents the next line to be printed
    	 */
    	void outdent()
    	{
    		if (indentation.length() > 0)
    		{
    			indentation = indentation.substring(1);
    		}
    	}

    	/**
    	 * Prints a line of code into the output.
    	 * <li>If the line ends with <code>"{"</code>, indents the next line.</li>
    	 * <li>If the line ends with <code>"}"</code>, <code>"};"</code> or <code>"});"</code>, outdents the next line.</li>
    	 * @param s
    	 */
    	void println(String s)
    	{
    		String line = s.trim();

			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};") || line.endsWith("}-*/;"))
    		{
    			outdent();
    		}

    		builder.append(indentation+s+"\n");

    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}
    }

    private static class DefaultControllerAccessor implements ControllerAccessHandler
    {
		private final String viewVariable;

		public DefaultControllerAccessor(String viewVariable)
        {
			this.viewVariable = viewVariable;
        }

		public String getControllerExpression(String controller)
        {

	        return "(("+getControllerImplClassName(controller)+")"+viewVariable+".getRegisteredControllers().getController("
			+EscapeUtils.quote(controller)+"))";
        }

		public String getControllerImplClassName(String controller)
        {
			String controllerClass = ClientControllers.getController(controller);
	        return controllerClass + ControllerProxyCreator.CONTROLLER_PROXY_SUFFIX;
        }
    }

	/**
	 * @return the widgetConsumer
	 */
	WidgetConsumer getScreenWidgetConsumer()
	{
		return widgetConsumer;
	}
}
