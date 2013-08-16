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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.db.Transaction.TransactionCallback;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDatabase;
import org.cruxframework.crux.core.client.db.indexeddb.IDBDeleteDBRequest;
import org.cruxframework.crux.core.client.db.indexeddb.IDBFactory;
import org.cruxframework.crux.core.client.db.indexeddb.IDBObjectStore;
import org.cruxframework.crux.core.client.db.indexeddb.IDBOpenDBRequest;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBBlockedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBDatabaseDeleteEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBErrorEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBOpenedEvent;
import org.cruxframework.crux.core.client.db.indexeddb.events.IDBUpgradeNeededEvent;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.GWT;
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
	protected DBMessages messages = GWT.create(DBMessages.class);
	protected DatabaseErrorHandler errorHandler;
	protected String name;
	protected int version; 

	@Override
	public String getName()
	{
	    return name;
	}
	
	@Override
	public void setName(String newName) throws DatabaseException
	{
		if (isOpen())
		{
			throw new DatabaseException(messages.databaseSetPropertyOnOpenDBError(getName()));
		}
		this.name = newName;
	}
	
	@Override
	public int getVersion()
	{
	    return version;
	}
	
	@Override
	public void setVersion(int newVersion) throws DatabaseException
	{
		if (isOpen())
		{
			throw new DatabaseException(messages.databaseSetPropertyOnOpenDBError(getName()));
		}
		this.version = newVersion;
	}
	
    @Override
	public void open(final DatabaseCallback callback)
	{
		if (StringUtils.isEmpty(getName()))
		{
			throw new DatabaseException(messages.databaseInvalidNameDBError(getName()));
		}
		final IDBOpenDBRequest openDBRequest = IDBFactory.get().open(getName(), getVersion());
		openDBRequest.onSuccess(new IDBOpenedEvent.Handler()
		{
			@Override
			public void onSuccess(IDBOpenedEvent event)
			{
				db = event.getResult();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, messages.databaseOpened(getName()));
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
				String message = messages.databaseBlocked(getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
		
		openDBRequest.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = messages.databaseOpenError(getName(), event.getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
		openDBRequest.onUpgradeNeeded(new IDBUpgradeNeededEvent.Handler()
		{
			@Override
			public void onUpgradeNeeded(IDBUpgradeNeededEvent event)
			{
				db = event.getResult();
				try
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.INFO, messages.databaseUpgrading(getName()));
					}
					updateDatabaseStructure(openDBRequest);
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.INFO, messages.databaseUpgraded(getName()));
					}
				}
				catch (RuntimeException e) 
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.SEVERE, messages.databaseUpgradeError(getName(), e.getMessage()), e);
					}
					throw e;
				}
			}
		});
	}

    @Override
	public void close()
	{
		if (isOpen())
		{
			db.close();
			db = null;
		}
	}
	
    @Override
	public void delete(final DatabaseCallback callback)
	{
		if (StringUtils.isEmpty(getName()))
		{
			throw new DatabaseException(messages.databaseInvalidNameDBError(getName()));
		}
		IDBDeleteDBRequest deleteDatabase = IDBFactory.get().deleteDatabase(getName());
		deleteDatabase.onSuccess(new IDBDatabaseDeleteEvent.Handler()
		{
			@Override
			public void onDelete(IDBDatabaseDeleteEvent event)
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
				String message = messages.databaseBlocked(getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
		deleteDatabase.onError(new IDBErrorEvent.Handler()
		{
			@Override
			public void onError(IDBErrorEvent event)
			{
				String message = messages.databaseDeleteError(getName(), event.getName());
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.SEVERE, message);
				}
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
		});
	}

    @Override
	public Transaction getTransaction(Class<?>[] objectTypes, Transaction.Mode mode)
	{
		return getTransaction(objectTypes, mode, null);
	}
	
    @Override
	public Transaction getTransaction(String[] storeNames, Transaction.Mode mode)
	{
		return getTransaction(storeNames, mode, null);
	}

    @Override
	public Transaction getTransaction(Class<?>[] objectTypes, Transaction.Mode mode, TransactionCallback callback)
	{
		List<String> storeNames = new ArrayList<String>();
		
		for(Class<?> objectType: objectTypes)
		{
			String storeName = getObjectStoreName(objectType);
			if (storeName == null)
			{
				throw new DatabaseException(messages.databaseObjectStoreNotFoundError(getName(), objectType.getName()));
			}
			storeNames.add(storeName);
		}
			
		return getTransaction(storeNames.toArray(new String[storeNames.size()]), mode, callback);
	}
	
    @Override
	public Transaction getTransaction(String[] storeNames, Transaction.Mode mode, TransactionCallback callback)
	{
		Transaction transaction = new Transaction(this, storeNames, mode);
		transaction.setTransactionCallback(callback);
		return transaction;
	}

	@SuppressWarnings("unchecked")
    @Override
    public <K, V> void add(V object, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new Class<?>[]{object.getClass()}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, V> objectStore = transaction.getObjectStore((Class<V>) object.getClass());
   		objectStore.add(object);
	}

    @Override
	public <K, V> void add(V[] objects, Class<V> objectType, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new Class<?>[]{objectType}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectType);
    	for (V object : objects)
        {
    		objectStore.add(object);
        }
	}

	@SuppressWarnings("unchecked")
    @Override
    public <K, V> void put(V object, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new Class<?>[]{object.getClass()}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, V> objectStore = transaction.getObjectStore((Class<V>) object.getClass());
   		objectStore.put(object);
	}

    @Override
	public <K, V> void put(V[] objects, Class<V> objectType, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new Class<?>[]{objectType}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectType);
    	for (V object : objects)
        {
    		objectStore.put(object);
        }
	}

    @Override
    public <K, V> void get(K key, Class<V> objectType, final DatabaseRetrieveCallback<V> callback)
    {
    	Transaction transaction = getTransaction(new Class<?>[]{objectType}, Transaction.Mode.readOnly);
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectType);
    	objectStore.get(key, callback);
    }

    @Override
    public <K, V> void delete(K key, Class<V> objectType, DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new Class<?>[]{objectType}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectType);
    	objectStore.delete(key);
	}
        
    @Override
    public <K, V> void delete(KeyRange<K> keys, Class<V> objectType, DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new Class<?>[]{objectType}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectType);
    	objectStore.delete(keys);
	}

    @Override
    public boolean isOpen()
    {
    	return db != null;
    }

    @Override
    public void setDefaultErrorHandler(DatabaseErrorHandler errorHandler)
    {
		this.errorHandler = errorHandler;
        
    }
    
	private TransactionCallback getCallbackForWriteTransaction(final DatabaseCallback callback)
    {
		if (callback == null && errorHandler == null)
		{
			return null;
		}
	    return new TransactionCallback()
		{
			@Override
			public void onError(String message)
			{
				if (callback != null)
				{
					callback.onError(message);
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(message);
				}
			}
			
			@Override
			public void onAbort()
			{
				if (callback != null)
				{
					callback.onError(messages.databaseTransactionAborted(getName()));
				}
				else if (errorHandler != null)
				{
					errorHandler.onError(messages.databaseTransactionAborted(getName()));
				}
			}
			
			@Override
			public void onComplete()
			{
				if (callback != null)
				{
					callback.onSuccess();
				}
			}
		};
    }

	protected abstract void updateDatabaseStructure(IDBOpenDBRequest openDBRequest);
	protected abstract <K, V> ObjectStore<K, V> getObjectStore(Class<V> objectType, IDBObjectStore idbObjectStore);
	protected abstract <K, V> ObjectStore<K, V> getObjectStore(String storeName, IDBObjectStore idbObjectStore);
	protected abstract String getObjectStoreName(Class<?> objectType);
}
