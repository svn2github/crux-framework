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
package org.cruxframework.crux.widgets.rebind;

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.widgets.client.dynatabs.DynaTabs;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * Generates a invoker for calling existing controllers/methods on a tab that belongs to a DynaTabs object rendered in the current document.
 * @author Gesse S. F. Dafe
 */
@Legacy
@Deprecated
public class TabInvokerProxyCreator extends AbstractTabInvokerProxyCreator
{
	public TabInvokerProxyCreator(TreeLogger logger, GeneratorContext context, JClassType baseIntf)
    {
	    super(logger, context, baseIntf);
    }

	@Override
	protected String getTabMethodInvocationString()
	{
		return DynaTabs.class.getName() + ".invokeOnTab";
	}	
}