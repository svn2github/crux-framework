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
import java.util.List;
import java.util.regex.Matcher;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Image implements Cloneable
{
	private String id;
	private String file;
	private boolean flipRtl;
	private boolean preventInlining;
	private RepeatStyle repeatStyle;
	private int width;
	private int height;
	private Size deviceSize;
	private Input deviceInput;
	private List<Definition> definitions = new ArrayList<Definition>();

	public Image(String id, String file, boolean flipRtl, boolean preventInlining, RepeatStyle repeatStyle, int width, int height, 
				Size deviceSize, Input deviceInput)
    {
		Matcher matcher = SimpleResource.id_pattern.matcher(id);

        if (matcher.find()) 
        {
        	throw new ResourcesException("Invalid identifier for image ["+id+"]");
        }

        this.id = id;
		this.file = file;
		this.flipRtl = flipRtl;
		this.preventInlining = preventInlining;
		this.repeatStyle = repeatStyle;
		this.width = width;
		this.height = height;
		this.deviceSize = deviceSize;
		this.deviceInput = deviceInput;
    }
	
	public String getId()
    {
    	return id;
    }

	public String getFile()
    {
    	return file;
    }

	public void setFile(String file)
	{
		this.file = file;
	}

	public Size getDeviceSize()
    {
    	return deviceSize;
    }

	public Input getDeviceInput()
    {
    	return deviceInput;
    }
	
	public boolean isFlipRtl()
    {
    	return flipRtl;
    }

	public void setFlipRtl(boolean flipRtl)
    {
    	this.flipRtl = flipRtl;
    }

	public void setPreventInlining(boolean preventInlining)
    {
    	this.preventInlining = preventInlining;
    }

	public boolean isPreventInlining()
    {
    	return preventInlining;
    }

	public int getWidth()
    {
    	return width;
    }

	public void setWidth(int width)
    {
		this.width = width;
    }

	public int getHeight()
    {
    	return height;
    }

	public void setHeight(int height)
    {
		this.height = height;
    }

	public RepeatStyle getRepeatStyle()
    {
    	return repeatStyle;
    }
	
	public void setRepeatStyle(RepeatStyle repeatStyle)
	{
		this.repeatStyle = repeatStyle;
		
	}

	public List<Definition> getDefinitions()
    {
    	return definitions;
    }
	
	public void addDefinition(Definition definition)
	{
		definitions.add(definition);
	}
	
	public static class Definition
	{
		private String file;
		private Boolean flipRtl;
		private Boolean preventInlining;
		private RepeatStyle repeatStyle;
		private int width;
		private int height;
		private Size deviceSize;
		private Input deviceInput;
		
		public Definition(String file, Boolean flipRtl, Boolean preventInlining, RepeatStyle repeatStyle, 
						int width, int height, Size deviceSize, Input deviceInput)
        {
			this.file = file;
			this.flipRtl = flipRtl;
			this.preventInlining = preventInlining;
			this.repeatStyle = repeatStyle;
			this.width = width;
			this.height = height;
			this.deviceSize = deviceSize;
			this.deviceInput = deviceInput;
        }
		
		public String getFile()
        {
        	return file;
        }
		public Size getDeviceSize()
        {
        	return deviceSize;
        }
		public Input getDeviceInput()
        {
        	return deviceInput;
        }
		public Boolean isFlipRtl()
	    {
	    	return flipRtl;
	    }
		public RepeatStyle getRepeatStyle()
	    {
	    	return repeatStyle;
	    }
		public Boolean isPreventInlining()
        {
        	return preventInlining;
        }
		public int getWidth()
        {
        	return width;
        }
		public int getHeight()
        {
        	return height;
        }
	}

	@Override
    public Image clone() throws CloneNotSupportedException
	{
	    return (Image) super.clone();
	}
	
	public static enum RepeatStyle
	{
		vertical, horizontal, both
	}
}
