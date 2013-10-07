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

import org.cruxframework.crux.core.client.db.websql.SQLError;
import org.cruxframework.crux.core.client.db.websql.SQLResultSet;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
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
public class DBCursor extends JavaScriptObject
{
	protected static Logger logger = Logger.getLogger(DBCursor.class.getName());

	private static final int NOT_INITIALIZED = -1;
	private static final int CURSOR_BEGIN = 0;

	protected DBCursor(){}
	private DBObjectStore objectStore;
	private DBKeyRange range;
	private DBRequest cursorRequest;
	private String valueColumnName;
	private String keyColumnName;
	private int offset = -1;
	private SQLResultSet resultSet;
	private int length = -1;

	public static DBCursor create(DBKeyRange range, String direction, DBObjectStore dbObjectStore, DBRequest cursorRequest, String keyColumnName, String valueColumnName)
	{
		DBCursor cursor = DBCursor.createObject().cast();
		cursor.setKeyRange(range);
		cursor.setSource(dbObjectStore);
		cursor.setObjectStore(dbObjectStore);
		cursor.setDirection(direction==null?"":direction);
		cursor.setRequest(cursorRequest);
		cursor.setKeyColumnName(keyColumnName);
		cursor.setValueColumnName(valueColumnName);
		cursor.handleObjectNativeFunctions(cursor);
		
		if (!dbObjectStore.getTransaction().isActive())
		{
			cursor.throwError("Inactive Error", "The transaction this IDBObjectStore belongs to is not active.");
		}
		else
		{
			cursor.continueCursor(null);
		}
		
		return cursor;
	}

	public final void continueCursor(final JsArrayMixed key)
	{
		if (offset == NOT_INITIALIZED || key != null)
		{
			if (offset != NOT_INITIALIZED)
			{
				offset = NOT_INITIALIZED;
				length = NOT_INITIALIZED;
				resultSet = null;
			}
			objectStore.getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
			{
				@Override
				public void doOperation(SQLTransaction tx)
				{
					find(key, tx);
				}
			}, this, new String[]{DBTransaction.READ, DBTransaction.READ_WRITE});
		}
		else
		{
			offset++;
			if (LogConfiguration.loggingIsEnabled())
			{
				if (offset == length)
				{
					logger.log(Level.FINE, "Reached the end of cursor");
				}
			}
			fireSuccessEvent();
		}
	}

	public final void advance(int count)
	{
		if (offset == NOT_INITIALIZED)
		{
			throwError("Not Initialized", "Cursor is not initialized. Object store ["+objectStore.getName()+"]");
			return;
		}
		if (count <= 0)
		{
			throwError("Invalid State", "Count can not be 0 or negative.");
			return;
		}
		offset += count;
		if (LogConfiguration.loggingIsEnabled())
		{
			if (offset >= length)
			{
				logger.log(Level.FINE, "Reached the end of cursor");
			}
		}
		fireSuccessEvent();
	}
	
	public final DBRequest update(final JavaScriptObject valueToUpdate)
	{
		if (offset == NOT_INITIALIZED)
		{
			throwError("Not Initialized", "Cursor is not initialized. Object store ["+objectStore.getName()+"]");
			return null;
		}
		if (offset >= length)
		{
			throwError("Out Of Range", "Can not update cursors. It is out of range. Object store ["+objectStore.getName()+"]");
			return null;
		}
		final DBCursor me = this;
		DBRequest request = objectStore.getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				DBUtil.encodeValue(valueToUpdate, new EncodeCallback()
				{
					@Override
					public void onEncode(String encoded)
					{
						String sql = new StringBuilder("UPDATE \"").append(objectStore.getName()).append("\" SET value = ? WHERE key = ?").toString(); 
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "UPDATE SQL ["+sql+"]");
						}
						final JsArrayMixed key = JsArrayMixed.createArray().cast();
						JsUtils.readPropertyValue(me, "key", key);
						
						final JsArrayMixed args = JsArrayMixed.createArray().cast();
						args.push(encoded);
						args.push(DBUtil.encodeKey(key));
						
						tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
						{
							@Override
							public void onSuccess(SQLTransaction tx, SQLResultSet rs)
							{
								if (rs.getRowsAffected() == 1)
								{
									fireSuccessUpdateEvent(key);
								}
								else
								{
									throwError("Data Error", "No rowns with key found");
									return;
								}
							}

						}, new SQLTransaction.SQLStatementErrorCallback()
						{
							@Override
							public boolean onError(SQLTransaction tx, SQLError error)
							{
								throwError(error.getName(), error.getMessage());
								return false;
							}
						});
					}
				});
			}
		}, this, new String[]{DBTransaction.READ_WRITE});
		return request;
	}

	public final DBRequest delete()
	{
		if (offset == NOT_INITIALIZED)
		{
			throwError("Not Initialized", "Cursor is not initialized. Object store ["+objectStore.getName()+"]");
			return null;
		}
		if (offset >= length)
		{
			throwError("Out Of Range", "Can not update cursors. It is out of range. Object store ["+objectStore.getName()+"]");
			return null;
		}
		final DBCursor me = this;
		DBRequest request = objectStore.getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(SQLTransaction tx)
			{
				String sql = new StringBuilder("DELETE FROM  \"").append(objectStore.getName()).append("\" WHERE key = ?").toString(); 
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "DELETE SQL ["+sql+"]");
				}
				final JsArrayMixed key = JsArrayMixed.createArray().cast();
				JsUtils.readPropertyValue(me, "key", key);
				
				final JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(DBUtil.encodeKey(key));
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (rs.getRowsAffected() == 1)
						{
							fireSuccessUpdateEvent(null);
						}
						else
						{
							throwError("Data Error", "No rowns with key found");
							return;
						}
					}

				}, new SQLTransaction.SQLStatementErrorCallback()
				{
					@Override
					public boolean onError(SQLTransaction tx, SQLError error)
					{
						throwError(error.getName(), error.getMessage());
						return false;
					}
				});
			}
		}, this, new String[]{DBTransaction.READ_WRITE});
		return request;
	}
	
	public final native String getDirection()/*-{
		return this.direction; 
    }-*/;
	
	private void find(JsArrayMixed key, SQLTransaction tx)
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
				sql.append(" AND").append(keyColumnName).append(getDirection().startsWith("prev")?"<=":" >= ").append("?");
				sqlValues.push(DBUtil.encodeKey(key));
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
			tx.executeSQL(sqlStatement, sqlValues, new SQLTransaction.SQLStatementCallback()
			{
				@Override
				public void onSuccess(SQLTransaction tx, SQLResultSet rs)
				{
					resultSet = rs;
					offset = CURSOR_BEGIN;
					length = rs.getRowsAffected();
					fireSuccessEvent();
				}
			}, new SQLTransaction.SQLStatementErrorCallback()
			{
				@Override
				public boolean onError(SQLTransaction tx, SQLError error)
				{
					throwError(error.getName(), error.getMessage());
					return false;
				}
			});
		}
	}
	
	private void fireSuccessUpdateEvent(JsArrayMixed key)
	{
		DBEvent evt = DBEvent.create("success");
		cursorRequest.setReadyState("done");
		cursorRequest.setError(null);
		JsUtils.writePropertyValue(cursorRequest, "result", key, true);
        DBEvent.invoke("onsuccess", cursorRequest, evt);
	}
	private void fireSuccessEvent()
	{
		updateCursorValues();
		DBEvent evt = DBEvent.create("success");
		cursorRequest.setReadyState("done");
		cursorRequest.setError(null);
		cursorRequest.setResult(this);
        DBEvent.invoke("onsuccess", cursorRequest, evt);
	}
	
	private void updateCursorValues()
	{
		if (offset < length)
		{
			JavaScriptObject dbObject = resultSet.getRows().itemObject(offset);

			JsArrayMixed key = JsArrayMixed.createArray().cast();
			JsArrayMixed value = JsArrayMixed.createArray().cast();
			JsArrayMixed primaryKey = JsArrayMixed.createArray().cast();

			JsUtils.readPropertyValue(dbObject, keyColumnName, key);
			JsUtils.readPropertyValue(dbObject, valueColumnName, value);

			//Update Key
			key = DBUtil.decodeKey(key.getString(0));
			JsUtils.writePropertyValue(this, "key", key, true);
			
			//Update Value
			JavaScriptObject decodeValue = DBUtil.decodeValue(value.getString(0));
			if (StringUtils.unsafeEquals(valueColumnName, "value"))
			{
				setValue(decodeValue);
			}
			else
			{
				value = DBUtil.decodeKey(value.getString(0));
				JsUtils.writePropertyValue(this, "value", value, true);
			}

			//Update Primary Key
			if (!StringUtils.isEmpty(objectStore.metadata.getKeyPath()))
			{
				JsUtils.readPropertyValue(decodeValue, objectStore.metadata.getKeyPath(), primaryKey);
				JsUtils.writePropertyValue(this, "primaryKey", primaryKey, true);
			}
		}
		else
		{
			JsUtils.writePropertyValue(this, "key", null, false);
			JsUtils.writePropertyValue(this, "value", null, false);
			JsUtils.writePropertyValue(this, "primaryKey", null, false);
		}
	}

	private native void setValue(JavaScriptObject val)/*-{
	    this.value = val;
    }-*/;

	private void throwError(String errorName, String message)
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
	
	private native void handleObjectNativeFunctions(DBCursor db)/*-{
		function convertKey(key)
		{
			var keys = (Object.prototype.toString.call(key) === '[object Array]')?key:[key];
			return keys; 
		}
		
		this.update = function(value)
		{
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::update(Lcom/google/gwt/core/client/JavaScriptObject;)(value);
		};
	
		this.advance = function(count){
			db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::advance(I)(count);
		};
		this["delete"] = function(){
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::delete()();
		};
		this["continue"] = function(key){
			var keys = convertKey(key);
			return db.@org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::continueCursor(Lcom/google/gwt/core/client/JsArrayMixed;)(keys);
		};
		this.key = this.value = this.primaryKey = null;
	}-*/;
	
}
