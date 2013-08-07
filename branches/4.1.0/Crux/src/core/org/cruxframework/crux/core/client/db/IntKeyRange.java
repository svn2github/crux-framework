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
public class IntKeyRange extends KeyRange<Integer>
{
	protected IntKeyRange(IDBKeyRange idbKeyRange)
    {
		super(idbKeyRange);
    }
	
	public static IntKeyRange only(int key)
	{
		return new IntKeyRange(IDBKeyRange.only(key));
	}

	public static IntKeyRange lowerBound(int key, boolean open)
	{
		return new IntKeyRange(IDBKeyRange.lowerBound(key, open));
	}
	
	public static IntKeyRange lowerBound(int key)
	{
		return new IntKeyRange(IDBKeyRange.lowerBound(key));
	}

	public static IntKeyRange upperBound(int key, boolean open)
	{
		return new IntKeyRange(IDBKeyRange.upperBound(key, open));
	}

	public static IntKeyRange upperBound(int key)
	{
		return new IntKeyRange(IDBKeyRange.upperBound(key));
	}

	public static IntKeyRange bound(int startKey, int endKey, boolean startOpen, boolean endOpen)
	{
		return new IntKeyRange(IDBKeyRange.bound(startKey, endKey, startOpen, endOpen));
	}
	
	public static IntKeyRange bound(int startKey, int endKey)
	{
		return new IntKeyRange(IDBKeyRange.bound(startKey, endKey));
	}

	@Override
    public Integer getLower()
    {
	    return idbKeyRange.getLowerInt();
    }

	@Override
    public Integer getUpper()
    {
	    return idbKeyRange.getUpperInt();
    }
}
