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

import org.cruxframework.crux.core.client.db.indexeddb.IDBCursor.IDBCursorDirection;
import org.cruxframework.crux.core.client.db.indexeddb.IDBCursorWithValue;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class Cursor<K, V>
{
	/**
	 * Direction for the cursor
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum CursorDirection 
	{
		next(IDBCursorDirection.next), 
		nextunique(IDBCursorDirection.nextunique),
		prev(IDBCursorDirection.prev), 
		prevunique(IDBCursorDirection.prevunique);
		
		private IDBCursorDirection direction;
		CursorDirection(IDBCursorDirection direction)
		{
			this.direction = direction;
		}
		protected IDBCursorDirection getNativeCursorDirection()
		{
			return direction;
		}
	}

	protected final IDBCursorWithValue idbCursor;

	protected Cursor(IDBCursorWithValue idbCursor)
	{
		this.idbCursor = idbCursor;
	}

	public void advance(int count)
	{
		idbCursor.advance(count);
	};
	
	public void continueCursor()
	{
		idbCursor.continueCursor();
	}

	public void delete()
	{
		idbCursor.delete();
	}

	public CursorDirection getDirection()
	{
		switch (idbCursor.getDirection())
        {
        	case next:
        		return CursorDirection.next;
        	case nextunique:
    	        return CursorDirection.nextunique;
        	case prev:
    	        return CursorDirection.prev;
        	default:
    	        return CursorDirection.prevunique;
        }
	}
	
	public abstract void update(V value);
	public abstract K getKey();
	public abstract V getValue();
	public abstract void continueCursor(K key);
}
