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
package org.cruxframework.crux.core.client.screen.views;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.resources.Resource;

import com.google.gwt.resources.client.ClientBundle;



/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class LazyPanel extends com.google.gwt.user.client.ui.LazyPanel
{
	private boolean initialized = false;
	private final String lazyId;
	private final View view;
	private static FastMap<ClientBundle> resources = new FastMap<ClientBundle>();
	
	//TODO: inject Controller and other resources in order to run when this is a device adaptive widget.
	
	public LazyPanel(View view, String lazyId)
    {
		this.view = view;
		this.lazyId = lazyId;
    }
	
	/**
	 * @see com.google.gwt.user.client.ui.LazyPanel#ensureWidget()
	 */
	@Override
	public void ensureWidget()
	{
		if (!initialized)
		{
			view.cleanLazyDependentWidgets(lazyId);
			initialized = true;
		}
		super.ensureWidget();
	}

	/**
	 * Retrieve the client bundle associated with the given id. To map a client bundle interface to an identifier, use
	 * the {@link Resource} annotation
	 * @param id
	 * @return
	 */
	public static ClientBundle getResource(String id)
	{
		return resources.get(id);
	}
	
	/**
	 * 
	 * @param id
	 * @param resource
	 */
	protected static void addResource(String id, ClientBundle resource)
	{
		resources.put(id, resource);
	}
	
	/**
	 * Returns true if a resource associated with the given identifiers was loaded by application
	 * @param id
	 * @return
	 */
	public static boolean containsResource(String id)
	{
		return resources.containsKey(id);
	}
	
}
