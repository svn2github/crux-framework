/*
 * Copyright 2013 cruxframework.org.
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

import java.util.Iterator;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.resources.Data;
import org.cruxframework.crux.core.rebind.screen.resources.ExternalText;
import org.cruxframework.crux.core.rebind.screen.resources.Resources;
import org.cruxframework.crux.core.rebind.screen.resources.SimpleResource;
import org.cruxframework.crux.core.rebind.screen.resources.Text;

import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class SimpleResourcesRebind
{
	private final View view;
	private final Device device;
	private final Resources resources;

	SimpleResourcesRebind(View view, Device device, Resources resources)
    {
		this.view = view;
		this.device = device;
		this.resources = resources;
    }
	
	void generateSimpleResourceMethods(SourcePrinter srcWriter)
    {
	    Iterator<Data> datas = resources.iterateDatas();
		while (datas.hasNext())
		{
			Data data = resolveSimleResource(datas.next());
			if (data != null)
			{
				srcWriter.println("@Source("+EscapeUtils.quote(data.getFile())+")");
				srcWriter.println(DataResource.class.getCanonicalName() + " " + data.getId() + "();");
				srcWriter.println();
			}
		}
		Iterator<Text> texts = resources.iterateTexts();
		while (texts.hasNext())
		{
			Text text = resolveSimleResource(texts.next());
			if (text != null)
			{
				srcWriter.println("@Source("+EscapeUtils.quote(text.getFile())+")");
				srcWriter.println(TextResource.class.getCanonicalName() + " " + text.getId() + "();");
				srcWriter.println();
			}
		}
		Iterator<ExternalText> externalTexts = resources.iterateExternalTexts();
		while (externalTexts.hasNext())
		{
			ExternalText externalText = resolveSimleResource(externalTexts.next());
			if (externalText != null)
			{
				srcWriter.println("@Source("+EscapeUtils.quote(externalText.getFile())+")");
				srcWriter.println(ExternalTextResource.class.getCanonicalName() + " " + externalText.getId() + "();");
				srcWriter.println();
			}
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
		
		resolveSimpleResourceDefinitions(resource, result);
		
		return result;
	}

	private <T extends SimpleResource> void resolveSimpleResourceDefinitions(T resource, T result)
    {
	    for (int i = 0; i < result.getDefinitions().size(); i++)
		{
			SimpleResource.Definition definition = result.getDefinitions().get(i);
		
			Size deviceSize = definition.getDeviceSize();
			Input deviceInput = definition.getDeviceInput();
			if (deviceSize == null && deviceInput == null)
			{
				throw new CruxGeneratorException("Invalid resource declaration on view ["+view.getId()+"]. You must inform a deviceSize or a deviceInput for a set declaration.");
			}
			
			int numMatches = 0;
			if (deviceSize != null)
			{
				if (device.getSize().equals(deviceSize))
				{
					numMatches++;
				}
				else
				{
					continue;
				}
			}
			if (deviceInput != null)
			{
				if (device.getInput().equals(deviceInput))
				{
					numMatches++;
				}
				else
				{
					continue;
				}
			}
			result.setFile(definition.getFile());
			if (numMatches==2)
			{
				break;
			}
		}
    }	
}
