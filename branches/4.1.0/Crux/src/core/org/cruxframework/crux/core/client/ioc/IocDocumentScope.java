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
package org.cruxframework.crux.core.client.ioc;

import org.cruxframework.crux.core.client.collection.FastMap;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
class IocDocumentScope extends IocScope
{
	private static FastMap<Object> values = new FastMap<Object>();
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(IocProvider<T> provider, String className)
    {
		T result = (T) values.get(className);
		if (result == null)
		{
			result = provider.get();
			values.put(className, result);
		}
	    return result;
    }
}
