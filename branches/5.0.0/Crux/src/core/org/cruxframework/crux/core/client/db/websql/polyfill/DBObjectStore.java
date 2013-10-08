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
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Timer;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBObjectStore extends JavaScriptObject
{
	private static Logger logger = Logger.getLogger(DBObjectStore.class.getName());

	protected DBObjectStore(){}

	public final native String getName()/*-{
		return this.name;
	}-*/;

	public final native DBTransaction getTransaction()/*-{
	    return this.transaction;
    }-*/;
	
    static final DBRequest add(DBObjectStore db, final JavaScriptObject object, final JsArrayMixed key)
    {
    	return db.add(object, key);
    }
    
    final DBRequest add(final JavaScriptObject object, final JsArrayMixed key)
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			final DBTransaction.RequestOperation op = this;
    			deriveKey(this, tx, object, key, new CallbackKey()
				{
					@Override
					public void execute(final JsArrayMixed key)
					{
						insertObject(object, tx, op, key);
					}
				});
    		}
    	}, this, new String[]{DBTransaction.READ_WRITE});
    	return request;
    }
    
    static final DBRequest put(DBObjectStore db, final JavaScriptObject object, final JsArrayMixed key)
    {
    	return db.put(object, key);
    }

    final DBRequest put(final JavaScriptObject object, final JsArrayMixed key)
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			final DBTransaction.RequestOperation op = this;
    			deriveKey(this, tx, object, key, new CallbackKey()
				{
					@Override
					public void execute(final JsArrayMixed key)
					{
					    String sql = "DELETE FROM \"" + getName() + "\" WHERE key = ?";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Removing old record ["+key+"], on object store ["+getName()+"]. SQL ["+sql+"]");
						}
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						args.push(DBUtil.encodeKey(key));
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.FINE, "Object was " +(rs.getRowsAffected()>0?"updated":"inserted")+" into the table ["+getName()+"]");
								}
								insertObject(object, tx, op, key);
							}
						}, new SQLTransaction.SQLStatementErrorCallback()
						{
							@Override
							public boolean onError(SQLTransaction tx, SQLError error)
							{
								op.throwError(error.getName(), error.getMessage());
								return false;
							}
						});
					}
				});
    		}
    	}, this, new String[]{DBTransaction.READ_WRITE});
    	return request;
    }
    
// TODO   public DBRequest deleteRange(final JsArrayMixed key)
//    {
//    	
//    }
    
    static final DBRequest delete(DBObjectStore db, JsArrayMixed key)
    {
    	return db.delete(key);
    }
    
    final DBRequest delete(final JsArrayMixed key)
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			final DBTransaction.RequestOperation op = this;
    			waitForReady(new DBObjectStore.Callback()
				{
					@Override
					public void execute()
					{
					    String sql = "DELETE FROM \"" + getName() + "\" WHERE key = ?";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Removing old record ["+key+"], on object store ["+getName()+"]. SQL ["+sql+"]");
						}
						final JsArrayMixed args = JsArrayMixed.createArray().cast();
						args.push(DBUtil.encodeKey(key));
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.FINE, "Object was deleted from database. Key ["+args.getString(0)+"] Table ["+getName()+"]");
								}
								op.setResult(null);
								op.onSuccess();
							}
						}, new SQLTransaction.SQLStatementErrorCallback()
						{
							@Override
							public boolean onError(SQLTransaction tx, SQLError error)
							{
								op.throwError(error.getName(), error.getMessage());
								return false;
							}
						});
					}
				}, null);
    		}
    	}, this, new String[]{DBTransaction.READ_WRITE});
    	return request;
    }

 // TODO   get com keyrange public DBRequest get(final JsArrayMixed key)
//  {
//  	
//  }
    
    static final DBRequest get (DBObjectStore db, final JsArrayMixed key)
    {
    	return db.get(key);
    }
    
    final DBRequest get (final JsArrayMixed key)
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				waitForReady(new Callback()
				{
					@Override
					public void execute()
					{
						String sql = "SELECT * FROM \"" + getName() + "\" WHERE key = ?";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Retrieving record ["+key+"], on object store ["+getName()+"]. SQL ["+sql+"]");
						}
						final JsArrayMixed args = JsArrayMixed.createArray().cast();
						args.push(DBUtil.encodeKey(key));
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								if (rs.getRows().length() > 0)
								{
									JsArrayMixed result = JsArrayMixed.createArray().cast();
									JsUtils.readPropertyValue(rs.getRows().itemObject(0), "value", result);
									if (result.length() > 0)
									{
										op.setResult(DBUtil.decodeValue(result.getString(0)));
									}
									else
									{
										op.throwError("Data Error", "Error reading value from object store ["+getName()+"]");
										return;
									}
								}
								else
								{
									op.setResult(null);
								}
								op.onSuccess();
							}
						}, new SQLTransaction.SQLStatementErrorCallback()
						{
							@Override
							public boolean onError(SQLTransaction tx, SQLError error)
							{
								op.throwError(error.getName(), error.getMessage());
								return false;
							}
						});
					}
				}, null);
			}
		}, this, new String[]{DBTransaction.READ, DBTransaction.READ_WRITE});
    	
    	return request;
    }
    
    static final DBRequest clear(DBObjectStore db)
    {
    	return db.clear();
    }
    
    final DBRequest clear()
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				waitForReady(new Callback()
				{
					@Override
					public void execute()
					{
						String sql = "DELETE FROM \"" + getName() + "\"";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Removing all object store records .Object store ["+getName()+"]. SQL ["+sql+"]");
						}
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.FINE, "All objects cleared from object store ["+getName()+"].");
								}
								op.setResult(null);
								op.onSuccess();
							}
						}, new SQLTransaction.SQLStatementErrorCallback()
						{
							@Override
							public boolean onError(SQLTransaction tx, SQLError error)
							{
								op.throwError(error.getName(), error.getMessage());
								return false;
							}
						});
					}
				}, null);
			}
		}, this, new String[]{DBTransaction.READ_WRITE});
    	
    	return request;
    }
//TODO    public DBRequest count(KeyRange)

    static final DBRequest count(DBObjectStore db)
    {
    	return db.count();
    }
    
    final DBRequest count()
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				waitForReady(new Callback()
				{
					@Override
					public void execute()
					{
						String sql = "SELECT COUNT(*) AS total FROM \"" + getName() + "\"";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Counting all records on object store ["+getName()+"]. SQL ["+sql+"]");
						}
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								
								JsArrayMixed result = JsArrayMixed.createArray().cast();
								JsUtils.readPropertyValue(rs.getRows().itemObject(0), "total", result);
								if (result.length() > 0)
								{
									op.setContentAsResult(result);
								}
								else
								{
									op.throwError("Data Error", "Error counting records on object store ["+getName()+"]");
									return;
								}
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.FINE, "There are ["+result.getNumber(0)+"] records on object store ["+getName()+"].");
								}
								op.onSuccess();
							}
						}, new SQLTransaction.SQLStatementErrorCallback()
						{
							@Override
							public boolean onError(SQLTransaction tx, SQLError error)
							{
								op.throwError(error.getName(), error.getMessage());
								return false;
							}
						});
					}
				}, null);
			}
		}, this, new String[]{DBTransaction.READ, DBTransaction.READ_WRITE});
    	
    	return request;
    }    
    
    static final DBRequest openCursor (DBObjectStore db, DBKeyRange range, String direction)
    {
    	return db.openCursor(range, direction);
    }
    
    final DBRequest openCursor (DBKeyRange range, String direction)
    {
    	DBRequest cursorRequest = getTransaction().createRequest(this);
        DBCursor.create(range, direction, this, cursorRequest, "key", "value"); 
        return cursorRequest;
    }
    
//TODO index    
//    public DBIndex index(String indexName)
//    {
//        var index = new idbModules.IDBIndex(indexName, this);
//        return index;
//    }

//TOD create index  e com chave (keyPath) composta  
//    public DBIndex createIndex (String indexName, String keyPath, optionalParameters)
//    {
//        var me = this;
//        optionalParameters = optionalParameters || {};
//        me.__setReadyState("createIndex", false);
//        var result = new idbModules.IDBIndex(indexName, me);
//        me.__waitForReady(function(){
//            result.__createIndex(indexName, keyPath, optionalParameters);
//        }, "createObjectStore");
//        me.indexNames.push(indexName);
//        return result;
//    }

//TODO delete index
//    public void deleteIndex (String indexName)
//    {
//        var result = new idbModules.IDBIndex(indexName, this, false);
//        result.__deleteIndex(indexName);
//        return result;
//    }    
    
	native final DBObjectStoreMetadata getMetadata()/*-{
		return this.metadata;
	}-*/;

	/**
	 * Need this flag as createObjectStore is synchronous. So, we simply return when create ObjectStore is called
	 * but do the processing in the background. All other operations should wait till ready is set
	 * @param key
	 * @param value
	 */
	final void setReadyState(String key, Boolean value)
	{
		getReadyProperties().put(key, value);
	}

	/**
	 * Called by all operations on the object store, waits till the store is ready, and then performs the operation
	 * @param callback
	 * @param key
	 */
	private void waitForReady(final Callback callback, final String key)
	{
		boolean ready = true;
		Map<Boolean> readyProperties = getReadyProperties();
		if (key != null) 
		{
			ready = (readyProperties.containsKey(key) ? readyProperties.get(key) : true);
		}
		else 
		{
			Array<String> keys = readyProperties.keys();
			for (int i=0; i< keys.size(); i++) 
			{
				if (!readyProperties.get(keys.get(i))) 
				{
					ready = false;
					break;
				}
			}
		}

		if (ready) 
		{
			callback.execute();
		}
		else 
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.FINE, "Waintin for property ["+key+"], on object store ["+getName()+"] to be ready.");
			}
			new Timer()
			{
				@Override
				public void run()
				{
					waitForReady(callback, key);
				}
			}.schedule(50);
		}
	}

	/**
	 * Reads (and optionally caches) the properties like keyPath, autoincrement, etc for this objectStore
	 * @param requestOp
	 * @param tx
	 * @param callback
	 * @param waitOnProperty
	 */
	private void readStoreProps(final DBTransaction.RequestOperation requestOp, final SQLTransaction tx, final Callback callback, String waitOnProperty)
	{
		waitForReady(new Callback()
		{
			@Override
			public void execute()
			{
				if (getMetadata() != null)
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Reading object store metadata from cache. Store name ["+getName()+"].");
					}
					callback.execute();
				}
				else
				{
					String sql = "SELECT * FROM __sys__ WHERE name = ?";
					JsArrayMixed args = JsArrayMixed.createArray().cast();
					args.push(getName());
					tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
					{
						@Override
						public void onSuccess(SQLTransaction tx, SQLResultSet rs)
						{
							if (rs.getRows().length() != 1)
							{
								requestOp.throwError("Not Found", "Error Reading object store metadata. Store name ["+getName()+"]. No rows found on system table.");
								return;
							}
							else
							{
								DBObjectStoreMetadata metadata = rs.getRows().itemObject(0).cast();
								setMetadata(metadata);
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.FINE, "Reading object store metadata from database. Store name ["+getName()+"]. Result cached.");
								}
								callback.execute();
							}
						}
					}, new SQLTransaction.SQLStatementErrorCallback()
					{
						@Override
						public boolean onError(SQLTransaction tx, SQLError error)
						{
							requestOp.throwError(error.getName(), "Error Reading object store metadata. Store name ["+getName()+"]. Error name ["+error.getName()+"]. Error message ["+error.getMessage()+"].");
							return false;
						}
					});
				}
			}
		}, waitOnProperty);
	}

	/**
	 * From the store properties and object, extracts the value for the key in hte object Store
	 * If the table has auto increment, get the next in sequence
	 * @param requestOp
	 * @param tx
	 * @param object
	 * @param key
	 * @param callback
	 */
	private void deriveKey(final DBTransaction.RequestOperation requestOp, final SQLTransaction tx, final JavaScriptObject object, final JsArrayMixed key, final CallbackKey callback)
	{
		readStoreProps(requestOp, tx, new Callback(){
			@Override
			public void execute()
			{
				if (getMetadata() == null)
				{
					requestOp.throwError("Data Error", "Could not locate definition for the table ["+getName()+"]");
					return;
				}
				if (!StringUtils.isEmpty(getMetadata().getKeyPath()))
				{
					deriveKeyFromMetatadaKeyPath(requestOp, tx, object, key, callback);
				}
				else
				{
					if (key != null && key.length() > 0)
					{
						callback.execute(key);
					}
					else if (getMetadata().isAutoInc())
					{
						readNextAutoIncKey(requestOp, tx, callback);
					}
					else
					{
						requestOp.throwError("Data Error", "The object store ["+getName()+"] uses out-of-line keys and has no key generator and the key parameter was not provided.");
						return;
					}					
				}
			}
		}, null);//wait for all properties
	}

	private void readNextAutoIncKey(final DBTransaction.RequestOperation requestOp, SQLTransaction tx, final CallbackKey callback)
	{
		String sql = "SELECT * FROM sqlite_sequence WHERE name LIKE ?";
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL["+sql+"].");
		}
		JsArrayMixed args = JsArrayMixed.createArray().cast();
		args.push(getName());
		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				JsArrayMixed key = JsArrayMixed.createArray().cast();

				if (rs.getRows().length() != 1)
				{
					key.push(0);//TODO melhor reportar erro?
				}
				else
				{
					key.push(rs.getRows().itemDouble(0));
				}

				callback.execute(key);
			}
		}, new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				requestOp.throwError(error.getName(), "Could not get the auto increment value for key. Message["+error.getMessage()+"]");
				return false;
			}
		});
	}

	private void deriveKeyFromMetatadaKeyPath(final DBTransaction.RequestOperation requestOp, final SQLTransaction tx, final JavaScriptObject object, final JsArrayMixed key, final CallbackKey callback)
    {
        if (key != null && key.length() > 0)
        {
        	requestOp.throwError("Data Error", "The object store uses in-line keys and the key parameter was provided ["+getName()+"]");
        	return;
        }
        if (object == null)
        {
        	requestOp.throwError("Data Error", "The object was not specified. Object Store ["+getName()+"]");
        	return;
        }
        try 
        {
        	JsArrayMixed primaryKey = JsArrayMixed.createArray().cast();
        	JsUtils.readPropertyValue(object, getMetadata().getKeyPath(), primaryKey);
        	if (primaryKey.length() > 0)
        	{
        		callback.execute(primaryKey);
        	}
        	else if (getMetadata().isAutoInc())
        	{
        		readNextAutoIncKey(requestOp, tx, callback);
        	}
        	else
        	{
        		requestOp.throwError("Data Error", "Could not evaluate key from keyPath ["+getMetadata().getKeyPath()+"] on object store ["+getName()+"]");
        		return;
        	}					
        } 
        catch (Exception e) 
        {
        	requestOp.throwError("Data Error", "Could not evaluate key from keyPath ["+getMetadata().getKeyPath()+"] on object store ["+getName()+"]");
        }
    }

	private void insertObject(final JavaScriptObject object, final SQLTransaction tx, final DBTransaction.RequestOperation op, final JsArrayMixed key)
    {
        DBUtil.encodeValue(object, new DBUtil.EncodeCallback()
		{
			@Override
			public void onEncode(String encoded)
			{
				insertData(op, tx, object, encoded, key, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (key.length() > 1)
						{
							op.setResult(key);
						}
						else
						{
							op.setContentAsResult(key);
						}
						op.onSuccess();
					}
				}, new SQLTransaction.SQLStatementErrorCallback()
				{
					@Override
					public boolean onError(SQLTransaction tx, SQLError error)
					{
						op.throwError(error.getName(), error.getMessage());
						return false;
					}
				});
			}
		});
    }

    private void insertData(final DBTransaction.RequestOperation requestOp, SQLTransaction tx, final JavaScriptObject object, String encodedObject, JsArrayMixed primaryKey, 
    		SQLTransaction.SQLStatementCallback success, SQLTransaction.SQLStatementErrorCallback error)
    {
    	Map<String> paramMap = CollectionFactory.createMap();
    	if (primaryKey != null && primaryKey.length() > 0)
    	{
    		paramMap.put("key", DBUtil.encodeKey(primaryKey));
    	}

    	JsArrayMixed indexes = DBUtil.decodeValue(getMetadata().getIndexList());
    	if (indexes != null)
    	{
    		try
    		{
	    		for (int i=0; i< indexes.length(); i++)
	    		{
	        		JsArrayMixed indexProps = JsArrayMixed.createArray().cast();
	        		JsUtils.readPropertyValue(indexes, "key.columnName", indexProps);
	        		JsUtils.readPropertyValue(indexes, "key.keyPath", indexProps);
	        		JsUtils.readPropertyValue(object, indexProps.getString(1), indexProps);
	    			
	    			paramMap.put(indexProps.getString(0), DBUtil.encodeKey(indexProps.getObject(2)));
	    		}
    		}
    		catch (Exception e) 
    		{
    			requestOp.throwError("Data Error", "Error updating indexes while processing transaction request. Error Message [" +e.getMessage()+"].");
    			return;
			}
    	}
    	
    	StringBuilder sqlStart = new StringBuilder("INSERT INTO ").append("\""+ getName() +"\" (");
    	StringBuilder sqlEnd = new StringBuilder(" VALUES(");
    	JsArrayMixed sqlValues = JsArrayMixed.createArray().cast();
    	
    	Array<String> keys = paramMap.keys();
    	for (int i=0; i< keys.size(); i++)
    	{
    		String key = keys.get(i);
    		sqlStart.append(key +",");
    		sqlEnd.append("?,");
    		sqlValues.push(paramMap.get(key));
    	}
    	
		sqlStart.append("value)");
		sqlEnd.append("?");
		sqlValues.push(encodedObject);
    	
        String sql = sqlStart.toString()+" "+sqlEnd.toString() ;
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Inserting data. SQL["+sql+"].");
		}

		tx.executeSQL(sql, sqlValues, success, error);
    }

	private native void setIndexNames(Array<String> indexNames)/*-{
	    this.indexNames = indexNames;
    }-*/;

	private native void setTransaction(DBTransaction dbTransaction)/*-{
	    this.transaction = dbTransaction;
    }-*/;

	private native void setName(String objectStoreName)/*-{
	    this.name = objectStoreName;
    }-*/;

	private native final void setMetadata(DBObjectStoreMetadata meta)/*-{
		this.metadata = meta;
	}-*/;
	
	private native Map<Boolean> getReadyProperties()/*-{
		return this.ready;
	}-*/;
	
	private native void setReadyProperties(Map<Boolean> r)/*-{
		this.ready = r;
	}-*/;
	
	private native void handleObjectNativeFunctions(DBObjectStore db)/*-{
		function convertKey(key)
		{
			if (!key) return null;
			var keys = (Object.prototype.toString.call(key) === '[object Array]')?key:[key];
			return keys; 
		}
		
		this.add = function(value, key){
			var keys = convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::add(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JsArrayMixed;)(db, value, keys);
		};
	
		this.put = function(value, key){
			var keys = convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::put(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JsArrayMixed;)(db, value, keys);
		};
		this["delete"] = function(key){
			var keys = convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::delete(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;Lcom/google/gwt/core/client/JsArrayMixed;)(db, keys);
		};
		this.get = function(key){
			var keys = convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::get(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;Lcom/google/gwt/core/client/JsArrayMixed;)(db, keys);
		};
		this.clear = function(){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::clear(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;)(db);
		};
		this.count = function(){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::count(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;)(db);
		};
		this.openCursor = function(range, direction){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::openCursor(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStore;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBKeyRange;Ljava/lang/String;)(db, range, direction);
		};
	}-*/;

	public static DBObjectStore create(String objectStoreName, DBTransaction dbTransaction, boolean ready)
	{
		DBObjectStore objectStore = DBObjectStore.createObject().cast();
		objectStore.setName(objectStoreName);
		Map<Boolean> readyProp = CollectionFactory.createMap();
		objectStore.setReadyProperties(readyProp);
		objectStore.setTransaction(dbTransaction);
		objectStore.setReadyState("createObjectStore", ready);
		Array<String> indexNames = CollectionFactory.createArray();
		objectStore.setIndexNames(indexNames);
		objectStore.handleObjectNativeFunctions(objectStore);
		return objectStore;
	}

	private static interface Callback
	{
		void execute();
	}

	private static interface CallbackKey
	{
		void execute(JsArrayMixed key);
	}
}
