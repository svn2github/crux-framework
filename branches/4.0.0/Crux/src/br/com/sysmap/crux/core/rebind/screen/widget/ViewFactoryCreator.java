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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.LazyPanelWrappingType;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ViewFactory;
import br.com.sysmap.crux.core.client.screen.ViewFactoryUtils;
import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessageClasses;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.screen.Event;
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.dev.generator.NameFactory;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ViewFactoryCreator
{
	private static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);
	private static NameFactory nameFactory = new NameFactory();
	private Map<String, Boolean> attachToDOMfactories = new HashMap<String, Boolean>();
	private GeneratorContext context;
	private Map<String, String> declaredMessages = new HashMap<String, String>();
	private Map<String, WidgetCreator<?>> factories = new HashMap<String, WidgetCreator<?>>();
	private Map<String, WidgetCreatorHelper> factoryHelpers = new HashMap<String, WidgetCreatorHelper>();
	private Map<String, Boolean> htmlContainersfactories = new HashMap<String, Boolean>();
	private final LazyPanelFactory lazyFactory;
	private final Set<String> lazyPanels = new HashSet<String>();	
    private TreeLogger logger;
	private final LinkedList<PostProcessingPrinter> postProcessingCode = new LinkedList<PostProcessingPrinter>();
	private final Screen screen;
	private String screenVariable;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param logger
	 * @param screen
	 */
	public ViewFactoryCreator(GeneratorContext context, TreeLogger logger, Screen screen)
    {
		this.logger = logger;
		this.context = context;
		this.screen = screen;
		this.lazyFactory = new LazyPanelFactory(this);
		this.screenVariable = createVariableName("screen");

    }	
	
	/**
	 * @param context
	 * @param logger
	 */
	void prepare(GeneratorContext context, TreeLogger logger)
	{
		this.context = context;
		this.logger = logger;
		this.lazyPanels.clear();
		this.declaredMessages.clear();
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
	 * Creates the ViewFactory class.
	 * 
	 * @return generated class name .
	 * @throws CruxGeneratorException 
	 */
	public String create() throws CruxGeneratorException
	{
		SourceWriter sourceWriter = getSourceWriter();
		if (sourceWriter == null)
		{
			return getQualifiedName();
		}
		SourcePrinter printer = new SourcePrinter(sourceWriter, logger);

		generateProxyMethods(printer);
		generateProxyFields(printer);

		printer.commit();
		return getQualifiedName();
	}	
	
	/**
	 * Generate the code for the Screen creation
	 * 
	 * @param printer 
	 */
	protected void createScreen(SourcePrinter printer) 
	{
		if (screen.getTitle() != null && screen.getTitle().length() >0)
		{
			printer.println("Window.setTitle("+EscapeUtils.quote(getDeclaredMessage(screen.getTitle()))+");" );
		}

		printer.println("final Screen "+screenVariable+" = Screen.get();");
		
		printer.println(screenVariable+".setDeclaredControllers("+extractReferencedResourceList(screen.iterateControllers())+");");
		printer.println(screenVariable+".setDeclaredDataSources("+extractReferencedResourceList(screen.iterateDataSources())+");");
		printer.println(screenVariable+".setDeclaredSerializables("+extractReferencedResourceList(screen.iterateSerializers())+");");
		printer.println(screenVariable+".setDeclaredFormatters("+extractReferencedResourceList(screen.iterateFormatters())+");");

		createHistoryChangedEvt(printer);
		createClosingEvt(printer);
		createCloseEvt(printer);
		createResizedEvt(printer);		
		createLoadEvt();
	}

	/**
	 * Generate the ViewFactory fields
	 * 
	 * @param printer
	 */
	protected void generateProxyFields(SourcePrinter printer)
    {
	    for (String messageClass: declaredMessages.keySet())
	    {
	    	printer.println("private "+messageClass+" "+declaredMessages.get(messageClass) + " = GWT.create("+messageClass+".class);");
	    }
    }
	
	/**
	 * Generate the ViewFactory methods.
	 * 
     * @param printer 
     * @throws CruxGeneratorException
     */
    protected void generateProxyMethods(SourcePrinter printer) throws CruxGeneratorException
    {
    	createPostProcessingScope();
    	printer.println("public void create() throws InterfaceConfigException{");
		
		JSONArray metaData = this.screen.getMetaData();
		createScreen(printer);
		for (int i = 0; i < metaData.length(); i++)
		{
			JSONObject metaElement = metaData.optJSONObject(i);

			if (!metaElement.has("_type"))
			{
				throw new CruxGeneratorException(messages.viewFactoryMetaElementDoesNotContainsType());
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
					throw new CruxGeneratorException(messages.viewFactoryGenericErrorCreateWidget(e.getLocalizedMessage()), e);
				}
			}
		}

		commitPostProcessing(printer);
		printer.println("}");
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
		return newWidget(printer, metaElem, widgetId, widgetType, true);
	} 	
	
	/**
	 * Creates a new widget based on its meta-data element.
	 *  
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param addToScreen
	 * @return
	 * @throws CruxGeneratorException
	 */
	protected String newWidget(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType, boolean addToScreen) 
				throws CruxGeneratorException
	{
		WidgetCreator<?> widgetFactory = getWidgetFactory(widgetType);
		if (widgetFactory == null)
		{
			throw new CruxGeneratorException(messages.viewFactoryWidgetFactoryNotFound(widgetType));
		}
		
		String widget;
		//TODO nao colocar na lista de lazyDeps qdo addToScreen for false
		if (addToScreen && mustRenderLazily(widgetType, metaElem, widgetId))
		{
			String lazyPanelId = ViewFactoryUtils.getLazyPanelId(widgetId, LazyPanelWrappingType.wrapWholeWidget);
			lazyPanels.add(lazyPanelId);
			widget = lazyFactory.getLazyPanel(printer, metaElem, widgetId, LazyPanelWrappingType.wrapWholeWidget);
			printer.println(screenVariable+".addWidget("+EscapeUtils.quote(lazyPanelId)+", "+widget+");");
		}
		else
		{
			widget = widgetFactory.createWidget(printer, metaElem, widgetId, addToScreen); 
		}
		if (widget == null)
		{
			throw new CruxGeneratorException(messages.screenFactoryErrorCreatingWidget(widgetId));
		}
		
		return widget;
	}
	
	/**
	 * Close the current postProcessing scope and schedule the execution of all scope commands.
	 * 
	 * @param printer
	 */
	void commitPostProcessing(SourcePrinter printer)
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
	void createPostProcessingScope()
	{
		this.postProcessingCode.add(new PostProcessingPrinter());
	}
	
	/**
     * Gets the code necessary to access a i18n declared property or the own property, if
     * it is not in declarative i18n format.
     * 
	 * @param title
	 * @return
	 */
	String getDeclaredMessage(String property)
    {
	    if (isKeyReference(property))
	    {
			String[] messageParts = getKeyMessageParts(property);
			String messageClassName = MessageClasses.getMessageClass(messageParts[0]);
			
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
			return messageVariable+".get"+Character.toUpperCase(messageParts[1].charAt(0))+messageParts[1].substring(1)+"()";
	    }
	    else
	    {
	    	return property;
	    }
    }
	
	/**
	 * Gets the list of classes used by the ViewFactory.
	 * 
     * @return
     */
    String[] getImports()
    {
	    String[] imports = new String[] {
    		GWT.class.getCanonicalName(), 
    		br.com.sysmap.crux.core.client.screen.Screen.class.getCanonicalName(),
    		StringUtils.class.getCanonicalName(), 
    		Window.class.getCanonicalName(),
    		ViewFactoryUtils.class.getCanonicalName(),
    		RootPanel.class.getCanonicalName(),
    		RootLayoutPanel.class.getCanonicalName(),
    		Element.class.getCanonicalName(),
    		Node.class.getCanonicalName(),
    		br.com.sysmap.crux.core.client.event.Event.class.getCanonicalName(),
    		Events.class.getCanonicalName(),
    		ScreenLoadEvent.class.getCanonicalName(), 
    		Panel.class.getCanonicalName(), 
    		InterfaceConfigException.class.getCanonicalName(), 
    		Widget.class.getCanonicalName()
		};
	    return imports;
	}
	
	/**
	 * Retrieves the screen variable name
	 * @return
	 */
	String getScreenVariable()
	{
		return screenVariable;
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
    SourcePrinter getSubTypeWriter(String subType, String superClass, String[] interfaces, String[] imports)
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, subType);

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, subType);

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
	boolean isValidWidget(JSONObject metaElem)
	{
		String type =  metaElem.optString("_type");
		if (type != null && type.length() > 0 && !StringUtils.unsafeEquals("screen",type))
		{
			return true;
		}
		return false;
	}	
	
	/**
	 * @param widgetId
	 * @return
	 */
	boolean containsWidget(String widgetId)
	{
		return screen.getWidget(widgetId) != null;
	}
	
	/**
	 * Print code that will be executed after the viewFactory completes the widgets construction.
	 * Note that this code will be executed from inside a Command class. Any variable accessed in 
	 * this code and declared outside need to be declared as final.
	 * 
	 * @param s code string
	 */
	void printlnPostProcessing(String s)
	{
		this.postProcessingCode.getLast().println(s);
	}

	/**
	 * Creates the close event.
	 * 
	 * @param printer 
	 */
	private void createCloseEvt(SourcePrinter printer)
    {
	    Event onClose = screen.getEvent("onClose");
		if (onClose != null)
		{
			String eventClose = createVariableName("eventClose");

			printer.println("final Event "+eventClose+" = Events.getEvent(\"onClose\", "+ EscapeUtils.quote(onClose.toString())+");");
			printer.println(screenVariable+".addWindowCloseHandler(new CloseHandler<Window>(){");
			printer.println("public void onClose(CloseEvent<Window> event){"); 
			printer.println("Events.callEvent("+eventClose+", event);");				
			printer.println("}");
			printer.println("});");
		}
    }
	
	/**
	 * Creates the closing event.
	 * 
	 * @param printer 
	 */
	private void createClosingEvt(SourcePrinter printer)
    {
	    Event onClosing = screen.getEvent("onClosing");
		if (onClosing != null)
		{
			String eventClosing = createVariableName("eventClosing");

			printer.println("final Event "+eventClosing+" = Events.getEvent(\"onClosing\", "+ EscapeUtils.quote(onClosing.toString())+");");
			printer.println(screenVariable+".addWindowClosingHandler(new Window.ClosingHandler(){");
			printer.println("public void onWindowClosing(ClosingEvent closingEvent){"); 
			printer.println("Events.callEvent("+eventClosing+", closingEvent);");
			printer.println("}");
			printer.println("});");
		}
    }
	
	
	/**
	 * Creates the historyChanged event.
	 * 
	 * @param printer 
	 */
	private void createHistoryChangedEvt(SourcePrinter printer)
    {
	    Event onHistoryChanged = screen.getEvent("onHistoryChanged");
		if (onHistoryChanged != null)
		{
			String eventHistory = createVariableName("eventHistory");

			printer.println("final Event "+eventHistory+" = Events.getEvent(\"onHistoryChanged\", "+ EscapeUtils.quote(onHistoryChanged.toString())+");");
			printer.println(screenVariable+".addWindowHistoryChangedHandler(new ValueChangeHandler<String>(){");
			printer.println("public void onValueChange(ValueChangeEvent<String> historyEvent){");
			printer.println("Events.callEvent("+eventHistory+", historyEvent);");
			printer.println("}");
			printer.println("});");
		}
    }

	/**
	 * Generate the code for an {@code HTMLContainer} widget creation and attach it to the page DOM.  
	 * 
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @return
	 */
	private String createHtmlContainerAndAttach(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType) 

	{
		String panelElement = createVariableName("panelElement");
		String parentElement =createVariableName("parentElement");
		String previousSibling = createVariableName("previousSibling");

		printer.println("Element "+panelElement+" = ViewFactoryUtils.getEnclosingPanelElement("+EscapeUtils.quote(widgetId)+");");
		printer.println("Element "+parentElement+" = "+panelElement+".getParentElement();");
		printer.println("Node "+previousSibling+" = "+panelElement+".getPreviousSibling();");

		String widget = newWidget(printer, metaElem, widgetId, widgetType);

		printer.println("if ("+previousSibling+" != null){");
		printer.println(parentElement+".insertAfter("+widget+".getElement(), "+previousSibling+");");
		printer.println("}");
		printer.println("else{");
		printer.println(parentElement+".appendChild("+widget+".getElement());");
		printer.println("}");
		printer.println("((HTMLContainer)"+widget+").onAttach();");
		printer.println("RootPanel.detachOnWindowClose("+widget+");");		
		return widget;
	}

	/**
	 * Creates the load event.
	 *  
	 * @param printer 
	 */
	private void createLoadEvt()
    {
	    Event onLoad = screen.getEvent("onLoad");
		if (onLoad != null)
		{
			String eventLoad = createVariableName("eventLoad");
			
			printlnPostProcessing("Event "+eventLoad+" = Events.getEvent(\"onLoad\", " + EscapeUtils.quote(onLoad.toString())+");");
			printlnPostProcessing("Events.callEvent("+eventLoad+", new ScreenLoadEvent());");
		}
    }

	/**
	 * @param attributes
	 * @return
	 */
	private String extractReferencedResourceList(Iterator<String> attributes)
	{
		StringBuilder result = new StringBuilder("new String[]{"); 

		boolean first = true;
		while (attributes.hasNext())
		{
			if (!first)
			{
				result.append(",");
			}
			first = false;
			result.append(EscapeUtils.quote(attributes.next().trim()));
		}
		result.append("}"); 
		return result.toString();
	}	
	
	/**
	 * Creates the resized event.
	 * 
	 * @param printer 
	 * @return
	 */
	private Event createResizedEvt(SourcePrinter printer)
    {
	    Event onResized = screen.getEvent("onResized");
		if (onResized != null)
		{
			String eventResized = createVariableName("eventResized");
			
			printer.println("final Event "+eventResized+" = Events.getEvent(\"onResized\", " + EscapeUtils.quote(onResized.toString())+");");
			printer.println("screen.addWindowResizeHandler(new ResizeHandler(){");
			printer.println("public void onResize(ResizeEvent event){"); 
			printer.println("Events.callEvent("+eventResized+", event);");
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
			throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
		}
		String widget;

		String widgetId = metaElem.optString("id");
		if (widgetId == null || widgetId.length() == 0)
		{
			throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
		}
		/*
			Widget widget = screen.getWidget(widgetId, false);//Does not fire any lazy initialization here.
			if (widget != null)
			{
				return widget;
			}*/
		//TODO: verificar se precisa disso ainda

		if (!isAttachToDOM(widgetType))
		{
			widget = newWidget(printer, metaElem, widgetType, widgetId);
		}
		else if (isHtmlContainer(widgetType))
		{
			widget = createHtmlContainerAndAttach(printer, metaElem, widgetId, widgetType);
		}
		else
		{
			widget = createWidgetAndAttach(printer, metaElem, widgetId, widgetType);
		}
		return widget;
	}	

	/**
	 * Generate the code for an widget creation and attach it to the page DOM.
	 * 
	 * @param printer 
	 * @param metaElem
	 * @param widgetId
	 * @param widgetType
	 * @return
	 */
	private String createWidgetAndAttach(SourcePrinter printer, JSONObject metaElem, String widgetId, String widgetType)
	{
		String panelElement = createVariableName("panelElement");
		String panel = createVariableName("panel");
		
		printer.println("Element "+panelElement+" = ViewFactoryUtils.getEnclosingPanelElement("+EscapeUtils.quote(widgetId)+");");

		Class<?> widgetClassType = getWidgetFactoryHelper(widgetType).getWidgetType();
		String widget = newWidget(printer, metaElem, widgetId, widgetType);
		printer.println("Panel "+panel+";");
		if (RequiresResize.class.isAssignableFrom(widgetClassType))
		{
			boolean hasSize = (WidgetCreator.hasWidth(metaElem) && WidgetCreator.hasHeight(metaElem));
			if (!hasSize)
			{
				printer.println("if (RootPanel.getBodyElement().equals("+panelElement+".getParentElement())){");
				printer.println(panel+" = RootLayoutPanel.get();");
				printer.println("}");
				printer.println("else{");
				printer.println(panel+" = RootPanel.get("+panelElement+".getId());");
				printer.println("}");
				printer.println("GWT.log(Crux.getMessages().screenFactoryLayoutPanelWithoutSize("+EscapeUtils.quote(widgetId)+"), null);");
			}
			else
			{
				printer.println(panel+" = RootPanel.get("+panelElement+".getId());");
			}
		}
		else
		{
			printer.println(panel+" = RootPanel.get("+panelElement+".getId());");
		}
		printer.println(panel+".add("+widget+");");
		return widget;
	}	
	
	/**
	 * Split the i18n message and separate the messageClass alias from the message method
	 * 
	 * @param text
	 * @return
	 */
	private String[] getKeyMessageParts(String text)
	{
		text = text.substring(2, text.length()-1);
		return text.split("\\.");
	}

	/**
	 * Return the qualified name of the ViewFactory class created for the associated screen
	 * @return
	 */
	private String getQualifiedName()
    {
	    return ViewFactory.class.getPackage().getName() + "." + getSimpleName();
    }
	
	/**
	 * Return the simple name of the ViewFactory class created for the associated screen
	 * @return
	 */
	private String getSimpleName()
    {
		String className = screen.getModule()+"_"+screen.getRelativeId(); 
		className = className.replaceAll("[\\W]", "_");
		return className;
    }

	/**
	 * Creates and returns a new {@link SourceWriter}
     * @return a new {@link SourceWriter}
     */
    private SourceWriter getSourceWriter()
    {
		String packageName = ViewFactory.class.getPackage().getName();
		PrintWriter printWriter = context.tryCreate(logger, packageName, getSimpleName());

		if (printWriter == null)
		{
			return null;
		}

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, getSimpleName());

		String[] imports = getImports();
		for (String imp : imports)
		{
			composerFactory.addImport(imp);
		}

		return composerFactory.createSourceWriter(context, printWriter);    
	}
	
	/**
	 * Returns the factory of the widgets of the given type.
	 * @param widgetType
	 * @return the factory of the widgets of the given type.
	 */
	protected WidgetCreator<?> getWidgetFactory(String widgetType)
	{
		try
        {
	        if (!factories.containsKey(widgetType))
	        {
	        	String factoryClassName = WidgetConfig.getClientClass(widgetType);
	        	Class<?> widgetFactory = Class.forName(factoryClassName);
	        	WidgetCreator<?> factory = (WidgetCreator<?>) widgetFactory.newInstance();
	        	factory.setViewFactory(this);
	        	factories.put(widgetType, factory);
	        	WidgetCreatorHelper creatorHelper = new WidgetCreatorHelper(widgetFactory);
				factoryHelpers.put(widgetType, creatorHelper);
	        }
        }
        catch (Exception e)
        {
        	throw new CruxGeneratorException(messages.viewFactoryErrorRetrievingWidgetFactory(widgetType),e);
        }
		return factories.get(widgetType);
	}
	
    /**
     * Returns a helper object to create the code of the widgets of the given type. 
	 * @param widgetType
	 * @return a helper object to create the code of the widgets of the given type. 
	 */
	protected WidgetCreatorHelper getWidgetFactoryHelper(String widgetType)
	{
		if (!factoryHelpers.containsKey(widgetType))
		{
			if (getWidgetFactory(widgetType) == null)
			{
				return null;
			}
		}
		return factoryHelpers.get(widgetType);
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
			DeclarativeFactory declarativeFactory = getWidgetFactory(widgetType).getClass().getAnnotation(DeclarativeFactory.class);
			attachToDOMfactories.put(widgetType, declarativeFactory.attachToDOM());
		}
		return attachToDOMfactories.get(widgetType);
	}

    /**
     * Returns <code>true</code> if the given widget type is an HTML container.
	 * @param widgetType
	 * @return <code>true</code> if the given widget type is an HTML container.
	 */
	private boolean isHtmlContainer(String widgetType)
	{
		if (!htmlContainersfactories.containsKey(widgetType))
		{
			DeclarativeFactory declarativeFactory = getWidgetFactory(widgetType).getClass().getAnnotation(DeclarativeFactory.class);
			htmlContainersfactories.put(widgetType, declarativeFactory.htmlContainer());
		}
		return htmlContainersfactories.get(widgetType);
	}
    
    /**
     * Returns <code>true</code> if the given text is an internationalization key.
	 * @param text
	 * @return <code>true</code> if the given text is an internationalization key.
	 */
	private boolean isKeyReference(String text)
	{
		return text.matches("\\$\\{\\w+\\.\\w+\\}");
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
		if (Panel.class.isAssignableFrom(getWidgetFactoryHelper(widgetType).getWidgetType()))
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
     * Printer for screen creation codes.
     * 
     * @author Thiago da Rosa de Bustamante
     */
    public static class SourcePrinter
    {
    	private final TreeLogger logger;
		private final SourceWriter srcWriter;

    	/**
    	 * Constructor
    	 * @param srcWriter
    	 * @param logger
    	 */
    	public SourcePrinter(SourceWriter srcWriter, TreeLogger logger)
        {
			this.srcWriter = srcWriter;
			this.logger = logger;
        }
    	
    	
    	/**
    	 * Flushes the printed code into a real output (file).
    	 */
    	public void commit()
    	{
    		srcWriter.commit(logger);
    	}
    	
    	/**
    	 * Indents the next line to be printed
    	 */
    	public void indent()
    	{
    		srcWriter.indent();
    	}
    	
    	/**
    	 * Outdents the next line to be printed
    	 */
    	public void outdent()
    	{
    		srcWriter.outdent();
    	}
    	
    	/**
    	 * Prints an in-line code.
    	 * @param s
    	 */
    	public void print(String s)
    	{
    		srcWriter.print(s);
    	}
    	
    	/**
    	 * Prints a line of code into the output. 
    	 * <li>If the line ends with <code>"{"</code>, indents the next line.</li>
    	 * <li>If the line ends with <code>"}"</code>, <code>"};"</code> or <code>"});"</code>, outdents the next line.</li>
    	 * @param s
    	 */
    	public void println(String s)
    	{
    		String line = s.trim();
    		
			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};"))
    		{
    			outdent();
    		}
			
    		srcWriter.println(s);
    		
    		if (line.endsWith("{"))
    		{
    			indent();
    		}
    	}
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
    		
			if (line.endsWith("}") || line.endsWith("});") || line.endsWith("};"))
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
}
