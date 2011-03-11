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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import br.com.sysmap.crux.core.rebind.screen.Screen;
import br.com.sysmap.crux.core.rebind.screen.ScreenFactory;
import br.com.sysmap.crux.core.rebind.screen.ScreenResourceResolverInitializer;
import br.com.sysmap.crux.core.rebind.screen.Widget;
import br.com.sysmap.crux.core.server.scan.ClassScanner;
import br.com.sysmap.crux.core.utils.HTMLUtils;
import br.com.sysmap.crux.core.utils.XMLUtils;
import br.com.sysmap.crux.gadget.client.Gadget;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.ContainerFeature;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.Feature;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.NeedsDynamicHeightFeature;
import br.com.sysmap.crux.gadget.client.meta.GadgetFeature.NeedsFeatures;
import br.com.sysmap.crux.gadget.client.meta.GadgetInfo;
import br.com.sysmap.crux.gadget.client.meta.GadgetInfo.ModulePrefs;
import br.com.sysmap.crux.gadget.client.widget.GadgetView.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.gadgets.client.UserPreferences.DataType;
import com.google.gwt.gadgets.client.UserPreferences.Preference;
import com.google.gwt.gadgets.client.UserPreferences.PreferenceAttributes;
import com.google.gwt.gadgets.rebind.GadgetUtils;
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
	public GadgetProxyCreator(TreeLogger logger, GeneratorContextExt context)
    {
	    super(logger, context, context.getTypeOracle().findType(Gadget.class.getCanonicalName()), false);
		try
		{
			Set<String> descriptorClasses = ClassScanner.searchClassesByInterface(GadgetInfo.class);
			if (descriptorClasses == null || descriptorClasses.size() != 1)
			{
				logger.log(TreeLogger.ERROR, "Error generating gadget proxy. You must declare a interface (only one) that implements the interface GadgetInfo");//TODO message here
				throw new CruxGeneratorException();
			}
			
			moduleMetaClass = baseIntf.getOracle().getType(descriptorClasses.iterator().next());
			if (moduleMetaClass.isInterface() == null)
			{
				logger.log(TreeLogger.ERROR, "Gadget Descriptor must be an interface");//TODO message here
				throw new CruxGeneratorException();
			}
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
		if (manifestOut != null)
		{
			generateGadgetManifest(new PrintWriter(new OutputStreamWriter(manifestOut)));
			context.commitResource(logger, manifestOut);
		}
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
			d = impl.createDocument(null, "Module", null);
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

		Element module = d.getDocumentElement();
		Element modulePrefs = (Element) module.appendChild(d.createElement("ModulePrefs"));	    
	    
	    generateModulePreferences(d, modulePrefs);
		generateUserPreferences(d, module);
		generateFeaturesList(d, modulePrefs, moduleMetaClass, new HashSet<String>());
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
    	try
        {
	        Set<String> screenIDs = ScreenResourceResolverInitializer.getScreenResourceResolver().getAllScreenIDs(getCurrentScreen().getModule());
	        for (String screenId : screenIDs)
            {
				InputStream stream = ScreenResourceResolverInitializer.getScreenResourceResolver().getScreenXMLResource(screenId);
				Document screenElement = XMLUtils.createNSUnawareDocument(stream);
				Screen screen = ScreenFactory.getInstance().getScreen(screenId);
				
				List<Widget> gadgetViewWidgets = getGadgetViewWidget(screen);
				for (Widget gadgetViewWidget : gadgetViewWidgets)
		        {
					module.appendChild(getContentElement(d, screenElement, gadgetViewWidget));
		        }
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
	private Element getContentElement(Document d, Document screenDocument, Widget gadgetViewWidget) throws IOException
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

		String viewName = gadgetViewWidget.getMetadata().optString("view");
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
	private List<Widget> getGadgetViewWidget(Screen screen)
    {
		List<Widget> result = new ArrayList<Widget>();
		
	    Iterator<Widget> widgets = screen.iterateWidgets();
		while (widgets.hasNext())
		{
			Widget widget = widgets.next();
			if (widget.getType().equals("gadget_gadgetView"))
			{
				result.add(widget);
			}
		}
		return result;
    }

	/**
	 * @param out
	 * @param element
	 * @throws IOException
	 */
	private void getBodyContent(StringWriter out, Element element) throws IOException
    {
	    NodeList bodyChildren = element.getChildNodes();
	    JClassType needsDynamicHeightFeatureType = context.getTypeOracle().findType(NeedsDynamicHeightFeature.class.getCanonicalName());
	    
	    boolean hasDynamicFeature = this.moduleMetaClass.isAssignableTo(needsDynamicHeightFeatureType);
	    
	    if (hasDynamicFeature)
	    {
	    	out.write("<div id=\"__gwt_gadget_content_div\">");
	    }
	    for(int j=0; j<bodyChildren.getLength(); j++)
	    {
	    	Node child = bodyChildren.item(j);
	    	if (child.getNodeType() != Node.ELEMENT_NODE || !isModuleScriptTag((Element) child))
	    	{
	    		HTMLUtils.write(child, out);
	    	}
	    }
	    if (hasDynamicFeature)
	    {
	    	out.write("</div>");
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
	    	
	    	Node child = headChildren.item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				Element item = (Element) child;

				String nodeName = item.getNodeName();
				if (!isModuleScriptTag(item) && (nodeName.equalsIgnoreCase("link") || nodeName.equalsIgnoreCase("style") || nodeName.equalsIgnoreCase("script") ))
				{
					HTMLUtils.write(item, out);
				}
			}
	    }
    }
	
	private boolean isModuleScriptTag(Element elem)
	{
		if (elem.getNodeName().equalsIgnoreCase("script"))
		{
			String src = elem.getAttribute("src");
			if (src != null && src.endsWith(".nocache.js"))
			{
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * Add required features to the manifest
	 * {@code <require feature="someFeature" />}
	 * @param d
	 * @param modulePrefs
	 */
	private void generateFeaturesList(Document d, Element modulePrefs, JClassType moduleMetaClass, Set<String> added)
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
	                if (!added.contains(containerFeature.getFeatureName()))
	                {
	                	Element require = (Element) modulePrefs.appendChild(d.createElement("Require"));
	                	require.setAttribute("feature", containerFeature.getFeatureName());

	                	added.add(containerFeature.getFeatureName());
	                }
                }
			}
		}
		
		JClassType[] interfaces = moduleMetaClass.getImplementedInterfaces();
		if (interfaces != null)
		{
			for (JClassType interfaceType : interfaces)
            {
				generateFeaturesList(d, modulePrefs, interfaceType, added);
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
		generateFeaturesInitialization(srcWriter, moduleMetaClass, new HashSet<String>());
	}

	/**
	 * @param srcWriter
	 * @param moduleMetaClass
	 */
	protected void generateFeaturesInitialization(SourceWriter srcWriter, JClassType moduleMetaClass, Set<String> added)
	{
		NeedsFeatures needsFeatures = moduleMetaClass.getAnnotation(NeedsFeatures.class);
		if (needsFeatures != null)
		{
			Feature[] features = needsFeatures.value();
			for (Feature feature : features)
			{
				if (!added.contains(feature.value().getFeatureName()))
				{
					initializeFeature(srcWriter, feature.value());
					added.add(feature.value().getFeatureName());
				}
			}
		}
		
		JClassType[] interfaces = moduleMetaClass.getImplementedInterfaces();
		if (interfaces != null)
		{
			for (JClassType interfaceType : interfaces)
            {
				generateFeaturesInitialization(srcWriter, interfaceType, added);
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
			Class<?> featureClass = feature.getFeatureClass();
			if (featureClass != null)
			{
				srcWriter.println("private " + featureClass.getCanonicalName() + " "+feature.toString()+"Feature = null;");
			}
		}
    }

	/**
	 * @see br.com.sysmap.crux.core.rebind.AbstractProxyCreator#generateProxyMethods(com.google.gwt.user.rebind.SourceWriter)
	 */
	@Override
	protected void generateProxyMethods(SourceWriter srcWriter) throws CruxGeneratorException
	{
		srcWriter.println("public " + UserPreferences.class.getSimpleName() + " getUserPreferences(){");
		srcWriter.indent();
		srcWriter.println("return userPreferences;");
		srcWriter.outdent();
		srcWriter.println("}");
		for (ContainerFeature feature : ContainerFeature.values())
		{
			
			Class<?> featureClass = feature.getFeatureClass();
			if (featureClass != null)
			{
				srcWriter.println("public " + featureClass.getCanonicalName() + " get"+featureClass.getSimpleName()+"(){");
				srcWriter.indent();
				srcWriter.println("return "+feature.toString()+"Feature;");
				srcWriter.outdent();
				srcWriter.println("}");
			}
		}
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
		srcWriter.println("this."+feature.toString()+"Feature = GWT.create("+feature.getFeatureClass().getCanonicalName()+".class);");
		neededFeatures.add(feature.getFeatureName());
	}
}
