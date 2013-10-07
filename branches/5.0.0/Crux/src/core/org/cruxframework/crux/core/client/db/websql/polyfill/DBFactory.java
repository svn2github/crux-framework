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

import org.cruxframework.crux.core.client.db.websql.SQLDatabase;
import org.cruxframework.crux.core.client.db.websql.SQLDatabaseFactory;
import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.utils.JsUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBFactory
{
	protected static Logger logger = Logger.getLogger(DBFactory.class.getName());
	private static int  DEFAULT_DB_SIZE = 5 * 1024 * 1024;
	private boolean initialized;
	private SQLDatabase systemDatabase;
	
	public final void init()
	{
		systemDatabase = SQLDatabaseFactory.openDatabase("__sysdb__", 1, "System Database", DEFAULT_DB_SIZE);
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

	public final DBOpenRequest open(String name, final int version)
	{
		final DBOpenRequest request = DBOpenRequest.create();
		final SQLTransaction.SQLStatementErrorCallback errorCallback = new SQLTransaction.SQLStatementErrorCallback()
		{
			@Override
			public boolean onError(SQLTransaction tx, SQLError error)
			{
				throwError(request, error.getName(), error.getMessage());
				return false;
			}
		};
		
		systemDatabase.transaction(new SQLDatabase.SQLTransactionCallback()
		{
			@Override
			public void onTransaction(SQLTransaction tx)
			{
				String sql = "SELECT * FROM dbVersions where name = ?";
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
						if (rs.getRowsAffected() > 0)
						{
							JsArrayMixed output = JsArrayMixed.createArray().cast();
							JsUtils.readPropertyValue(rs.getRows().itemObject(0), "version", output); 
							openDB((int) output.getNumber(0));
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
						              openDB(0);
								}
							}, errorCallback);
						}
					}
				}, errorCallback);
			}
		}, null, null);
		
		return request;
	}
	
    protected DBError throwError(DBOpenRequest request, String errorName, String message)
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
	
    private void openDB(int oldVersion)
    {
    	
    }
//        function openDB(oldVersion){
//            var db = window.openDatabase(name, 1, name, DEFAULT_DB_SIZE);
//            req.readyState = "done";
//            if (typeof version === "undefined") {
//                version = oldVersion || 1;
//            }
//            if (version <= 0 || oldVersion > version) {
//                idbModules.util.throwDOMException(0, "An attempt was made to open a database using a lower version than the existing version.", version);
//            }
//            
//            db.transaction(function(tx){
//                tx.executeSql("CREATE TABLE IF NOT EXISTS __sys__ (name VARCHAR(255), keyPath VARCHAR(255), autoInc BOOLEAN, indexList BLOB)", [], function(){
//                    tx.executeSql("SELECT * FROM __sys__", [], function(tx, data){
//                        var e = idbModules.Event("success");
//                        req.source = req.result = new idbModules.IDBDatabase(db, name, version, data);
//                        if (oldVersion < version) {
//                            // DB Upgrade in progress 
//                            sysdb.transaction(function(systx){
//                                systx.executeSql("UPDATE dbVersions set version = ? where name = ?", [version, name], function(){
//                                    var e = idbModules.Event("upgradeneeded");
//                                    e.oldVersion = oldVersion;
//                                    e.newVersion = version;
//                                    req.transaction = req.result.__versionTransaction = new idbModules.IDBTransaction([], 2, req.source);
//                                    idbModules.util.callback("onupgradeneeded", req, e, function(){
//                                        var e = idbModules.Event("success");
//                                        idbModules.util.callback("onsuccess", req, e);
//                                    });
//                                }, dbCreateError);
//                            }, dbCreateError);
//                        } else {
//                            idbModules.util.callback("onsuccess", req, e);
//                        }
//                    }, dbCreateError);
//                }, dbCreateError);
//            }, dbCreateError);
//        }
	
	public static void registerStaticFunctions()
    {
	    // TODO Auto-generated method stub
	    
    }

}
