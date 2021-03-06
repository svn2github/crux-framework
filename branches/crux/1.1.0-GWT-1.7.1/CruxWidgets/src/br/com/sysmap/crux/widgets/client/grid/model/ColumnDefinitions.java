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
package br.com.sysmap.crux.widgets.client.grid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.sysmap.crux.widgets.client.AdvancedWidgetMessages;

import com.google.gwt.core.client.GWT;

/**
 * Represents the columns to be rendered by a grid widget.
 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
 */
public class ColumnDefinitions
{
	private List<ColumnDefinition> definitionsInOrder = new ArrayList<ColumnDefinition>();
	private Map<String, ColumnDefinition> definitionsByKey = new HashMap<String, ColumnDefinition>();
	
	/**
	 * Register a new column definition
	 * @param key
	 * @param definition
	 */
	public void add(String key, ColumnDefinition definition)
	{
		definitionsInOrder.add(definition);
		definitionsByKey.put(key, definition);
		definition.setKey(key);
	}
	
	/**
	 * Gets all registered columns definition
	 */
	List<ColumnDefinition> getDefinitions()
	{
		return definitionsInOrder;
	}
	
	/**
	 * Gets a registered column definition by its key
	 * @param key
	 */
	ColumnDefinition getDefinition(String key)
	{
		return definitionsByKey.get(key);
	}
	
	/**
	 * Gets the column definition index 
	 * @param key
	 */
	int getColumnIndex(String key)
	{
		return definitionsInOrder.indexOf(definitionsByKey.get(key));
	}
	
	/**
	 * Creates and returns a iterator for accessing column definitions in an ordered way
	 */
	public Iterator<ColumnDefinition> getIterator()
	{
		return new ColumnIterator<ColumnDefinition>(definitionsInOrder);
	}
		
	/**
	 * Iterator for the registered column definition
	 * @author Gess� S. F. Daf� - <code>gessedafe@gmail.com</code>
	 */
	public static class ColumnIterator<T extends ColumnDefinition> implements Iterator<T>
	{
		private AdvancedWidgetMessages messages = GWT.create(AdvancedWidgetMessages.class);
		
		int cursor = 0;
		private List<T> defs;
	
		/**
		 * Restrict constructor 
		 * @param definitions
		 */
		ColumnIterator(List<T> definitions)
		{
			this.defs = definitions;
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext()
		{
			return cursor < defs.size();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public T next()
		{
			if(hasNext())
			{
				return (defs.get(cursor++));
			}
			else
			{
				return null;
			}
		}

		/**
		 * Unsupported method. It's not possible unregister a columns definition.
		 * @throws UnsupportedOperationException 
		 */
		public void remove()
		{
			throw new UnsupportedOperationException(messages.removingColumnDefinitionByIterator());
		}		
	}
}