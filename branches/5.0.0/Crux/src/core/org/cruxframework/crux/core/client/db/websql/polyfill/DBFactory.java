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
package org.cruxframework.crux.core.client.db.websql.polyfill;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.db.websql.SQLDatabase;
import org.cruxframework.crux.core.client.db.websql.SQLDatabaseFactory;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore.Callback;
import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBFactory
{
	private static Logger logger = Logger.getLogger(DBFactory.class.getName());
	private static int  DEFAULT_DB_SIZE = 5 * 1024 * 1024;
	private boolean initialized = false;
	private SQLDatabase systemDatabase;
	private int readyStores;
	
	public final DBOpenRequest open(final String name, final int version)
	{
		final DBOpenRequest request = DBOpenRequest.create();
		if (!initialized)
		{
			init(new InitializationCallback()
			{
				@Override
				public void onInitialize()
				{
					doOpen(request, name, version);
				}
			});
		}
		else
		{
			doOpen(request, name, version);
		}
		return request;
	}

	public final DBOpenRequest deleteDatabase(final String name)
	{
		final DBOpenRequest request = DBOpenRequest.create();
		if (!initialized)
		{
			init(new InitializationCallback()
			{
				@Override
				public void onInitialize()
				{
					doDelete(request, name);
				}
			});
		}
		else
		{
			doDelete(request, name);
		}
		return request;
	}
	
	public final int cmp(JsArrayMixed key1, JsArrayMixed key2)
	{
		return DBUtil.encodeKey(key1).compareTo(DBUtil.encodeKey(key2));
	}
	
	private final void init(final InitializationCallback callback)
	{
		if (!initialized)
		{
			systemDatabase = SQLDatabaseFactory.openDatabase("__sysdb__", "System Database", DEFAULT_DB_SIZE);
			systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
			{
				
				@Override
				public void onTransaction(SQLTransaction tx)
				{
					String sql = "CREATE TABLE IF NOT EXISTS dbVersions (name VARCHAR(255), version INT)";
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Create System table SQL ["+sql+"]");
					}
					JsArrayMixed args = JsArrayMixed.createArray().cast();
					tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
					{
						
	
						@Override
						public void onSuccess(SQLTransaction tx, SQLResultSet rs)
						{
							initialized = true;
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.INFO, "System table created.");
							}
							callback.onInitialize();
						}
					}, null);
				}
			}, new SQLDatabase.SQLTransactionErrorCallback()
			{
				
				@Override
				public void onError(SQLError error)
				{
					DBUtil.throwDOMException("Error Data", "Could not create the systam table (__sysdb__).");
				}
			}, null);
		}
	}

	private void doDelete(final DBOpenRequest request, final String name)
    {
		final SQLTransaction.SQLStatementErrorCallback errorCallback = getErrorCallback(request);
	    systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "SELECT * FROM dbVersions WHERE name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Execute SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (rs.getRows().length() == 0)
						{
							request.setResult((String)null);
	        				DBEvent evt = DBEvent.create("success");
	        				DBEvent.invoke("onsuccess", request, evt);
	        				return;
						}
						int version = JsUtils.readIntPropertyValue(rs.getRows().itemObject(0), "version");
						deleteDB(request, name, version, errorCallback);
					}
				}, errorCallback);
			}
		}, null, null);
    }

	private void deleteDB(final DBOpenRequest request, final String name, final int version, final SQLStatementErrorCallback errorCallback)
	{
    	final SQLDatabase database = SQLDatabaseFactory.openDatabase(name, name, DEFAULT_DB_SIZE);
    	database.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(final SQLTransaction tx)
			{
				String sql = "SELECT * FROM __sys__";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Execute SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						int length = rs.getRows().length();
						String sql;
						final JsArrayMixed args = JsArrayMixed.createArray().cast();
						for (int i=0; i< length; i++)
						{
							sql = "DROP TABLE "+name;
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.INFO, "Execute SQL ["+sql+"]");
							}
							tx.executeSQL(sql, args, null, errorCallback);
						}
						sql = "DROP TABLE __sys__";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.INFO, "Execute SQL ["+sql+"]");
						}
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								deleteFromDbVersions(request, name, version, errorCallback);
							}
						}, errorCallback);
					}
				}, errorCallback);
			}
		}, null, null);
	}
	
	private void deleteFromDbVersions(final DBOpenRequest request, final String name, final int version, final SQLStatementErrorCallback errorCallback)
	{
		systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "DELETE FROM dbVersions WHERE name = ? ";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Execute SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						request.setResult((String)null);
						DBEvent evt = DBEvent.create("success");
						JsUtils.writePropertyValue(evt, "newVersion", (String)null);
						JsUtils.writePropertyValue(evt, "oldVersion", version);
						DBEvent.invoke("onsuccess", request, evt);
					}
				}, errorCallback);
				
			}
		}, null, null);
	}
	
	private DBOpenRequest doOpen(final DBOpenRequest request, final String name, final int version)
    {
		final SQLTransaction.SQLStatementErrorCallback errorCallback = getErrorCallback(request);
		
		systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "SELECT * FROM dbVersions WHERE name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Execute SQL ["+sql+"]");
				}
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(name);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (rs.getRows().length() > 0)
						{
							JsArrayMixed output = JsArrayMixed.createArray().cast();
							JsUtils.readPropertyValue(rs.getRows().itemObject(0), "version", output); 
							openDB(name, (int) output.getNumber(0), version, request, errorCallback);
						}
						else
						{
							String sql = "INSERT INTO dbVersions VALUES (?,?)";
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.INFO, "Execute SQL ["+sql+"]");
							}
							args.push(version);
							tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
							{
								@Override
								public void onSuccess(SQLTransaction tx, SQLResultSet rs)
								{
									openDB(name, 0, version, request, errorCallback);
								}
							}, errorCallback);
						}
					}
				}, errorCallback);
			}
		}, null, null);
		
		return request;
    }
	
    private void openDB(final String name, final int oldVersion, final int newVersion, 
    					final DBOpenRequest request, final SQLTransaction.SQLStatementErrorCallback errorCallback)
    {
    	final SQLDatabase database = SQLDatabaseFactory.openDatabase(name, name, DEFAULT_DB_SIZE);
    	request.setReadyState("done");
    	if (newVersion <=0 || oldVersion > newVersion)
    	{
    		throwError(request, "Data Error", "An attempt was made to open a database using a lower version than the existing version.");
    		return;
    	}
    	database.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(final SQLTransaction tx)
			{
				String sql = "CREATE TABLE IF NOT EXISTS __sys__ (name VARCHAR(255), keyPath VARCHAR(255), autoInc BOOLEAN, indexData BLOB, indexColumns BLOB)";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.INFO, "Execute SQL ["+sql+"]");
				}
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						String sql = "SELECT * FROM __sys__";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.INFO, "Execute SQL ["+sql+"]");
						}
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
                            public void onSuccess(SQLTransaction tx, SQLResultSet rs)
                            {
								final DBDatabase dbDatabase = DBDatabase.create(database, name, newVersion, rs);
								request.setSource(dbDatabase);
								request.setResult(dbDatabase);
								if (oldVersion < newVersion)
								{
									updateDBVersionAndOpen(name, oldVersion, newVersion, request, errorCallback, dbDatabase);
								}
								else
								{
			        				DBTransaction transaction = DBTransaction.create(dbDatabase.getObjectStoreNames(), DBTransaction.READ, dbDatabase);
			        				request.setTransaction(transaction);
									openWhenStoresAreReady(dbDatabase, request);
								}
							}
						}, errorCallback);
					}
				}, errorCallback);
			}
		}, null, null);
    }
	
	private void updateDBVersionAndOpen(final String name, final int oldVersion, final int newVersion, final DBOpenRequest request, final SQLTransaction.SQLStatementErrorCallback errorCallback, final DBDatabase dbDatabase)
    {
        systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
        {
        	@Override
        	public void onTransaction(SQLTransaction tx)
        	{
        		String sql = "UPDATE dbVersions SET version = ? WHERE name = ?";
        		if (LogConfiguration.loggingIsEnabled())
        		{
        			logger.log(Level.INFO, "Execute SQL ["+sql+"]");
        		}
        		JsArrayMixed args = JsArrayMixed.createArray().cast();
        		args.push(newVersion);
        		args.push(name);
        		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
        		{
        			@Override
        			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
        			{
        				Array<String> storeNames = dbDatabase.getObjectStoreNames();
        				DBTransaction transaction = DBTransaction.create(storeNames, DBTransaction.VERSION_TRANSACTION, dbDatabase);
        				request.setTransaction(transaction);
        				dbDatabase.setVersionTransaction(transaction);
        				DBEvent upgradeEvt = DBEvent.create("upgradeneeded");
        				JsUtils.writePropertyValue(upgradeEvt, "oldVersion", oldVersion);
        				JsUtils.writePropertyValue(upgradeEvt, "newVersion", newVersion);
        				DBEvent.invoke("onupgradeneeded", request, upgradeEvt);
        				openWhenStoresAreReady(dbDatabase, request);
        			}
        		}, errorCallback);
        	}
        }, null, null);
    }

	/**
	 * Ensure that open callback will be called after all database are ready. It is necessary because
	 * Web SQL API execute Asynchronous operations to load object stores metadata, and IndexedDB API 
	 * returns Object Stores from transactions synchronously. 
	 * @param database
	 * @param request
	 */
	private void openWhenStoresAreReady(DBDatabase database, final DBOpenRequest request)
    {
        Array<String> storeNames = database.getObjectStoreNames();
        final int totalStores = storeNames.size();
        if (totalStores > 0)
        {
        	readyStores = 0;
        	for (int i=0; i< totalStores; i++)
        	{
        		String objectStoreName = storeNames.get(i);
        		DBObjectStore objectStore = request.getTransaction().objectStore(objectStoreName);
        		objectStore.loadMetadata(new Callback()
        		{
        			@Override
        			public void execute()
        			{
        				readyStores++;
        				if (readyStores == totalStores)
        				{
        					DBEvent successEvt = DBEvent.create("success");
        					DBEvent.invoke("onsuccess", request, successEvt);
        				}
        			}
        		});
        	}
        }
        else
        {
			DBEvent successEvt = DBEvent.create("success");
			DBEvent.invoke("onsuccess", request, successEvt);
        }
    }
	
	private SQLTransaction.SQLStatementErrorCallback getErrorCallback(final DBOpenRequest request)
	{
		return new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				throwError(request, error.getName(), error.getMessage());
				return true;
			}
		};
	}
	
	private DBError throwError(DBOpenRequest request, String errorName, String message)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.SEVERE, "An error has occurred on transaction. Error name [" +errorName+"]. Error message ["+message+"]");
		}
		DBEvent evt = DBEvent.create("error");
		DBError error = DBError.create(errorName, message);
		request.setError(error);
		request.setResult((String)null);
		DBEvent.invoke("onerror", request, evt);
		return error;
    }
	
	static DBFactory create()
	{
		DBFactory factory = new DBFactory();
		return factory;
	}
	
	static void registerStaticFunctions()
    {
	    DBFactory dbFactory = create();
	    registerStaticFunctions(dbFactory);
    }
	
	private native static void registerStaticFunctions(DBFactory db)/*-{
		$wnd.__db_bridge__ = $wnd.__db_bridge__ || {};
		$wnd.__db_bridge__.indexedDB = {};
		
		$wnd.__db_bridge__.indexedDB.open = function(name,version){
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBFactory::open(Ljava/lang/String;I)(name, version);
		};

		$wnd.__db_bridge__.indexedDB.deleteDatabase = function(name){
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBFactory::deleteDatabase(Ljava/lang/String;)(name);
		};
		
		$wnd.__db_bridge__.indexedDB.cmp = function(key1, key2){
			var keys1 = $wnd.__db_bridge__.convertKey(key1);
			var keys2 = $wnd.__db_bridge__.convertKey(key2);
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBFactory::cmp(Lcom/google/gwt/core/client/JsArrayMixed;Lcom/google/gwt/core/client/JsArrayMixed;)(keys1, keys2);
		};
    }-*/;

	static interface InitializationCallback
	{
		void onInitialize();
	}
}
