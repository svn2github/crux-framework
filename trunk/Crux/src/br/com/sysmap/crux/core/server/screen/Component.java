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
package br.com.sysmap.crux.core.server.screen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represent a CRUX component at the application's server side. 
 * @author Thiago Bustamante
 */
public class Component implements Cloneable
{
	private int hashValue = 0;
	protected String id;
	protected String type;
	protected String property;
	protected String formatter;

	protected Map<String, Event> events = new HashMap<String, Event>();
	
	
	public Component() 
	{
	}

	public Component(String id) 
	{
		this.id = id;
	}

	protected void addEvent(Event event)
	{
		if (event != null)
		{
			events.put(event.getId(), event);
		}
	}
	
	public Event getEvent(String evtId)
	{
		return events.get(evtId);
	}
	
	public Iterator<Event> iterateEvents()
	{
		return events.values().iterator();
	}
	
	public boolean equals(Object obj) 
	{
    	if (obj == null) return false;
    	if (!(obj instanceof Component)) return false;
    	
    	String compId1 = getId();
    	String compId2 = ((Component)obj).getId();
    	return (compId1 == null?compId2==null:compId1.equals(compId2));
    }

	public String getId() 
	{
		return id;
	}

	void setId(String id)
	{
		this.id = id;
	}
	
	public int hashCode()
    {
        if (this.hashValue == 0)
        {
            int result = 17;
            String compStr = this.getId();
            int idComp = compStr == null ? 0 : compStr.hashCode();
            result = result * 37 + idComp;
            this.hashValue = result;
        }
        return this.hashValue;
    }

	public String getType() 
	{
		return type;
	}

	void setType(String type) 
	{
		this.type = type;
	}

	public String getProperty() 
	{
		return property;
	}

	public void setProperty(String property) 
	{
		this.property = property;
	}

	public String getFormatter() 
	{
		return formatter;
	}

	public void setFormatter(String formatter) 
	{
		this.formatter = formatter;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException 
	{
		Component result = (Component)super.clone();
		result.events = new HashMap<String, Event>();
		for (String key : events.keySet()) 
		{
			result.addEvent((Event)events.get(key).clone());
		}
		
		return result;
	}
}
