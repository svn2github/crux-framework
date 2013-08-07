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
package org.cruxframework.crux.core.client.db;

import org.cruxframework.crux.core.client.db.indexeddb.IDBKeyRange;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class StringKeyRange extends KeyRange<String>
{
	protected StringKeyRange(IDBKeyRange idbKeyRange)
    {
		super(idbKeyRange);
    }
	
	public static StringKeyRange only(String key)
	{
		return new StringKeyRange(IDBKeyRange.only(key));
	}

	public static StringKeyRange lowerBound(String key, boolean open)
	{
		return new StringKeyRange(IDBKeyRange.lowerBound(key, open));
	}
	
	public static StringKeyRange lowerBound(String key)
	{
		return new StringKeyRange(IDBKeyRange.lowerBound(key));
	}

	public static StringKeyRange upperBound(String key, boolean open)
	{
		return new StringKeyRange(IDBKeyRange.upperBound(key, open));
	}

	public static StringKeyRange upperBound(String key)
	{
		return new StringKeyRange(IDBKeyRange.upperBound(key));
	}

	public static StringKeyRange bound(String startKey, String endKey, boolean startOpen, boolean endOpen)
	{
		return new StringKeyRange(IDBKeyRange.bound(startKey, endKey, startOpen, endOpen));
	}
	
	public static StringKeyRange bound(String startKey, String endKey)
	{
		return new StringKeyRange(IDBKeyRange.bound(startKey, endKey));
	}

	@Override
    public String getLower()
    {
	    return idbKeyRange.getLowerString();
    }

	@Override
    public String getUpper()
    {
	    return idbKeyRange.getUpperString();
    }
}
