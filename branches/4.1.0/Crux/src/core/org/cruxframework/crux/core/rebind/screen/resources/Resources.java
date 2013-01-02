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
package org.cruxframework.crux.core.rebind.screen.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Resources implements Cloneable
{
	private String id;
	private boolean shared;
	private String packageName;
	private Size deviceSize;
	private Input deviceInput;
	
	private Map<String, Image> images = new HashMap<String, Image>();
	private Map<String, Text> texts = new HashMap<String, Text>();
	private Map<String, ExternalText> externalTexts = new HashMap<String, ExternalText>();
	private Map<String, Data> datas = new HashMap<String, Data>();
	private Map<String, Css> css = new HashMap<String, Css>();
	private List<Definition> definitions = new ArrayList<Definition>();

	public Resources(String id, String packageName, boolean shared, Size deviceSize, Input deviceInput)
    {
		this.id = id;
		this.packageName = packageName;
		this.shared = shared;
		this.deviceSize = deviceSize;
		this.deviceInput = deviceInput;
    }
	
	public String getId()
    {
    	return id;
    }

	public String getPackageName()
    {
    	return packageName;
    }

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
	public Size getDeviceSize()
    {
    	return deviceSize;
    }

	public Input getDeviceInput()
    {
    	return deviceInput;
    }

	public boolean isShared()
    {
    	return shared;
    }

	public Iterator<Image> iterateImages()
    {
    	return images.values().iterator();
    }

	public Iterator<Css> iterateCssResources()
    {
    	return css.values().iterator();
    }

	public Iterator<Text> iterateTexts()
    {
    	return texts.values().iterator();
    }

	public Iterator<ExternalText> iterateExternalTexts()
    {
    	return externalTexts.values().iterator();
    }

	public Iterator<Data> iterateDatas()
    {
    	return datas.values().iterator();
    }

	public List<Definition> getDefinitions()
    {
    	return definitions;
    }

	public void addDefinition(Definition definition)
	{
		definitions.add(definition);
	}

	public void addData(Data data)
	{
		datas.put(data.getId(), data);
	}
	
	public void addText(Text text)
	{
		texts.put(text.getId(), text);
	}
	
	public void addExternalText(ExternalText text)
	{
		externalTexts.put(text.getId(), text);
	}
	
	public void addImage(Image image)
	{
		images.put(image.getId(), image);
	}

	public void addCss(Css css)
	{
		this.css.put(css.getId(), css);
	}
	
	public static class Definition
	{
		private String packageName;
		private Size deviceSize;
		private Input deviceInput;
		
		public Definition(String packageName, Size deviceSize, Input deviceInput)
        {
			this.packageName = packageName;
			this.deviceSize = deviceSize;
			this.deviceInput = deviceInput;
        }
		
		public String getPackageName()
        {
        	return packageName;
        }
		public Size getDeviceSize()
        {
        	return deviceSize;
        }
		public Input getDeviceInput()
        {
        	return deviceInput;
        }
	}
	
	@Override
    public Resources clone() throws CloneNotSupportedException
	{
	    return (Resources) super.clone();
	}
	
}
