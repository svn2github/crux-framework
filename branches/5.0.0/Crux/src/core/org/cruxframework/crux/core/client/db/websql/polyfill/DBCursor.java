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

import org.cruxframework.crux.core.client.db.websql.SQLTransaction;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBCursor extends JavaScriptObject
{
	protected DBCursor(){}
	private DBObjectStore objectStore;
	private DBKeyRange range;
	private DBRequest cursorRequest;
	private String valueColumnName;
	private String keyColumnName;
	private int offset = -1;
	private JsArrayMixed lastKeyContinued;

	public static DBCursor create(DBKeyRange range, String direction, DBObjectStore dbObjectStore, DBRequest cursorRequest, String keyColumnName, String valueColumnName)
	{
		DBCursor cursor = DBCursor.createObject().cast();
		cursor.setKeyRange(range);
		cursor.setSource(dbObjectStore);
		cursor.setObjectStore(dbObjectStore);
		cursor.setDirection(direction);
		cursor.setRequest(cursorRequest);
		cursor.setKeyColumnName(keyColumnName);
		cursor.setValueColumnName(valueColumnName);
		
		if (!dbObjectStore.getTransaction().isActive())
		{
			cursor.throwError("Inactive Error", "The transaction this IDBObjectStore belongs to is not active.");
		}
		else
		{
//			continueCursor();
		}
		
		return cursor;
	}


	public final native String getDirection()/*-{
		return this.direction; 
    }-*/;
	
	protected void find(JsArrayMixed key, SQLTransaction tx)
	{
		StringBuilder sql = new StringBuilder("SELECT * FROM \"").append(objectStore.getName()).append("\" WHERE ").append(keyColumnName).append(" NOT NULL");
		JsArrayMixed sqlValues = range.getBounds();
		if (range != null)
		{
			if (range.hasLowerBound())
			{
				sql.append(" AND ").append(keyColumnName).append(range.isLowerOpen()?" > ":" >= ").append("?");
			}
			if (range.hasUpperBound())
			{
				sql.append(" AND").append(keyColumnName).append(range.isUpperOpen()?" < ":" <= ").append("?");
			}
			if (key != null && key.length() > 0)
			{
				this.lastKeyContinued = key;
				this.offset = 0;
			}
			if (lastKeyContinued != null && lastKeyContinued.length() > 0)
			{
				sql.append(" AND").append(keyColumnName).append(" >= ").append("?");
				sqlValues.push(DBUtil.encodeKey(lastKeyContinued));
			}
			if (getDirection().endsWith("unique"))
			{
				sql.append(" GROUP BY ").append(keyColumnName);
			}
			sql.append(" ORDER BY ").append(keyColumnName);
			if (getDirection().startsWith("prev"))
			{
				sql.append(" DESC");
			}
			String sqlStatement = sql.toString();
//			tx.executeSQL(sqlStatement, sqlValues, stmtCallback, errorCallback);
		}
	}
//
//	 IDBCursor.prototype.__find = function(key, tx, success, error){
//	        var me = this;
//	        var sql = ["SELECT * FROM ", idbModules.util.quote(me.__idbObjectStore.name)];
//	        var sqlValues = [];
//	        sql.push("WHERE ", me.__keyColumnName, " NOT NULL");
//	        if (me.__range && (me.__range.lower || me.__range.upper)) {
//	            sql.push("AND");
//	            if (me.__range.lower) {
//	                sql.push(me.__keyColumnName + (me.__range.lowerOpen ? " >" : " >= ") + " ?");
//	                sqlValues.push(idbModules.Key.encode(me.__range.lower));
//	            }
//	            (me.__range.lower && me.__range.upper) && sql.push("AND");
//	            if (me.__range.upper) {
//	                sql.push(me.__keyColumnName + (me.__range.upperOpen ? " < " : " <= ") + " ?");
//	                sqlValues.push(idbModules.Key.encode(me.__range.upper));
//	            }
//	        }
//	        if (typeof key !== "undefined") {
//	            me.__lastKeyContinued = key;
//	            me.__offset = 0;
//	        }
//	        if (me.__lastKeyContinued !== undefined) {
//	            sql.push("AND " + me.__keyColumnName + " >= ?");
//	            sqlValues.push(idbModules.Key.encode(me.__lastKeyContinued));
//	        }
//	        sql.push("ORDER BY ", me.__keyColumnName);
//	        sql.push("LIMIT 1 OFFSET " + me.__offset);
//	        idbModules.DEBUG && console.log(sql.join(" "), sqlValues);
//	        tx.executeSql(sql.join(" "), sqlValues, function(tx, data){
//	            if (data.rows.length === 1) {
//	                var key = idbModules.Key.decode(data.rows.item(0)[me.__keyColumnName]);
//	                var val = me.__valueColumnName === "value" ? idbModules.Sca.decode(data.rows.item(0)[me.__valueColumnName]) : idbModules.Key.decode(data.rows.item(0)[me.__valueColumnName]);
//	                success(key, val);
//	            }
//	            else {
//	                idbModules.DEBUG && console.log("Reached end of cursors");
//	                success(undefined, undefined);
//	            }
//	        }, function(tx, data){
//	            idbModules.DEBUG && console.log("Could not execute Cursor.continue");
//	            error(data);
//	        });
//	    };	
	
	
	protected void throwError(String errorName, String message)
    {
		DBError error = DBError.create(errorName, message);
		cursorRequest.setReadyState("done");
		cursorRequest.setError(error);
		cursorRequest.setResult((String)null);
		DBEvent evt = DBEvent.create("error");
		DBEvent.invoke("onerror", cursorRequest, evt);
		objectStore.getTransaction().throwError(error);
    }

    private void setValueColumnName(String valueColumnName)
    {
		this.valueColumnName = valueColumnName;
    }

	private void setKeyColumnName(String keyColumnName)
    {
		this.keyColumnName = keyColumnName;
    }

	private void setRequest(DBRequest cursorRequest)
    {
		this.cursorRequest = cursorRequest;
    }

	private native void setDirection(String direction)/*-{
		this.direction = direction; 
    }-*/;

	private void setKeyRange(DBKeyRange range)
    {
		this.range = range;
    }

	private void setObjectStore(DBObjectStore dbObjectStore)
    {
		objectStore = dbObjectStore;
    }

	private native void setSource(JavaScriptObject src)/*-{
	    this.source = src;
    }-*/;
	
	
	public static interface CursorCallback
	{
		void onSuccess(JsArrayMixed key, JsArrayMixed value);
	}
}
