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
package br.com.sysmap.crux.core.rebind.scanner.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represent a CRUX Widget at the application's server side. 
 * @author Thiago Bustamante
 */
public class Widget
{
	protected String dataSource;
	protected Map<String, Event> events = new HashMap<String, Event>();
	protected String formatter;
	protected String id;
	protected Widget parent = null;
	protected boolean visible = true;

	protected List<String> propertiesValues = new ArrayList<String>();
	protected String type;
	private int hashValue = 0;
	
	public Widget() 
	{
	}

	public Widget(String id) 
	{
		this.id = id;
	}

	public boolean equals(Object obj) 
	{
    	if (obj == null) return false;
    	if (!(obj instanceof Widget)) return false;
    	
    	String compId1 = getId();
    	String compId2 = ((Widget)obj).getId();
    	return (compId1 == null?compId2==null:compId1.equals(compId2));
    }

	public String getDataSource()
	{
		return dataSource;
	}
	
	public Event getEvent(String evtId)
	{
		return events.get(evtId);
	}
	
	public String getFormatter() 
	{
		return formatter;
	}
	
	public String getId() 
	{
		return id;
	}

	public Widget getParent()
	{
		return parent;
	}

	public String getType() 
	{
		return type;
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
	
	public Iterator<Event> iterateEvents()
	{
		return events.values().iterator();
	}

	public Iterator<String> iterateProperties()
	{
		return propertiesValues.iterator();
	}

	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	public void setFormatter(String formatter) 
	{
		this.formatter = formatter;
	}

	public boolean isVisible() 
	{
		return visible;
	}

	protected void addEvent(Event event)
	{
		if (event != null)
		{
			events.put(event.getId(), event);
		}
	}

	protected void addPropertyValue(String value)
	{
		propertiesValues.add(value);
	}

	void setId(String id)
	{
		this.id = id;
	}

	void setParent(Widget parent)
	{
		this.parent = parent;
	}

	void setType(String type) 
	{
		this.type = type;
	}

	void setVisible(boolean visible) 
	{
		this.visible = visible;
	}
	
	
}
