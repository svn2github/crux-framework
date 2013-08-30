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
import com.google.gwt.core.client.JavaScriptException;
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
	public Transaction getTransaction(String[] storeNames, Transaction.Mode mode)
	{
		return getTransaction(storeNames, mode, null);
	}

    @Override
	public Transaction getTransaction(String[] storeNames, Transaction.Mode mode, TransactionCallback callback)
	{
		Transaction transaction = new Transaction(this, storeNames, mode);
		transaction.setTransactionCallback(callback);
		return transaction;
	}

    @Override
	public <V> void add(V[] objects, String objectStoreName, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<?, V> objectStore = transaction.getObjectStore(objectStoreName);
    	for (V object : objects)
        {
    		objectStore.add(object);
        }
	}

    @Override
	public <V> void put(V[] objects, String objectStoreName, final DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<?, V> objectStore = transaction.getObjectStore(objectStoreName);
    	for (V object : objects)
        {
    		objectStore.put(object);
        }
	}

    @Override
    public <K, V> void get(K key, String objectStoreName, final DatabaseRetrieveCallback<V> callback)
    {
    	Transaction transaction = null;
    	try
    	{
    		//according to http://www.w3.org/TR/IndexedDB/#widl-IDBObjectStore-get-IDBRequest-any-key
    		//this may throw a exception if the objectstore is not present
    		transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readOnly);	
    	} catch (JavaScriptException e)
		{
			if(e.getName().equals("NotFoundError"))
			{
				callback.onSuccess(null);
				return;
			}
		}
    	
    	ObjectStore<K, V> objectStore = transaction.getObjectStore(objectStoreName);
    	objectStore.get(key, callback);
    }

    @Override
    public <K> void delete(K key, String objectStoreName, DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, ?> objectStore = transaction.getObjectStore(objectStoreName);
    	objectStore.delete(key);
	}
        
    @Override
    public <K> void delete(KeyRange<K> keys, String objectStoreName, DatabaseCallback callback)
	{
    	Transaction transaction = getTransaction(new String[]{objectStoreName}, Transaction.Mode.readWrite, getCallbackForWriteTransaction(callback));
    	ObjectStore<K, ?> objectStore = transaction.getObjectStore(objectStoreName);
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
	protected abstract <K, V> ObjectStore<K, V> getObjectStore(String storeName, IDBObjectStore idbObjectStore);
}
