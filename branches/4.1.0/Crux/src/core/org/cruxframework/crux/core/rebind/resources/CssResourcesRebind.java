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
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.View;
import org.cruxframework.crux.core.rebind.screen.resources.Image;
import org.cruxframework.crux.core.rebind.screen.resources.Resources;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CssResourcesRebind
{
	private final View view;
	private final Device device;
	private final Resources resources;

	CssResourcesRebind(View view, Device device, Resources resources)
    {
		this.view = view;
		this.device = device;
		this.resources = resources;
    }

	void generateImageResourceMethods(SourcePrinter srcWriter)
    {
	    Iterator<Image> images = resources.iterateImages();
		while (images.hasNext())
		{
			Image image = resolveImageResource(images.next());
			if (image != null)
			{
				srcWriter.println("@Source("+EscapeUtils.quote(image.getFile())+")");
				if (image.isFlipRtl() || image.getRepeatStyle() != null || image.isPreventInlining() || image.getWidth() > -1 || image.getHeight() > -1)
				{
					srcWriter.print("@ImageOptions(flipRtl="+image.isFlipRtl()+", preventInlining="+image.isPreventInlining());
					if (image.getWidth() > -1)
					{
						srcWriter.println(", width="+image.getWidth());
					}
					if (image.getHeight() > -1)
					{
						srcWriter.println(", height="+image.getHeight());
					}
					srcWriter.println(")");
				}
				srcWriter.println(ImageResource.class.getCanonicalName() + " " + image.getId() + "();");
			}
		}
    }

	private Image resolveImageResource(Image resource)
	{
		Image result;
        try
        {
	        result = resource.clone();
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
		
		resolveImageDefinitions(resource, result);

		return result;
	}
	
	private void resolveImageDefinitions(Image resource, Image result)
    {
	    for (int i = 0; i < result.getDefinitions().size(); i++)
		{
			Image.Definition definition = result.getDefinitions().get(i);
		
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
			if (!StringUtils.isEmpty(definition.getFile()))
			{
				result.setFile(definition.getFile());
			}
			if (definition.getRepeatStyle() != null)
			{
				result.setRepeatStyle(definition.getRepeatStyle());
			}
			if (definition.isFlipRtl() != null)
			{
				result.setFlipRtl(definition.isFlipRtl());
			}
			if (definition.isPreventInlining() != null)
			{
				result.setPreventInlining(definition.isPreventInlining());
			}
			if (definition.getWidth() > -1)
			{
				result.setWidth(definition.getWidth());
			}
			if (definition.getHeight() > -1)
			{
				result.setHeight(definition.getHeight());
			}
			if (numMatches==2)
			{
				break;
			}
		}
    }
}
