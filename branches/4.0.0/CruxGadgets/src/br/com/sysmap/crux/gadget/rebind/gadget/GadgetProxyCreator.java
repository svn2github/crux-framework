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
package br.com.sysmap.crux.gadget.rebind.gadget;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.scanner.module.Module;
import br.com.sysmap.crux.core.rebind.scanner.module.Modules;
import br.com.sysmap.crux.core.rebind.scanner.screen.Screen;
import br.com.sysmap.crux.core.rebind.scanner.screen.ScreenFactory;
import br.com.sysmap.crux.core.rebind.scanner.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.rebind.scanner.screen.Widget;
import br.com.sysmap.crux.core.utils.HTMLUtils;
import br.com.sysmap.crux.core.utils.XMLUtils;
import br.com.sysmap.crux.gadget.client.Gadget;
import br.com.sysmap.crux.gadget.client.widget.GadgetView.View;
import br.com.sysmap.crux.gadget.meta.GadgetFeature.ContainerFeature;
import br.com.sysmap.crux.gadget.meta.GadgetFeature.Feature;
import br.com.sysmap.crux.gadget.meta.GadgetFeature.NeedsFeatures;
import br.com.sysmap.crux.gadget.meta.GadgetInfo.ModulePrefs;
import br.com.sysmap.crux.gadget.meta.GadgetInfo.UserPreferences;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.gadgets.client.UserPreferences.DataType;
import com.google.gwt.gadgets.client.UserPreferences.Preference;
import com.google.gwt.gadgets.client.UserPreferences.PreferenceAttributes;
import com.google.gwt.gadgets.rebind.PreferenceGenerator;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetProxyCreator extends AbstractInterfaceWrapperProxyCreator
{
	private Set<String> neededFeatures = new HashSet<String>();
	private JClassType moduleMetaClass;
	
	/**
	 * Constructor
	 * @param logger
	 * @param context
	 */
	public GadgetProxyCreator(TreeLogger logger, GeneratorContext context)
    {
	    super(logger, context, context.getTypeOracle().findType(Gadget.class.getCanonicalName()));
		try
		{
			br.com.sysmap.crux.core.rebind.scanner.screen.Screen screen = getRequestedScreen();
			Module module = Modules.getInstance().getModule(screen.getModule());
			moduleMetaClass = baseIntf.getOracle().getType(module.getFullName());
			generateGadgetManifestFile();
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Error generating gadget proxy", e);//TODO message here
			throw new CruxGeneratorException();
		}
    }
	
	/**
	 * @throws UnableToCompleteException
	 */
	private void generateGadgetManifestFile() throws UnableToCompleteException
    {
		String manifestName = moduleMetaClass.getQualifiedSourceName();
		if (!GadgetUtils.useLongManifestName(logger, context.getTypeOracle(), moduleMetaClass))
		{
			int lastIndex = manifestName.lastIndexOf('.');
			if (lastIndex != -1)
			{
				manifestName = manifestName.substring(lastIndex + 1);
			}
		}
		OutputStream manifestOut = context.tryCreateResource(logger, manifestName + ".gadget.xml");
		if (manifestOut == null)
		{
			logger.log(TreeLogger.ERROR, "Gadget manifest was already created", null);// TODO message here
			throw new UnableToCompleteException();
		}
		generateGadgetManifest(new PrintWriter(new OutputStreamWriter(manifestOut)));
		context.commitResource(logger, manifestOut);
	}

	/**
	 * @param printWriter
	 * @throws UnableToCompleteException 
	 */
	private void generateGadgetManifest(PrintWriter out) throws UnableToCompleteException
    {
	    logger.log(TreeLogger.DEBUG, "Building gadget manifest", null);

		Document d;
		LSSerializer serializer;
		LSOutput output;

		try
		{
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementation impl = registry.getDOMImplementation("Core 3.0");
			d = impl.createDocument(null, null, null);
			DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
			output = implLS.createLSOutput();
			output.setCharacterStream(out);
			serializer = implLS.createLSSerializer();
		}
		catch (Exception e)
		{
			logger.log(TreeLogger.ERROR, "Could not create document", e);// TODO message
			throw new UnableToCompleteException();
		}

		Element module = (Element) d.appendChild(d.createElement("Module"));
		Element modulePrefs = (Element) module.appendChild(d.createElement("ModulePrefs"));	    
	    
	    generateModulePreferences(d, modulePrefs);
		generateUserPreferences(d, module);
		generateFeaturesList(d, modulePrefs);
		generateContentSections(d, module);
		
	    serializer.write(d, output);
    }

	/**
	 * @param d
	 * @param module
	 * @throws UnableToCompleteException 
	 */
	private void generateContentSections(Document d, Element module) throws UnableToCompleteException
    {
	    //TODO boolean quirksMode = GadgetUtils.allowHtmlQuirksMode(logger, context.getTypeOracle(), this.moduleMetaClass);
	    
    	try
        {
	        Set<String> screenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(getRequestedScreen().getModule());
	        for (String screenId : screenIDs)
            {
				InputStream stream = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenXMLResource(screenId);
				Document screenElement = XMLUtils.createNSUnawareDocument(stream);
				Screen screen = ScreenFactory.getInstance().getScreen(screenId);
				module.appendChild(getContentElement(d, screenElement, screen));
            }
        }
        catch (Exception e)
        {
			logger.log(TreeLogger.ERROR, "Could not retrieve screen ids", e);// TODO message
			throw new UnableToCompleteException();
        }
    }

	/**
	 * @param d
	 * @param screenDocument
	 * @param screen 
	 * @return
	 * @throws IOException
	 */
	private Element getContentElement(Document d, Document screenDocument, Screen screen) throws IOException
	{
		Element html = screenDocument.getDocumentElement();
		StringWriter out = new StringWriter();
		
		DocumentType doctype = screenDocument.getDoctype();
		if (doctype != null)
		{
			out.write("<!DOCTYPE " + doctype.getName() + ">\n");
		}
		
		NodeList children = html.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) child;
				String nodeName = element.getNodeName();
				if (nodeName.equalsIgnoreCase("body"))
				{
					getBodyContent(out, element);
				}
				else if (nodeName.equalsIgnoreCase("head"))
				{
					getHeadContent(out, element);
				}
			}
		}
		
		Element content = d.createElement("Content");
		content.setAttribute("type", "html");

		Widget gadgetViewWidget = getGadgetViewWidget(screen);
		String viewName = gadgetViewWidget.getProperty("view");
		if (!StringUtils.isEmpty(viewName) && !viewName.equals(View.noViews.toString())) 
		{
			content.setAttribute("view", viewName);
			/*
			 * Add a piece of JS code that sets the name of this view. We use this to
			 * select the right code. Thus we don't have to rely on the views feature
			 * being present. We could use gadget.views.* functionality, but we
			 * shouldn't enforce the "views" feature to be present, as it adds API
			 * that the gadget doesn't use.
			 */
			out.write("<script>window.gadgetViewName = '"+ viewName.replace("\'", "\\\'") + "';</script>");
		}

	    content.appendChild(d.createCDATASection(out.toString() + "__BOOTSTRAP__"));
		return content;
	}

	/**
	 * @param screen
	 * @return
	 */
	private Widget getGadgetViewWidget(Screen screen)
    {
	    Iterator<Widget> widgets = screen.iterateWidgets();
		while (widgets.hasNext())
		{
			Widget widget = widgets.next();
			if (widget.getType().equals("gadget_gadgetView"))
			{
				return widget;
			}
		}
		return null;
    }

	/**
	 * @param out
	 * @param element
	 * @throws IOException
	 */
	private void getBodyContent(StringWriter out, Element element) throws IOException
    {
	    NodeList bodyChildren = element.getChildNodes();
	    for(int j=0; j<bodyChildren.getLength(); j++)
	    {
	    	HTMLUtils.write(bodyChildren.item(j), out);
	    }
    }

	/**
	 * @param out
	 * @param element
	 * @throws IOException
	 */
	private void getHeadContent(StringWriter out, Element element) throws IOException
    {
	    NodeList headChildren = element.getChildNodes();
	    for (int j = 0; j < headChildren.getLength(); j++)
	    {
	    	Element item = (Element) headChildren.item(j);

	    	if (item.getNodeName().equalsIgnoreCase("script"))
	    	{
	    		String src = item.getAttribute("src");
	    		if (src == null || !src.endsWith(".nocache.js"))
	    		{
	    			HTMLUtils.write(item, out);
	    		}
	    	}
	    	else if (item.getNodeName().equalsIgnoreCase("link") || item.getNodeName().equalsIgnoreCase("style"))
	    	{
	    		HTMLUtils.write(item, out);
	    	}
	    }
    }
	
	/**
	 * Add required features to the manifest
	 * {@code <require feature="someFeature" />}
	 * @param d
	 * @param modulePrefs
	 */
	private void generateFeaturesList(Document d, Element modulePrefs)
    {
		NeedsFeatures needsFeature = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		
		if (needsFeature != null)
		{
			Feature[] features = needsFeature.value();
			if (features != null)
			{
				for (Feature feature : features)
                {
	                ContainerFeature containerFeature = feature.value();
	                
					Element require = (Element) modulePrefs.appendChild(d.createElement("Require"));
					require.setAttribute("feature", containerFeature.getFeatureName());
                }
			}
		}
    }

	/**
	 * @param d
	 * @param module
	 * @throws UnableToCompleteException 
	 */
	private void generateUserPreferences(Document d, Element module) throws UnableToCompleteException
    {
	    JClassType preferenceType = context.getTypeOracle().findType(Preference.class.getName().replace('$', '.'));
		assert preferenceType != null;

		JClassType prefsType = GadgetUtils.getUserPrefsType(logger, context.getTypeOracle(), moduleMetaClass);
		for (JMethod m : prefsType.getOverridableMethods())
		{
			Element userPref = (Element) module.appendChild(d.createElement("UserPref"));
			configurePreferenceElement(d, userPref, preferenceType, m);
		}
    }

	/**
	 * @param d
	 * @param userPref
	 * @param preferenceType
	 * @param m
	 * @throws UnableToCompleteException
	 */
	protected void configurePreferenceElement( Document d, Element userPref, JClassType preferenceType, JMethod m) throws UnableToCompleteException
	{
		logger.log(TreeLogger.DEBUG, "Generating userpref element for method " + m.getReadableDeclaration(), null);

		JClassType prefType = m.getReturnType().isClassOrInterface();

		if (prefType == null || !preferenceType.isAssignableFrom(prefType))
		{
			logger.log(TreeLogger.ERROR, m.getReturnType().getQualifiedSourceName() + " is not assignable to " + preferenceType.getQualifiedSourceName(), null);
			throw new UnableToCompleteException();
		}

		DataType dataType = prefType.getAnnotation(DataType.class);

		if (dataType == null)
		{
			logger.log(TreeLogger.ERROR, prefType + " must define a DataType annotation", null);
			throw new UnableToCompleteException();
		}

		userPref.setAttribute("name", m.getName());
		userPref.setAttribute("datatype", dataType.value());

		PreferenceAttributes attributes = m.getAnnotation(PreferenceAttributes.class);
		if (attributes != null)
		{
			GadgetUtils.writeAnnotationToElement(logger, attributes, userPref);

			switch (attributes.options())
			{
			case HIDDEN:
				userPref.setAttribute("datatype", "hidden");
				break;
			case NORMAL:
				break;
			case REQUIRED:
				userPref.setAttribute("required", "true");
				break;
			default:
				logger.log(TreeLogger.ERROR, "Unknown Option " + attributes.options().name(), null);
				throw new UnableToCompleteException();
			}
		}

		// Allow type-specific modifications to the userpref Element to be made
		PreferenceGenerator prefGenerator = GadgetUtils.getPreferenceGenerator(logger, prefType);
		prefGenerator.configurePreferenceElement(logger, d, userPref, prefType, m);
	}

	/**
	 * @param d
	 * @param modulePrefs
	 * @throws UnableToCompleteException 
	 */
	private void generateModulePreferences(Document d, Element modulePrefs) throws UnableToCompleteException
    {
	    ModulePrefs prefs = moduleMetaClass.getAnnotation(ModulePrefs.class);
	    if (prefs != null) 
	    {
	      GadgetUtils.writeAnnotationToElement(logger, prefs, modulePrefs, "requirements", "locales");
	      GadgetUtils.writeLocalesToElement(logger, d, modulePrefs, prefs.locales());
	    }
    }

	/**
	 * @param srcWriter
	 */
	protected void generateFeatureInitialization(SourceWriter srcWriter)
	{
		generateFeaturesInitialization(srcWriter, moduleMetaClass);
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	protected void generateFeaturesInitialization(SourceWriter srcWriter, JClassType moduleMetaClass)
	{
		NeedsFeatures needsFeatures = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		if (needsFeatures != null)
		{
			Feature[] features = needsFeatures.value();
			for (Feature feature : features)
			{
				initializeFeature(srcWriter, feature.value());
			}
		}
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyContructor(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyContructor(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("public " + getProxySimpleName() + "(){");
		srcWriter.indent();
		srcWriter.println("this.userPreferences = GWT.create("+UserPreferences.class.getSimpleName()+".class);");
		
		generateFeatureInitialization(srcWriter);
		
		srcWriter.outdent();
		srcWriter.println("}");
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyFields(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateProxyFields(SourceWriter srcWriter) throws CruxGeneratorException
    {
		srcWriter.println("private " + UserPreferences.class.getSimpleName() + " userPreferences = null;");
		
		for (ContainerFeature feature : ContainerFeature.values())
		{
			srcWriter.println("private " + feature.getClass().getCanonicalName() + " "+feature.toString()+"Feature = null;");
		}
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateSubTypes(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
    protected void generateSubTypes(SourceWriter srcWriter) throws CruxGeneratorException
    {
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractInterfaceWrapperProxyCreator#getImports()
	 */
	@Override
    protected String[] getImports()
    {
		String[] imports = new String[] {
				UserPreferences.class.getCanonicalName(),
				GWT.class.getCanonicalName(),
				Crux.class.getCanonicalName(),
				Window.class.getCanonicalName()
		};
		return imports;       
    }

	/**
	 * @param srcWriter
	 * @param feature
	 */
	private void initializeFeature(SourceWriter srcWriter, ContainerFeature feature)
	{
		srcWriter.println("this."+feature.toString()+"Feature = GWT.create("+feature.getClass()+".class);");
		neededFeatures.add(feature.getFeatureName());
	}
}
