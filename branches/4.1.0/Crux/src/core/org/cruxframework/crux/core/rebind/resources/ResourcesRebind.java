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
package org.cruxframework.crux.core.rebind.resources;

import java.io.PrintWriter;
import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.resources.Data;
import org.cruxframework.crux.core.rebind.screen.resources.ExternalText;
import org.cruxframework.crux.core.rebind.screen.resources.Resources;
import org.cruxframework.crux.core.rebind.screen.resources.SimpleResource;
import org.cruxframework.crux.core.rebind.screen.resources.Text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.GeneratorContextExt;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ResourcesRebind extends AbstractProxyCreator
{ 
	private final Resources resources;
	private final View view;
	private final Device device;

	public ResourcesRebind(TreeLogger logger, GeneratorContextExt context, View view, String device, Resources resources)
    {
	    super(logger, context);
		this.view = view;
		this.device = Device.valueOf(device);
		this.resources = resources;
    }

	@Override
    protected void generateProxyMethods(SourcePrinter srcWriter) throws CruxGeneratorException
    {
		Iterator<Data> datas = resources.iterateDatas();
		while (datas.hasNext())
		{
			Data data = resolveSimleResource(datas.next());
			srcWriter.println("@Source("+data.getFile()+")");
			srcWriter.println(DataResource.class.getCanonicalName() + " " + data.getId() + "();");
		}
		Iterator<Text> texts = resources.iterateTexts();
		while (texts.hasNext())
		{
			Text text = resolveSimleResource(texts.next());
			srcWriter.println("@Source("+text.getFile()+")");
			srcWriter.println(TextResource.class.getCanonicalName() + " " + text.getId() + "();");
		}
		Iterator<ExternalText> externalTexts = resources.iterateExternalTexts();
		while (externalTexts.hasNext())
		{
			ExternalText externalText = resolveSimleResource(externalTexts.next());
			srcWriter.println("@Source("+externalText.getFile()+")");
			srcWriter.println(TextResource.class.getCanonicalName() + " " + externalText.getId() + "();");
		}
		
    }

	@SuppressWarnings("unchecked")
	private <T extends SimpleResource> T  resolveSimleResource(T resource)
	{
		
		
        T result;
        try
        {
	        result = (T) resource.clone();
        }
        catch (CloneNotSupportedException e)
        {
        	throw new CruxGeneratorException("Error processing resources.", e);
        }
		Size deviceSize = result.getDeviceSize();
		Input deviceInput = result.getDeviceInput();
		if (deviceSize != null && !device.getSize().equals(deviceSize))
		{
			return null;
		}
		if (deviceInput != null && !device.getInput().equals(deviceInput))
		{
			return null;
		}
		
		if (result.getDefinitions().size() > 1)
		{
			throw new CruxGeneratorException("Only one definition is allowed for text, data and externalText resources.");
		}
			
		if (result.getDefinitions().size() > 1)
		{
			SimpleResource.Definition definition = result.getDefinitions().get(0);
		
			if (definition.getDeviceSize() == null && definition.getDeviceInput() == null)
			{
				throw new CruxGeneratorException("Invalid resource declaration on view ["+view.getId()+"]. You must inform a deviceSize or a deviceInput for a set declaration.");
			}
			if (definition.getDeviceSize() != null && definition.getDeviceInput() != null)
			{
				throw new CruxGeneratorException("Invalid resource declaration on view ["+view.getId()+"]. You can not inform both deviceSize and deviceInput for a set declaration.");
			}
			
			result.setFile(resource.getFile());
		}
		
		return result;
	}
	
	@Override
    public String getProxyQualifiedName()
    {
	    return resources.getPackageName()+"."+getProxySimpleName();
    }

	@Override
	public String getProxySimpleName()
	{
		String className;
		if (resources.isShared())
		{
			className = view.getId() + "_" + resources.getId() + "_" + device; 
		}
		else
		{
			className = resources.getId() + "_" + device; 
		}
		className = className.replaceAll("[\\W]", "_");
		return "Resources_"+className;
	}

	@Override
    protected SourcePrinter getSourcePrinter()
    {
		String packageName = resources.getPackageName();
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
		composerFactory.makeInterface();
		composerFactory.addImplementedInterface(ClientBundle.class.getCanonicalName());

		return new SourcePrinter(composerFactory.createSourceWriter(context, printWriter), logger);
    }
	
	/**
	 * @return
	 */
    protected String[] getImports()
    {
	    String[] imports = new String[] {
	    	ClientBundle.class.getCanonicalName(),	
	    	Source.class.getCanonicalName(),
	    	ImageOptions.class.getCanonicalName(),
    		GWT.class.getCanonicalName()
		};
	    return imports;
    }
}
