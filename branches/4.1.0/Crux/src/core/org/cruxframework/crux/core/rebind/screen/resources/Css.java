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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Css
{
	private String id;
	private String file;
	private String importPrefix;
	private Size deviceSize;
	private Input deviceInput;
	private List<Definition> definitions = new ArrayList<Definition>();
	private Map<String, Alias> aliases = new HashMap<String, Alias>();

	public Css(String id, String file, String importPrefix, Size deviceSize, Input deviceInput)
    {
        Matcher matcher = SimpleResource.id_pattern.matcher(id);

        if (matcher.find()) 
        {
        	throw new ResourcesException("Invalid identifier for image ["+id+"]");
        }
		this.id = id;
		this.file = file;
		this.importPrefix = importPrefix;
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

	public String getImportPrefix()
    {
    	return importPrefix;
    }

	public Size getDeviceSize()
    {
    	return deviceSize;
    }

	public Input getDeviceInput()
    {
    	return deviceInput;
    }
	public List<Definition> getDefinitions()
    {
    	return definitions;
    }
	
	public Map<String, Alias> getAliases()
    {
    	return aliases;
    }

	public void addDefinition(Definition definition)
	{
		definitions.add(definition);
	}
	
	public void addAlias(Alias alias)
	{
		aliases.put(alias.getName(), alias);
	}

	public static class Definition
	{
		private String file;
		private String importPrefix;
		private Size deviceSize;
		private Input deviceInput;
		
		public Definition(String file, String importPrefix, Size deviceSize, Input deviceInput)
        {
			this.file = file;
			this.importPrefix = importPrefix;
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
		public String getImportPrefix()
        {
        	return importPrefix;
        }
	}
	
	public static class Alias
	{
		private String name;
		private String className;
		private Size deviceSize;
		private Input deviceInput;
		private List<Alias.Definition> definitions = new ArrayList<Alias.Definition>();
		
		public Alias(String name, String className, Size deviceSize, Input deviceInput)
        {
			this.name = name;
			this.className = className;
			this.deviceSize = deviceSize;
			this.deviceInput = deviceInput;
        }
		
		public Size getDeviceSize()
        {
        	return deviceSize;
        }
		public Input getDeviceInput()
        {
        	return deviceInput;
        }
		public String getName()
        {
        	return name;
        }
		public String getClassName()
        {
        	return className;
        }
		public List<Alias.Definition> getDefinitions()
	    {
	    	return definitions;
	    }
		public void addDefinition(Alias.Definition definition)
		{
			definitions.add(definition);
		}
		
		public static class Definition
		{
			private String className;
			private Size deviceSize;
			private Input deviceInput;
			
			public Definition(String className, Size deviceSize, Input deviceInput)
	        {
				this.className = className;
				this.deviceSize = deviceSize;
				this.deviceInput = deviceInput;
	        }
			
			public Size getDeviceSize()
	        {
	        	return deviceSize;
	        }
			public Input getDeviceInput()
	        {
	        	return deviceInput;
	        }
			public String getClassName()
	        {
	        	return className;
	        }
		}
	}
}
