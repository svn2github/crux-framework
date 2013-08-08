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

import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectCountRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCountEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;

import com.google.gwt.core.client.GWT;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractObjectStore<K, V> implements ObjectStore<K, V>
{
	protected final IDBObjectStore idbObjectStore;
    
	protected DBMessages messages = GWT.create(DBMessages.class);

	protected AbstractObjectStore(IDBObjectStore idbObjectStore)
	{
		this.idbObjectStore = idbObjectStore;
		
	}
	
	public String[] getIndexNames()
	{
		return idbObjectStore.getIndexNames();
	}
	
	@Override
	public boolean istAutoIncrement()
	{
	    return idbObjectStore.istAutoIncrement();
	}
	
	@Override
	public void clear()
	{
		idbObjectStore.clear();
	}
	
	@Override
	public void count(final DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbObjectStore.count();
		handleCallback(callback, countRequest);
	}

	@Override
	public void count(KeyRange<K> range, final DatabaseCountCallback callback)
	{
		IDBObjectCountRequest countRequest = idbObjectStore.count(range.getNativeKeyRange());
		handleCallback(callback, countRequest);
	}

	private void handleCallback(final DatabaseCountCallback callback, IDBObjectCountRequest countRequest)
    {
	    countRequest.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				callback.onFailed(messages.objectStoreCountError(event.getName()));
			}
		});
		countRequest.onSuccess(new IDBCountEvent.Handler()
		{
			
			@Override
			public void onSuccess(IDBCountEvent event)
			{
				callback.onSuccess(event.getCount());
			}
		});
    }
}
