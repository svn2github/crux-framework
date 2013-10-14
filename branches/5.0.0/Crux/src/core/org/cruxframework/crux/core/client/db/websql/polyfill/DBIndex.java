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
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLResultSetRowList;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBTransaction.RequestOperation;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBUtil.EncodeCallback;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBIndex extends JavaScriptObject
{
	private static Logger logger = Logger.getLogger(DBIndex.class.getName());

	protected DBIndex (){}
	
	static DBIndex create(String indexName, DBObjectStore dbObjectStore)
	{
		assert(!StringUtils.unsafeEquals(dbObjectStore.getTransaction().getMode(), DBTransaction.VERSION_TRANSACTION)):"This constructor can not be used on version transactions";
		DBObjectStoreMetadata metadata = dbObjectStore.getMetadata();
		if (metadata == null)
		{
			DBUtil.throwDOMException("Invalid State", "Index ["+indexName+"] metatada on object store ["+dbObjectStore.getName()+"] could not be retrieved.");
		}
		DBIndexData dbIndexData = metadata.getIndexData().get(indexName);
		if (dbIndexData == null)
		{
			DBUtil.throwDOMException("Not Found", "Index ["+indexName+"] not found on object store ["+dbObjectStore.getName()+"]");
		}
		return create(indexName, dbObjectStore, dbIndexData.getKeyPath(), dbIndexData.getIndexParameters());
	}

	static DBIndex create(String indexName, DBObjectStore dbObjectStore, Array<String> keyPath, DBIndexParameters indexParameters)
	{
		DBIndex index = DBIndex.createObject().cast();
		index.setName(indexName);
		index.setObjectStore(dbObjectStore);
		if (keyPath != null)
		{
			JsArrayMixed k = keyPath.cast();
			JsUtils.writePropertyValue(index, "keyPath", k, k.length() == 1);
		}
		JsUtils.writePropertyValue(index, "unique", indexParameters != null?indexParameters.isUnique():false);
		JsUtils.writePropertyValue(index, "multiEntry", indexParameters != null?indexParameters.isMultiEntry():false);
		index.handleObjectNativeFunctions(index);
		
		return index;
	}

	static final DBRequest openCursor (DBIndex db, DBKeyRange range, String direction)
	{
		return db.openCursor(range, direction);
	}
	
	final DBRequest openCursor (DBKeyRange range, String direction)
	{
    	DBRequest cursorRequest = getObjectStore().getTransaction().createRequest(this);
        DBCursor.create(range, direction, this.getObjectStore(), cursorRequest, getName(), "value"); 
        return cursorRequest;
	}

	static final DBRequest openKeyCursor (DBIndex db, DBKeyRange range, String direction)
	{
		return db.openKeyCursor(range, direction);
	}
	
	final DBRequest openKeyCursor (DBKeyRange range, String direction)
	{
    	DBRequest cursorRequest = getObjectStore().getTransaction().createRequest(this);
        DBCursor.create(range, direction, this.getObjectStore(), cursorRequest, getName(), "key"); 
        return cursorRequest;
	}    

    static final DBRequest count(DBIndex db, JavaScriptObject key)
    {
    	return db.count(key);
    }
	
    final DBRequest count(JavaScriptObject key)
    {
    	return fetchIndexData(key, new FetchCallback()
		{
			@Override
			public void onData(DBTransaction.RequestOperation op, SQLResultSet rs)
			{
				JsArrayMixed result = JsArrayMixed.createArray().cast();
				result.push(rs.getRows().length());
				op.setContentAsResult(result);
			}
		});
    }	

    static final DBRequest get(DBIndex db, JavaScriptObject key)
    {
    	return db.get(key);
    }
    
    final DBRequest get(JavaScriptObject key)
    {
    	return fetchIndexData(key, new FetchCallback()
		{
			@Override
			public void onData(DBTransaction.RequestOperation op, SQLResultSet rs)
			{
				if (rs.getRows().length() == 0)
				{
					op.setResult(null);
				}
				else
				{
					JavaScriptObject object = rs.getRows().itemObject(0);
					String encodedObject = JsUtils.readStringPropertyValue(object, "value");
					op.setResult(DBUtil.decodeValue(encodedObject));	
				}
			}
		});
    }	
    
    static final DBRequest getKey(DBIndex db, JavaScriptObject key)
    {
    	return db.getKey(key);
    }
    
    final DBRequest getKey(JavaScriptObject key)
    {
    	return fetchIndexData(key, new FetchCallback()
		{
			@Override
			public void onData(DBTransaction.RequestOperation op, SQLResultSet rs)
			{
				if (rs.getRows().length() == 0)
				{
					op.setResult(null);
				}
				else
				{
					JavaScriptObject object = rs.getRows().itemObject(0);
					String encodedKey = JsUtils.readStringPropertyValue(object, "key");
					op.setResult(DBUtil.decodeKey(encodedKey));	
				}
			}
		});
    }	
	
    final void deleteIndex()
    {
		getObjectStore().getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				getObjectStore().readStoreProps(this, tx, new DBObjectStore.Callback()
				{
					@Override
					public void execute()
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Deleting index ["+getName()+"] on Object Store ["+getObjectStore().getName()+"].");
						}
						final Map<DBIndexData> indexData = getObjectStore().getMetadata().getIndexData();
						if (!indexData.containsKey(getName()))
						{
		                    DBUtil.throwDOMException("Data Error", "Index ["+getName()+"] not found on object store ["+getObjectStore().getName()+"]");
						}
						indexData.remove(getName());
												
						StringBuilder sql = new StringBuilder("ALTER TABLE \"").append(getObjectStore().getName()).append("\" DROP COLUMN \"").append(getName()).append("\"");
						String sqlStatement = sql.toString();
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Running SQL ["+sqlStatement+"].");
						}
						tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								updateIndexMetadata(new Callback()
								{
									@Override
                                    public void onSuccess()
                                    {
										if (LogConfiguration.loggingIsEnabled())
										{
											logger.log(Level.FINE, "Index Successfully removed.");
										}
										getObjectStore().setReadyState("deleteIndex-"+getName(), true);
										op.onSuccess();
                                    }
								}, indexData, tx);
							}
						}, getErrorHandler());
					}
				}, "createObjectStore");
			}
		}, this, new String[]{DBTransaction.VERSION_TRANSACTION});    	
    }
    
	final void createIndex(final Array<String> keyPath, final DBIndexParameters optionalParameters)
	{
		if (keyPath == null || keyPath.size() == 0)
		{
			DBUtil.throwDOMException("Data Error", "Invalid KeyPath for index ["+getName()+"].");
		}
		
		getObjectStore().getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				getObjectStore().readStoreProps(this, tx, new DBObjectStore.Callback()
				{
					@Override
					public void execute()
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Creating index ["+getName()+"] on Object Store ["+getObjectStore().getName()+"].");
						}
						final Map<DBIndexData> indexData = getObjectStore().getMetadata().getIndexData();
						if (indexData.containsKey(getName()))
						{
		                    DBUtil.throwDOMException("Data Error", "Index already exists on store");
						}
						
						DBIndexData index = DBIndexData.createObject().cast();
						index.setKeyPath(keyPath);
						index.setIndexParameters(optionalParameters);
						indexData.put(getName(), index);
						
						StringBuilder sql = new StringBuilder("ALTER TABLE \"").append(getObjectStore().getName()).append("\" ADD \"").append(getName()).append("\" BLOB");
						if (optionalParameters.isUnique())
						{
							sql.append(" UNIQUE");
						}
						String sqlStatement = sql.toString();
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Running SQL ["+sqlStatement+"].");
						}
						tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								updateIndex(new Callback()
								{
									@Override
                                    public void onSuccess()
                                    {
										if (LogConfiguration.loggingIsEnabled())
										{
											logger.log(Level.FINE, "Index Successfully created.");
										}
										getObjectStore().setReadyState("createIndex-"+getName(), true);
										op.onSuccess();
                                    }
								}, keyPath, indexData, tx);
							}
						}, getErrorHandler());
					}
				}, "createObjectStore");
			}
		}, this, new String[]{DBTransaction.VERSION_TRANSACTION});
	}

	private SQLStatementErrorCallback getErrorHandler()
    {
        return new SQLTransaction.SQLStatementErrorCallback()
        {
        	@Override
        	public boolean onError(SQLTransaction tx, SQLError error)
        	{
        		DBUtil.throwDOMException(error.getName(), error.getMessage());
        		return true;
        	}
        };
    }

	private void updateIndex(final Callback callback, final Array<String> keyPath, final Map<DBIndexData> indexData, SQLTransaction tx)
    {
        // Once a column is created, put existing records into the index
		String sql = "SELECT * FROM \""+getObjectStore().getName() + "\"";
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.FINE, "Running SQL ["+sql+"].");
		}
		JsArrayMixed args = JsArrayMixed.createArray().cast();
		tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(final SQLTransaction tx, SQLResultSet rs)
			{
				updateIndexEntries(keyPath, tx, rs);
				updateIndexMetadata(callback, indexData, tx);
			}
		}, getErrorHandler());
    }
	
	private void updateIndexMetadata(final Callback callback, final Map<DBIndexData> indexData, final SQLTransaction tx)
    {
        DBUtil.encodeValue(indexData, new EncodeCallback()
		{
			@Override
			public void onEncode(String encoded)
			{
				String sql = "UPDATE __sys__ SET indexData = ?  WHERE name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"].");
				}
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(encoded);
				args.push(getObjectStore().getName());
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						callback.onSuccess();
					}
				}, getErrorHandler());
			}
		});
    }

	private void updateIndexEntries(final Array<String> keyPath, final SQLTransaction tx, SQLResultSet rs)
    {
        SQLResultSetRowList rows = rs.getRows();
		int length = rows.length();
		for (int i = 0; i < length; i++)
		{
			try
			{
				JavaScriptObject valueObject = DBUtil.decodeValue(JsUtils.readStringPropertyValue(rows.itemObject(i), "value"));
				StringBuilder sql = new StringBuilder("UPDATE \""+getObjectStore().getName()+"\" SET \"").append(getName()).append("\"= ?");
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				JsArrayMixed indexKey = JsArrayMixed.createArray().cast();
				for (int j=0; j<keyPath.size(); j++)
				{
					JsUtils.readPropertyValue(valueObject, keyPath.get(j), indexKey);
				}
				sql.append(" WHERE key = ?");
				args.push(DBUtil.encodeKey(indexKey));
				JsUtils.readPropertyValue(rows.itemObject(i), "key", args);
				String sqlStatement = sql.toString();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sqlStatement+"].");
				}
				tx.executeSQL(sqlStatement, args, null, null);
			}
			catch (Exception e) 
			{
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Ignoring Value for index ["+getName()+"].", e);
				}
			}
		}
    }
	
	private DBRequest fetchIndexData(final JavaScriptObject param, final FetchCallback callback)
	{
		DBRequest result = getObjectStore().getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(SQLTransaction tx)
			{
				StringBuilder sql = new StringBuilder("SELECT * FROM \"").append(getObjectStore().getName()).append("\" WHERE \"").append(getName()).append("\" NOT NULL");
				JsArrayMixed args = null;
				JsArrayMixed key = null;
				DBKeyRange range = null;
				if (param != null)
				{
					if (JsUtils.isArray(param))
					{
						key = param.cast();
					}
					else
					{
						range = param.cast();
					}
				}
				
				if (key != null && key.length() > 0)
				{
					sql.append(" AND \"").append(getName()).append("\" = ? ");
					args = JsArrayMixed.createArray().cast();
					args.push(DBUtil.encodeKey(key));
				}
				else if (range != null)
				{
					if (range.hasLowerBound())
					{
						sql.append(" AND ").append(getName()).append(range.isLowerOpen()?" > ":" >= ").append("?");
					}
					if (range.hasUpperBound())
					{
						sql.append(" AND").append(getName()).append(range.isUpperOpen()?" < ":" <= ").append("?");
					}
					args = range.getBounds();
				}
				String sqlStatement = sql.toString();
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sqlStatement+"].");
				}
				final DBTransaction.RequestOperation op = this;
				tx.executeSQL(sqlStatement, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						callback.onData(op, rs);
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
		}, this, new String[]{DBTransaction.READ, DBTransaction.READ_WRITE});
		
		return result;
	}
	
	private native void setObjectStore(DBObjectStore os)/*-{
		this.objectStore = os;
		this.source = os;
	}-*/;
	
	final native DBObjectStore getObjectStore()/*-{
		return this.objectStore;
	}-*/;
	
	private native void setName(String name)/*-{
		this.name = name;
	}-*/;
	
	final native String getName()/*-{
		return this.name;
	}-*/;
	
	private native void handleObjectNativeFunctions(DBIndex db)/*-{
		this.openCursor = function(range, direction){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::openCursor(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBKeyRange;Ljava/lang/String;)(db, range, direction);
		};
		this.openKeyCursor = function(range, direction){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::openKeyCursor(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBKeyRange;Ljava/lang/String;)(db, range, direction);
		};
	
		this.count = function(key){
			var keys = $wnd.__db_bridge__.convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::count(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lcom/google/gwt/core/client/JavaScriptObject;)(db, keys);
		};
		this.get = function(key){
			var keys = $wnd.__db_bridge__.convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::get(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lcom/google/gwt/core/client/JavaScriptObject;)(db, keys);
		};
		this.getKey = function(key){
			var keys = $wnd.__db_bridge__.convertKey(key);
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::getKey(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lcom/google/gwt/core/client/JavaScriptObject;)(db, keys);
		};
		this.openCursor = function(range, direction){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::openCursor(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBKeyRange;Ljava/lang/String;)(db, range, direction);
		};
		this.openKeyCursor = function(range, direction){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBIndex::openKeyCursor(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBIndex;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBKeyRange;Ljava/lang/String;)(db, range, direction);
		};
	}-*/;
	
	private static interface FetchCallback
	{
		void onData(RequestOperation op, SQLResultSet rs);
	}

	private static interface Callback
	{
		void onSuccess();
	}
}
