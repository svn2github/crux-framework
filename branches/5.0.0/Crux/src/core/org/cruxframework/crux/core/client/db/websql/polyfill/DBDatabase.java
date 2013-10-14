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
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLResultSetRowList;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.utils.JsUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBDatabase extends JavaScriptObject
{
	private static Logger logger = Logger.getLogger(DBDatabase.class.getName());

	protected DBDatabase(){}

    static void close()
    {
        // Don't do anything... the database automatically closes
    }

    static final DBTransaction transaction (DBDatabase db, Array<String> storeNames, String mode)
    {
    	return db.transaction(storeNames, mode);
    }
    
    final DBTransaction transaction (Array<String> storeNames, String mode)
    {
        DBTransaction transaction = DBTransaction.create(storeNames, mode, this);
        return transaction;
    };	
    
    static final DBObjectStore createObjectStore(DBDatabase db, String storeName, DBObjectStoreParameters createOptions)
    {
    	return db.createObjectStore(storeName, createOptions);
    }
    
    final DBObjectStore createObjectStore(final String storeName, final DBObjectStoreParameters createOptions)
    {
		if (getVersionTransaction() == null)
		{
			DBUtil.throwDOMException("Invalid State", "Database can create object stores only on a versionchange transaction");
		}
    	final DBObjectStore result = DBObjectStore.create(storeName, getVersionTransaction());
    	getVersionTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				if (getVersionTransaction() == null)
				{
					DBUtil.throwDOMException("Invalid State", "Invalid State error");
				}
				final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
				{
					@Override
					public boolean onError(SQLTransaction tx, SQLError error)
					{
						getVersionTransaction().throwError(error.getName(), "Could not create new object store. Error name["+error.getName()+"]. Error Message["+error.getMessage()+"]");
						return false;
					}
				};
				String sql = "CREATE TABLE \"" + storeName + "\" (key BLOB " + (createOptions.isAutoIncrement()?", inc INTEGER PRIMARY KEY AUTOINCREMENT":"PRIMARY KEY") + ", value BLOB)";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"].");
				}
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						JsArrayMixed args = JsArrayMixed.createArray().cast();
						args.push(storeName);
						args.push(createOptions.getStringKeyPath()); // TODO tratar chave multipla
						args.push(createOptions.isAutoIncrement());
						args.push(DBUtil.encodeKey(JavaScriptObject.createObject()));
						args.push(DBUtil.encodeKey(JavaScriptObject.createArray()));
						String sql = "INSERT INTO __sys__ VALUES (?,?,?,?,?)";
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Running SQL ["+sql+"].");
						}
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback(){
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								result.setReadyState("createObjectStore", true);
								op.setResult(result);
			                    op.onSuccess();
							}
						}, errorCallback);
					}
				}, errorCallback);
			}
		}, this, new String[]{DBTransaction.VERSION_TRANSACTION});
      // The IndexedDB Specification needs us to return an Object Store immediately, but WebSQL does not create and return the store immediately
      // Hence, this can technically be unusable, and we hack around it, by setting the ready value to false
      getObjectStoreNames().add(storeName);
      return result;
    }

    static final void deleteObjectStore(DBDatabase db, final String storeName)
    {
    	db.deleteObjectStore(storeName);
    }
    
    final void deleteObjectStore(final String storeName)
    {
		if (getVersionTransaction() == null)
		{
			DBUtil.throwDOMException("Invalid State", "Database can delete object stores only on a versionchange transaction");
		}
		final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				getVersionTransaction().throwError(error.getName(), "Could not create new object store. Error name["+error.getName()+"]. Error Message["+error.getMessage()+"]");
				return false;
			}
		};

		int index = getObjectStoreNames().indexOf(storeName);
		if (index < 0)
		{
			getVersionTransaction().throwError("Not Found", "Object store ["+storeName+"] does not exist.");
			return;
		}
		getObjectStoreNames().remove(index);
		getVersionTransaction().addToTransactionQueue(new DBTransaction.RequestOperation(){
			@Override
            public void doOperation(SQLTransaction tx)
            {
				if (getVersionTransaction() == null)
				{
					DBUtil.throwDOMException("Invalid State", "Invalid State error");
				}
				
				String sql = "SELECT * FROM __sys__ where name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"].");
				}
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(storeName);
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback(){
					@Override
                    public void onSuccess(SQLTransaction tx, SQLResultSet rs)
                    {
						if (rs.getRows().length() > 0)
						{
							String sql = "DROP TABLE \""+storeName+"\"";
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.FINE, "Running SQL ["+sql+"].");
							}
							JsArrayMixed args = JsArrayMixed.createArray().cast();
							tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback(){
								@Override
								public void onSuccess(SQLTransaction tx, SQLResultSet rs)
								{
									String sql = "DELETE FROM __sys__ WHERE name = ?";
									if (LogConfiguration.loggingIsEnabled())
									{
										logger.log(Level.FINE, "Running SQL ["+sql+"].");
									}
									JsArrayMixed args = JsArrayMixed.createArray().cast();
									args.push(storeName);
									tx.executeSQL(sql, args, null, errorCallback);
								}
							}, errorCallback);
						}
                    }
				}, errorCallback);
            }
		}, this, new String[]{DBTransaction.VERSION_TRANSACTION});
    }
    
	public final native Array<String> getObjectStoreNames() /*-{
		return this.objectStoreNames;
	}-*/;
	
    native final DBTransaction getVersionTransaction()/*-{
    	return this.versionTransaction;
    }-*/;
    
    
    native final void setVersionTransaction(DBTransaction t)/*-{
    	this.versionTransaction = t;
    }-*/;
    
	private native void setObjectStoreNames(Array<String> names) /*-{
		this.objectStoreNames = names;
	}-*/;
	
	private native void handleObjectNativeFunctions(DBDatabase db)/*-{
		this.transaction = function(storeNames, mode){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::transaction(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBDatabase;Lorg/cruxframework/crux/core/client/collection/Array;Ljava/lang/String;)(db, storeNames, mode);
		};
		this.close = function(){
			@org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::close()();
		};
	
		this.createObjectStore = function(objectStoreName, createOptions){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::createObjectStore(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBDatabase;Ljava/lang/String;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStoreParameters;)(db, objectStoreName, createOptions);
		};
		this.deleteObjectStore = function(objectStoreName){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::deleteObjectStore(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBDatabase;Ljava/lang/String;)(db, objectStoreName);
		};
		this.onabort = null;
		this.onerror = null;
		this.onversionchange = null; 
	}-*/;
	
	private native void setStoreProperties(SQLResultSet storeProperties)/*-{
		this.storeProperties = storeProperties;
	}-*/;

	private native SQLResultSet getStoreProperties()/*-{
		return this.storeProperties;
	}-*/;
	
	private native void setName(String name)/*-{
		this.name = name;
	}-*/;

	private native void setVersion(int version)/*-{
		this.version = version;
	}-*/;
	
    native final SQLDatabase getSQLDatabase()/*-{
		return this.sqlDatabase;
	}-*/;
	
	private native void setSQLDatabase(SQLDatabase db)/*-{
		this.sqlDatabase = db;
	}-*/;

	public static DBDatabase create(SQLDatabase db, String name, int version, SQLResultSet storeProperties)
	{
		DBDatabase database = createObject().cast();
		database.setSQLDatabase(db);
		database.setName(name);
		database.setVersion(version);
		Array<String> storeNames = Array.createArray().cast();
		int length = storeProperties.getRows().length();
		SQLResultSetRowList rows = storeProperties.getRows();
		for (int i=0; i < length; i++)
		{
			storeNames.add(JsUtils.readStringPropertyValue(rows.itemObject(i), "name"));
		}
		database.setStoreProperties(storeProperties);
		database.setObjectStoreNames(storeNames);
		database.handleObjectNativeFunctions(database);
		return database;
	}
}
