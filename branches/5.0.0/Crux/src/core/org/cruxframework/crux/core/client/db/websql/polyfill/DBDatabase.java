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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBDatabase extends JavaScriptObject
{
	protected static Logger logger = Logger.getLogger(DBDatabase.class.getName());

	private SQLDatabase sqlDatabase;
	private SQLResultSet storeProperties;
	private DBTransaction versionTransaction;

	protected DBDatabase(){}

    public final void close()
    {
        // Don't do anything... the database automatically closes
    }
    
    public final DBTransaction transaction (JsArrayString storeNames, String mode)
    {
        DBTransaction transaction = DBTransaction.create(storeNames, mode, this);
        return transaction;
    };	
    
    public final DBObjectStore createObjectStore(final String storeName, final DBObjectStoreParameters createOptions)
    {
		if (versionTransaction == null)
		{
			DBUtil.throwDOMException("Invalid State", "Database can create object stores only on a versionchange transaction");
		}
    	final DBObjectStore result = DBObjectStore.create(storeName, versionTransaction, false);
    	versionTransaction.addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(SQLTransaction tx)
			{
				final DBTransaction.RequestOperation op = this;
				if (versionTransaction == null)
				{
					DBUtil.throwDOMException("Invalid State", "Invalid State error");
				}
				final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
				{
					@Override
					public boolean onError(SQLTransaction tx, SQLError error)
					{
						versionTransaction.throwError(error.getName(), "Could not create new object store. Error name["+error.getName()+"]. Error Message["+error.getMessage()+"]");
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
						args.push(JavaScriptObject.createObject());
						String sql = "INSERT INTO __sys__ VALUES (?,?,?,?)";
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
		}, this);
      // The IndexedDB Specification needs us to return an Object Store immediately, but WebSQL does not create and return the store immediately
      // Hence, this can technically be unusable, and we hack around it, by setting the ready value to false
      getObjectStoreNames().add(storeName);
      return result;
    }

    public final void deleteObjectStore(final String storeName)
    {
		if (versionTransaction == null)
		{
			DBUtil.throwDOMException("Invalid State", "Database can delete object stores only on a versionchange transaction");
		}
		final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				versionTransaction.throwError(error.getName(), "Could not create new object store. Error name["+error.getName()+"]. Error Message["+error.getMessage()+"]");
				return false;
			}
		};

		int index = getObjectStoreNames().indexOf(storeName);
		if (index < 0)
		{
			versionTransaction.throwError("Not Found", "Object store ["+storeName+"] does not exist.");
			return;
		}
		getObjectStoreNames().remove(index);
		versionTransaction.addToTransactionQueue(new DBTransaction.RequestOperation(){
			@Override
            public void doOperation(SQLTransaction tx)
            {
				if (versionTransaction == null)
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
						if (rs.getRowsAffected() > 0)
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
		}, this);
    }
    
	public final native Array<String> getObjectStoreNames() /*-{
		return this.objectStoreNames;
	}-*/;
	
	private native void setObjectStoreNames(Array<String> names) /*-{
		this.objectStoreNames = names;
	}-*/;
	
	SQLDatabase getSQLDatabase()
	{
		return sqlDatabase;
	}
	
	private native void handleObjectNativeFunctions(DBDatabase db)/*-{
		this.transaction = function(storeNames, mode){
			db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::transaction(Lcom/google/gwt/core/client/JsArrayString;Ljava/lang/String;)(storeNames, mode);
		};
		this.close = function(){
			db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::close()();
		};
	
		this.createObjectStore = function(objectStoreName, createOptions){
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::createObjectStore(Ljava/lang/String;Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBObjectStoreParameters;)(objectStoreName, createOptions);
		};
		this.deleteObjectStore = function(objectStoreName){
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBDatabase::deleteObjectStore(Ljava/lang/String;)(objectStoreName);
		};
		this.onabort = null;
		this.onerror = null;
		this.onversionchange = null; 
	}-*/;
	
	private void setStoreProperties(SQLResultSet storeProperties)
	{
		this.storeProperties = storeProperties;
	}

	private native void setName(String name)/*-{
		this.name = name;
	}-*/;

	private native void setVersion(String version)/*-{
		this.version = version;
	}-*/;
	
	public static DBDatabase create(SQLDatabase db, String name, String version, SQLResultSet storeProperties)
	{
		DBDatabase database = createObject().cast();
		database.setName(name);
		database.setVersion(version);
		Array<String> storeNames = Array.createArray().cast();
		int rowsAffected = storeProperties.getRowsAffected();
		SQLResultSetRowList rows = storeProperties.getRows();
		for (int i=0; i < rowsAffected; i++)
		{
			storeNames.add(rows.itemString(i));
		}
		database.setStoreProperties(storeProperties);
		database.setObjectStoreNames(storeNames);
		database.handleObjectNativeFunctions(database);
		return database;
	}
}
