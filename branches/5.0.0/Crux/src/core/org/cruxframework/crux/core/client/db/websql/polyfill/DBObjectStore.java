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
	protected DBObjectStore(){}
	protected static Logger logger = Logger.getLogger(DBObjectStore.class.getName());

	private Map<Boolean> ready = CollectionFactory.createMap();
	private DBObjectStoreMetadata metadata;

	public static DBObjectStore create(String objectStoreName, DBTransaction dbTransaction, boolean ready)
	{
		DBObjectStore objectStore = DBObjectStore.createObject().cast();
		objectStore.setName(objectStoreName);
		objectStore.setTransaction(dbTransaction);
		objectStore.setReadyState("createObjectStore", ready);
		Array<String> indexNames = CollectionFactory.createArray();
		objectStore.setIndexNames(indexNames);
		objectStore.handleObjectNativeFunctions(objectStore);
		return objectStore;
	}

	public final native String getName()/*-{
		return this.name;
	}-*/;

	public final native DBTransaction getTransaction()/*-{
	    return this.transaction;
    }-*/;
	
    public DBRequest add(final JavaScriptObject object, final JsArrayMixed key)
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			final DBTransaction.RequestOperation op = this;
    			deriveKey(tx, object, key, new CallbackKey()
				{
					@Override
					public void execute(final JsArrayMixed key)
					{
						insertObject(object, tx, op, key);
					}
				});
    		}
    	}, this);
    	return request;
    }

    public DBRequest put(final JavaScriptObject object, final JsArrayMixed key)
    {
    	DBRequest request = getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
    	{
    		@Override
    		public void doOperation(final SQLTransaction tx)
    		{
    			final DBTransaction.RequestOperation op = this;
    			deriveKey(tx, object, key, new CallbackKey()
				{
					@Override
					public void execute(final JsArrayMixed key)
					{
					    String sql = "DELETE FROM \"" + getName() + "\" where key = ?";
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
								op.onError(error);
								return true;
							}
						});
					}
				});
    		}
    	}, this);
    	return request;
    }
    
// TODO   public DBRequest deleteRange(final JsArrayMixed key)
//    {
//    	
//    }
    
    public DBRequest delete(final JsArrayMixed key)
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
					    String sql = "DELETE FROM \"" + getName() + "\" where key = ?";
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
								op.onError(error);
								return true;
							}
						});
					}
				}, null);
    		}
    	}, this);
    	return request;
    }

 // TODO   get com keyrange public DBRequest get(final JsArrayMixed key)
//  {
//  	
//  }
    
    public DBRequest get (final JsArrayMixed key)
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
						String sql = "SELECT * FROM \"" + getName() + "\" where key = ?";
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
								if (rs.getRowsAffected() > 0)
								{
									JsArrayMixed result = JsArrayMixed.createArray().cast();
									readPropertyValue(rs.getRows().itemObject(0), "value", result);
									if (result.length() > 0)
									{
										op.setResult(DBUtil.decodeValue(result.getString(0)));
									}
									else
									{
										DBUtil.throwDOMException("Data Error", "Error reading value from object store ["+getName()+"]");
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
								op.onError(error);
								return true;
							}
						});
					}
				}, null);
			}
		}, this);
    	
    	return request;
    }
    
    public DBRequest clear()
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
								op.onError(error);
								return true;
							}
						});
					}
				}, null);
			}
		}, this);
    	
    	return request;
    }
//TODO    public DBRequest count(KeyRange)

    public DBRequest count()
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
								readPropertyValue(rs.getRows().itemObject(0), "total", result);
								if (result.length() > 0)
								{
									op.setContentAsResult(result);
								}
								else
								{
									DBUtil.throwDOMException("Data Error", "Error counting records on object store ["+getName()+"]");
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
								op.onError(error);
								return true;
							}
						});
					}
				}, null);
			}
		}, this);
    	
    	return request;
    }    
    
	/**
	 * Need this flag as createObjectStore is synchronous. So, we simply return when create ObjectStore is called
	 * but do the processing in the background. All other operations should wait till ready is set
	 * @param key
	 * @param value
	 */
	protected void setReadyState(String key, Boolean value)
	{
		ready.put(key, value);
	}

	/**
	 * Called by all operations on the object store, waits till the store is ready, and then performs the operation
	 * @param callback
	 * @param key
	 */
	protected void waitForReady(final Callback callback, final String key)
	{
		boolean ready = true;
		if (key != null) 
		{
			ready = (this.ready.containsKey(key) ? this.ready.get(key) : true);
		}
		else 
		{
			Array<String> keys = this.ready.keys();
			for (int i=0; i< keys.size(); i++) 
			{
				if (!this.ready.get(keys.get(i))) 
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
	 * @param tx
	 * @param callback
	 * @param waitOnProperty
	 */
	private void readStoreProps(final SQLTransaction tx, final Callback callback, String waitOnProperty)
	{
		waitForReady(new Callback()
		{
			@Override
			public void execute()
			{
				if (metadata != null)
				{
					if (LogConfiguration.loggingIsEnabled())
					{
						logger.log(Level.FINE, "Reading object store metadata from cache. Store name ["+getName()+"].");
					}
					callback.execute();
				}
				else
				{
					String sql = "SELECT * FROM __sys__ where name = ?";
					JsArrayMixed args = JsArrayMixed.createArray().cast();
					args.push(getName());
					tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
					{
						@Override
						public void onSuccess(SQLTransaction tx, SQLResultSet rs)
						{
							if (rs.getRowsAffected() != 1)
							{
								if (LogConfiguration.loggingIsEnabled())
								{
									logger.log(Level.SEVERE, "Error Reading object store metadata. Store name ["+getName()+"]. No rows found on system table.");
								}
								DBUtil.throwDOMException("Not Found", "Error Reading object store metadata. Store name ["+getName()+"]. No rows found on system table.");
							}
							else
							{
								metadata = rs.getRows().itemObject(0).cast();
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
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.SEVERE, "Error Reading object store metadata. Store name ["+getName()+"]. Error name ["+error.getName()+"]. Error message ["+error.getMessage()+"].");
							}
							DBUtil.throwDOMException(error.getName(), error.getMessage());
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
	 * @param tx
	 * @param object
	 * @param key
	 * @param callback
	 */
	private void deriveKey(final SQLTransaction tx, final JavaScriptObject object, final JsArrayMixed key, final CallbackKey callback)
	{
		readStoreProps(tx, new Callback(){
			@Override
			public void execute()
			{
				if (metadata == null)
				{
					DBUtil.throwDOMException("Data Error", "Could not locate defination for the table ["+getName()+"]");
				}
				if (!StringUtils.isEmpty(metadata.getKeyPath()))
				{
					deriveKeyFromMetatadaKeyPath(tx, object, key, callback);
				}
				else
				{
					if (key != null && key.length() > 0)
					{
						callback.execute(key);
					}
					else if (metadata.isAutoInc())
					{
						readNextAutoIncKey(tx, callback);
					}
					else
					{
						DBUtil.throwDOMException("Data Error", "The object store ["+getName()+"] uses out-of-line keys and has no key generator and the key parameter was not provided.");
					}					
				}
			}
		}, null);//wait for all properties
	}

	private void readNextAutoIncKey(SQLTransaction tx, final CallbackKey callback)
	{
		String sql = "SELECT * FROM sqlite_sequence where name like ?";
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Reading next value from sequence. SQL["+sql+"].");
		}
		JsArrayMixed args = JsArrayMixed.createArray().cast();
		args.push(getName());
		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				JsArrayMixed key = JsArrayMixed.createArray().cast();

				if (rs.getRowsAffected() != 1)
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
				DBUtil.throwDOMException(error.getName(), "Could not get the auto increment value for key. Message["+error.getMessage()+"]");
				return false;
			}
		});
	}

	private void deriveKeyFromMetatadaKeyPath(final SQLTransaction tx, final JavaScriptObject object, final JsArrayMixed key, final CallbackKey callback)
    {
        if (key != null && key.length() > 0)
        {
        	DBUtil.throwDOMException("Data Error", "The object store uses in-line keys and the key parameter was provided ["+getName()+"]");
        }
        if (object == null)
        {
        	DBUtil.throwDOMException("Data Error", "The object was not specified. Object Store ["+getName()+"]");
        }
        try 
        {
        	JsArrayMixed primaryKey = JsArrayMixed.createArray().cast();
        	readPropertyValue(object, metadata.getKeyPath(), primaryKey);
        	if (primaryKey.length() > 0)
        	{
        		callback.execute(primaryKey);
        	}
        	else if (metadata.isAutoInc())
        	{
        		readNextAutoIncKey(tx, callback);
        	}
        	else
        	{
        		DBUtil.throwDOMException("Data Error", "Could not evaluate key from keyPath ["+metadata.getKeyPath()+"] on object store ["+getName()+"]");
        	}					
        } 
        catch (Exception e) 
        {
        	if (LogConfiguration.loggingIsEnabled())
        	{
        		logger.log(Level.SEVERE, "Could not evaluate key from keyPath ["+metadata.getKeyPath()+"] on object store ["+getName()+"]", e);
        	}
        	DBUtil.throwDOMException("Data Error", "Could not evaluate key from keyPath ["+metadata.getKeyPath()+"] on object store ["+getName()+"]");
        }
    }

	private void insertObject(final JavaScriptObject object, final SQLTransaction tx, final DBTransaction.RequestOperation op, final JsArrayMixed key)
    {
        DBUtil.encodeValue(object, new DBUtil.EncodeCallback()
		{
			@Override
			public void onEncode(String encoded)
			{
				insertData(tx, encoded, key, new SQLTransaction.SQLStatementCallback()
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
						op.onError(error);
						return true;
					}
				});
			}
		});
    }

    private void insertData(SQLTransaction tx, String encodedObject, JsArrayMixed primaryKey, 
    		SQLTransaction.SQLStatementCallback success, SQLTransaction.SQLStatementErrorCallback error)
    {
    	Map<String> paramMap = CollectionFactory.createMap();
    	if (primaryKey != null && primaryKey.length() > 0)
    	{
    		paramMap.put("key", DBUtil.encodeKey(primaryKey));
    	}
    	
//TODO terminar a parte dos indices
//   	var indexes = JSON.parse(this.__storeProps.indexList);
//        for (var key in indexes) {
//            try {
//                paramMap[indexes[key].columnName] = idbModules.Key.encode(eval("value['" + indexes[key].keyPath + "']"));
//            } 
//            catch (e) {
//                error(e);
//            }
//        }
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
    
	private native void readPropertyValue(JavaScriptObject object, String property, JsArrayMixed primaryKey)/*-{
		function getDescendantProp(obj, desc) {
		    var arr = desc.split(".");
		    while(arr.length && (obj = obj[arr.shift()]));
		    return obj;
		}

		primaryKey.push(getDescendantProp(object, property));    
    }-*/;

	private native void setIndexNames(Array<String> indexNames)/*-{
	    this.indexNames = indexNames;
    }-*/;

	private native void setTransaction(DBTransaction dbTransaction)/*-{
	    this.transaction = dbTransaction;
    }-*/;

	private native void setName(String objectStoreName)/*-{
	    this.name = objectStoreName;
    }-*/;

	private native void handleObjectNativeFunctions(DBObjectStore db)/*-{
		this.add = function(value, key){
			db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::add()(value, keys);
		};
	
		this.put = function(value, key){
			db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::put()(value, keys);
		};
		this["delete"] = function(value, key){
			db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBObjectStore::delete()(keys);
		};
	}-*/;

	protected static interface Callback
	{
		void execute();
	}

	protected static interface CallbackKey
	{
		void execute(JsArrayMixed key);
	}
}
