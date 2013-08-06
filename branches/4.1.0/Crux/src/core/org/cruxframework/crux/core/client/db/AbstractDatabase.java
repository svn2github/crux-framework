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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.bean.JsonEncoder;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDatabase;
import org.cruxframework.crux.core.client.db.indexeddb.IDBFactory;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore.IDBObjectRetrieveRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBOpenDBRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBTransaction;
import org.cruxframework.crux.core.client.db.indexeddb.IDBTransaction.IDBTransactionMode;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBAbortEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBBlockedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBCompleteEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBObjectRetrieveEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBOpenedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBUpgradeNeededEvent;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * HTML5 AbstractDatabase based on IndexedDB (http://www.w3.org/TR/IndexedDB/) 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractDatabase implements Database
{
	protected static Logger logger = Logger.getLogger(AbstractDatabase.class.getName());
	protected IDBDatabase db = null;

	public void open(final DatabaseCallback callback)
	{
		IDBOpenDBRequest openDBRequest = IDBFactory.get().open(getName(), getVersion());
		openDBRequest.onSuccess(new IDBOpenedEvent.Handler()
		{
			@Override
			public void onSuccess(IDBOpenedEvent event)
			{
				db = event.getResult();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Database ["+getName()+"] opened.");
				}
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		});
		
		openDBRequest.onBlocked(new IDBBlockedEvent.Handler()
		{
			@Override
			public void onBlocked(IDBBlockedEvent event)
			{
				String message = "Database ["+getName()+"] is blocked.";//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
		
		openDBRequest.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = "Error opening Database ["+getName()+"]: " + event;//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
		openDBRequest.onUpgradeNeeded(new IDBUpgradeNeededEvent.Handler()
		{
			@Override
			public void onUpgradeNeeded(IDBUpgradeNeededEvent event)
			{
				db = event.getResult();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Browser is using an outdated Database ["+getName()+"]. Upgrading database structure.");//TODO i18n
				}
				updateDatabaseStructure();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Browser Database upgraded ["+getName()+"].");//TODO i18n
				}
			}
		});
	}

	public void close()
	{
		if (db != null)
		{
			db.close();
			db = null;
		}
	}
	
	public void delete(final DatabaseCallback callback)
	{
		IDBOpenDBRequest deleteDatabase = IDBFactory.get().deleteDatabase(getName());
		deleteDatabase.onSuccess(new IDBOpenedEvent.Handler()
		{
			@Override
			public void onSuccess(IDBOpenedEvent event)
			{
				db = null;
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		});
		deleteDatabase.onBlocked(new IDBBlockedEvent.Handler()
		{
			@Override
			public void onBlocked(IDBBlockedEvent event)
			{
				String message = "Database ["+getName()+"] is blocked.";//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
		deleteDatabase.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = "Error removing Database ["+getName()+"]: "+event;
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
	}

	public void add(Object object, DatabaseCallback callback)
	{
		add(new Object[]{object}, object.getClass(), callback);
	}
	
    public void add(Object[] objects, Class<?> objectType, final DatabaseCallback callback)
	{
		doAddOrPut(objects, objectType, callback, "add");
	}

	public void put(Object object, DatabaseCallback callback)
	{
		put(new Object[]{object}, object.getClass(), callback);
	}
	
    public void put(Object[] objects, Class<?> objectType, final DatabaseCallback callback)
	{
		doAddOrPut(objects, objectType, callback, "put");
	}

    public <T> void get(int key, Class<T> objectType, final DatabaseRetrieveCallback<T> callback)
    {
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readonly);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		IDBObjectRetrieveRequest retrieveRequest = objectStore.get(key);
		appendRetriveEventHandlers(callback, className, storeName, transaction, retrieveRequest);
    }

    public <T> void get(String key, Class<T> objectType, final DatabaseRetrieveCallback<T> callback)
    {
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readonly);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		IDBObjectRetrieveRequest retrieveRequest = objectStore.get(key);
		appendRetriveEventHandlers(callback, className, storeName, transaction, retrieveRequest);
    }

    public <T> void get(Object[] key, Class<T> objectType, final DatabaseRetrieveCallback<T> callback)
    {
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readonly);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		IDBObjectRetrieveRequest retrieveRequest = objectStore.get(getNativeCompositeKey(key, className));
		appendRetriveEventHandlers(callback, className, storeName, transaction, retrieveRequest);
    }

    public void delete(int key, Class<?> objectType, DatabaseCallback callback)
    {
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readwrite);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		objectStore.delete(key);
		appendWriteDBEventHandlers(callback, "delete", storeName, transaction);
    }

    public void delete(String key, Class<?> objectType, DatabaseCallback callback)
	{
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readwrite);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		objectStore.delete(key);
		appendWriteDBEventHandlers(callback, "delete", storeName, transaction);
	}
    
    public void delete(Object[] compositeKey, Class<?> objectType, DatabaseCallback callback)
    {
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readwrite);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		objectStore.delete(getNativeCompositeKey(compositeKey, className));
		appendWriteDBEventHandlers(callback, "delete", storeName, transaction);
    }
    
    private void checkDbOpened()
    {
	    if (db == null)
		{
			throw new DatabaseException("Database is not opened."); //i18n
		}
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void doAddOrPut(Object[] objects, Class<?> objectType, final DatabaseCallback callback, final String operation)
    {
	    checkDbOpened();
		final String className = objectType.getName();
		final String storeName = getObjectStoreName(className);
		if (storeName == null)
		{
			throw new DatabaseException("Can not found objectStore associated with class ["+className+"] on this database."); //i18n
		}
		IDBTransaction transaction = db.getTransaction(new String[]{storeName}, IDBTransactionMode.readwrite);
		IDBObjectStore objectStore = transaction.getObjectStore(storeName);
		JsonEncoder encoder = getEncoder(className);
		if (StringUtils.unsafeEquals("add", operation))
		{
			for (Object object : objects)
			{
				objectStore.add(encoder.toJavaScriptObject(object));
			}
		}
		else
		{
			for (Object object : objects)
			{
				objectStore.put(encoder.toJavaScriptObject(object));
			}
		}
		appendWriteDBEventHandlers(callback, operation, storeName, transaction);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
	private <T> void appendRetriveEventHandlers(final DatabaseRetrieveCallback<T> callback, 
			final String className, final String storeName, IDBTransaction transaction, 
			IDBObjectRetrieveRequest retrieveRequest)
    {
	    final JsonEncoder encoder = getEncoder(className);
		retrieveRequest.onSuccess(new IDBObjectRetrieveEvent.Handler()
		{
            @Override
			public void onSuccess(IDBObjectRetrieveEvent event)
			{
				callback.onSuccess((T) encoder.fromJavaScriptObject(event.getObject()));
			}
		});
		
		transaction.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = "Error executing transaction.  ObjectStore["+storeName+"], Database ["+getName()+"].";//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
		transaction.onAbort(new IDBAbortEvent.Handler()
		{
			@Override
			public void onAbort(IDBAbortEvent event)
			{
				String message = "Transaction aborted.  ObjectStore["+storeName+"], Database ["+getName()+"].";//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
    }

	private void appendWriteDBEventHandlers(final DatabaseCallback callback, final String operation, final String storeName, IDBTransaction transaction)
    {
	    transaction.onComplete(new IDBCompleteEvent.Handler()
		{
			@Override
			public void onComplete(IDBCompleteEvent event)
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Transaction completed for "+operation+". ObjectStore["+storeName+"], Database ["+getName()+"].");
				}
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		});
		transaction.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = "Error executing transaction.  ObjectStore["+storeName+"], Database ["+getName()+"].";//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
		transaction.onAbort(new IDBAbortEvent.Handler()
		{
			@Override
			public void onAbort(IDBAbortEvent event)
			{
				String message = "Transaction aborted.  ObjectStore["+storeName+"], Database ["+getName()+"].";//TODO i18n
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, message);
				}
				if (callback != null)
				{
					callback.onFailed(message);
				}
			}
		});
    }
	
	protected abstract void updateDatabaseStructure();
	protected abstract String getObjectStoreName(String className);
	protected abstract JsonEncoder<?> getEncoder(String className);
	protected abstract JsArrayMixed getNativeCompositeKey(Object[] key, String className);
}
