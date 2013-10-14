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
	private static Logger logger = Logger.getLogger(DBCursor.class.getName());

	private static final int NOT_INITIALIZED = -1;
	private static final int CURSOR_BEGIN = 0;

	protected DBCursor(){}

	public static DBCursor create(DBKeyRange range, String direction, DBObjectStore dbObjectStore, DBRequest cursorRequest, String keyColumnName, String valueColumnName)
	{
		DBCursor cursor = DBCursor.createObject().cast();
		cursor.setKeyRange(range);
		cursor.setSource(dbObjectStore);
		cursor.setObjectStore(dbObjectStore);
		cursor.setDirection(direction==null?"":direction);
		cursor.setCursorRequest(cursorRequest);
		cursor.setKeyColumnName(keyColumnName);
		cursor.setValueColumnName(valueColumnName);
		cursor.handleObjectNativeFunctions(cursor);
		cursor.setLength(NOT_INITIALIZED);
		cursor.setOffset(NOT_INITIALIZED);
		
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

	static final void continueCursor(DBCursor db, final JsArrayMixed key)
	{
		db.continueCursor(key);
	}
	
	final void continueCursor(final JsArrayMixed key)
	{
		if (getOffset() == NOT_INITIALIZED || key != null)
		{
			if (getOffset() != NOT_INITIALIZED)
			{
				setOffset(NOT_INITIALIZED);
				setLength(NOT_INITIALIZED);
				setResultSet(null);
			}
			getObjectStore().getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
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
			setOffset(getOffset()+1);
			if (LogConfiguration.loggingIsEnabled())
			{
				if (getOffset() == getLength())
				{
					logger.log(Level.FINE, "Reached the end of cursor");
				}
			}
			fireSuccessEvent();
		}
	}

	static final void advance(DBCursor db, int count)
	{
		db.advance(count);
	}
	
	final void advance(int count)
	{
		if (getOffset() == NOT_INITIALIZED)
		{
			throwError("Not Initialized", "Cursor is not initialized. Object store ["+getObjectStore().getName()+"]");
			return;
		}
		if (count <= 0)
		{
			throwError("Invalid State", "Count can not be 0 or negative.");
			return;
		}
		setOffset(getOffset() + count);
		if (LogConfiguration.loggingIsEnabled())
		{
			if (getOffset() >= getLength())
			{
				logger.log(Level.FINE, "Reached the end of cursor");
			}
		}
		fireSuccessEvent();
	}
	
	static final DBRequest update(DBCursor db, final JavaScriptObject valueToUpdate)
	{
		return db.update(valueToUpdate);
	}
	
	final DBRequest update(final JavaScriptObject valueToUpdate)
	{
		if (getOffset() == NOT_INITIALIZED)
		{
			throwError("Not Initialized", "Cursor is not initialized. Object store ["+getObjectStore().getName()+"]");
			return null;
		}
		if (getOffset() >= getLength())
		{
			throwError("Out Of Range", "Can not update cursors. It is out of range. Object store ["+getObjectStore().getName()+"]");
			return null;
		}
		final DBCursor me = this;
		DBRequest request = getObjectStore().getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(final SQLTransaction tx)
			{
				DBUtil.encodeValue(valueToUpdate, new EncodeCallback()
				{
					@Override
					public void onEncode(String encoded)
					{
						String sql = new StringBuilder("UPDATE \"").append(getObjectStore().getName()).append("\" SET value = ? WHERE key = ?").toString(); 
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Running SQL ["+sql+"]");
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

	static final DBRequest delete(DBCursor db)
	{
		return db.delete();
	}
	
	final DBRequest delete()
	{
		if (getOffset() == NOT_INITIALIZED)
		{
			throwError("Not Initialized", "Cursor is not initialized. Object store ["+getObjectStore().getName()+"]");
			return null;
		}
		if (getOffset() >= getLength())
		{
			throwError("Out Of Range", "Can not update cursors. It is out of range. Object store ["+getObjectStore().getName()+"]");
			return null;
		}
		final DBCursor me = this;
		DBRequest request = getObjectStore().getTransaction().addToTransactionQueue(new DBTransaction.RequestOperation()
		{
			@Override
			public void doOperation(SQLTransaction tx)
			{
				String sql = new StringBuilder("DELETE FROM  \"").append(getObjectStore().getName()).append("\" WHERE key = ?").toString(); 
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
		StringBuilder sql = new StringBuilder("SELECT * FROM \"").append(getObjectStore().getName()).append("\" WHERE ").append(getKeyColumnName()).append(" NOT NULL");
		JsArrayMixed sqlValues;
		if (getKeyRange() != null)
		{
			sqlValues = getKeyRange().getBounds();
			if (getKeyRange().hasLowerBound())
			{
				sql.append(" AND ").append(getKeyColumnName()).append(getKeyRange().isLowerOpen()?" > ":" >= ").append("?");
			}
			if (getKeyRange().hasUpperBound())
			{
				sql.append(" AND").append(getKeyColumnName()).append(getKeyRange().isUpperOpen()?" < ":" <= ").append("?");
			}
			if (key != null && key.length() > 0)
			{
				sql.append(" AND").append(getKeyColumnName()).append(getDirection().startsWith("prev")?"<=":" >= ").append("?");
				sqlValues.push(DBUtil.encodeKey(key));
			}
			if (getDirection().endsWith("unique"))
			{
				sql.append(" GROUP BY ").append(getKeyColumnName());
			}
			sql.append(" ORDER BY ").append(getKeyColumnName());
			if (getDirection().startsWith("prev"))
			{
				sql.append(" DESC");
			}
		}
		else
		{
			sqlValues = JsArrayMixed.createArray().cast();
		}
		String sqlStatement = sql.toString();
		tx.executeSQL(sqlStatement, sqlValues, new SQLTransaction.SQLStatementCallback()
		{
			@Override
			public void onSuccess(SQLTransaction tx, SQLResultSet rs)
			{
				setResultSet(rs);
				setOffset(CURSOR_BEGIN);
				setLength(rs.getRows().length());
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
	
	private void fireSuccessUpdateEvent(JsArrayMixed key)
	{
		DBEvent evt = DBEvent.create("success");
		DBRequest cursorRequest = getCursorRequest();
		cursorRequest.setReadyState("done");
		cursorRequest.setError(null);
		JsUtils.writePropertyValue(cursorRequest, "result", key, true);
        DBEvent.invoke("onsuccess", cursorRequest, evt);
	}
	
	private void fireSuccessEvent()
	{
		updateCursorValues();
		DBEvent evt = DBEvent.create("success");
		DBRequest cursorRequest = getCursorRequest();
		cursorRequest.setReadyState("done");
		cursorRequest.setError(null);
		if (getOffset() < getLength())
		{
			cursorRequest.setResult(this);
		}
		else
		{
			cursorRequest.setResult((String) null);
		}
        DBEvent.invoke("onsuccess", cursorRequest, evt, true);
	}
	
	private void updateCursorValues()
	{
		if (getOffset() < getLength())
		{
			JavaScriptObject dbObject = getResultSet().getRows().itemObject(getOffset());

			JsArrayMixed key = JsArrayMixed.createArray().cast();
			JsArrayMixed value = JsArrayMixed.createArray().cast();
			JsArrayMixed primaryKey = JsArrayMixed.createArray().cast();

			JsUtils.readPropertyValue(dbObject, getKeyColumnName(), key);
			JsUtils.readPropertyValue(dbObject, getValueColumnName(), value);

			//Update Key
			key = DBUtil.decodeKey(key.getString(0));
			JsUtils.writePropertyValue(this, "key", key, true);
			
			//Update Value
			JavaScriptObject decodedValue = DBUtil.decodeValue(value.getString(0));
			if (StringUtils.unsafeEquals(getValueColumnName(), "value"))
			{
				setValue(decodedValue);
			}
			else
			{
				value = DBUtil.decodeKey(value.getString(0));
				JsUtils.writePropertyValue(this, "value", value, true);
			}

			//Update Primary Key
			Array<String> keyPath = getObjectStore().getMetadata().getKeyPath();
			if (keyPath != null && keyPath.size() > 0)
			{
				for (int i = 0; i < keyPath.size(); i++)
				{
					JsUtils.readPropertyValue(decodedValue, keyPath.get(i), primaryKey, keyPath.size() > 1);					
				}
				JsUtils.writePropertyValue(this, "primaryKey", primaryKey, keyPath.size() == 1);
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
		DBRequest cursorRequest = getCursorRequest();
		cursorRequest.setReadyState("done");
		cursorRequest.setError(error);
		cursorRequest.setResult((String)null);
		DBEvent evt = DBEvent.create("error");
		DBEvent.invoke("onerror", cursorRequest, evt);
		getObjectStore().getTransaction().throwError(error);
    }

    private native String getValueColumnName()/*-{
		return this.valueColumnName;
    }-*/;

    private native void setValueColumnName(String valueColumnName)/*-{
		this.valueColumnName = valueColumnName;
    }-*/;

	private native String getKeyColumnName()/*-{
		return this.keyColumnName;
    }-*/;

	private native void setKeyColumnName(String keyColumnName)/*-{
		this.keyColumnName = keyColumnName;
    }-*/;

	private native void setDirection(String direction)/*-{
		this.direction = direction; 
    }-*/;

	private native DBKeyRange getKeyRange()/*-{
		return this.range;
	}-*/;

	private native void setKeyRange(DBKeyRange r)/*-{
		this.range = r;
	}-*/;
	
	private native DBRequest getCursorRequest()/*-{
		return this.cursorRequest;
	}-*/;
	
	private native void setCursorRequest(DBRequest r)/*-{
		this.cursorRequest = r;
	}-*/;

	private native DBObjectStore getObjectStore()/*-{
		return this.objectStore;
	}-*/;
	
	private native void setObjectStore(DBObjectStore os)/*-{
		this.objectStore = os;
	}-*/;
	
	private native SQLResultSet getResultSet()/*-{
		return this.resultSet;
	}-*/;
	
	private native void setResultSet(SQLResultSet r)/*-{
		this.resultSet = r;
	}-*/;

	private native int getOffset()/*-{
		return this.offset;
	}-*/;

	private native void setOffset(int o)/*-{
		this.offset = o;
	}-*/;

	private native int getLength()/*-{
		return this.length;
	}-*/;

	private native void setLength(int l)/*-{
		this.length = l;
	}-*/;

	private native void setSource(JavaScriptObject src)/*-{
	    this.source = src;
    }-*/;
	
	private native void handleObjectNativeFunctions(DBCursor db)/*-{
		this.update = function(value)
		{
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::update(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBCursor;Lcom/google/gwt/core/client/JavaScriptObject;)(db, value);
		};
	
		this.advance = function(count){
			@org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::advance(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBCursor;I)(db,count);
		};
		this["delete"] = function(){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::delete(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBCursor;)(db);
		};
		this["continue"] = function(key){
			var keys = $wnd.__db_bridge__.convertKey(key);
			@org.cruxframework.crux.core.client.db.websql.polyfill.DBCursor::continueCursor(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBCursor;Lcom/google/gwt/core/client/JsArrayMixed;)(db, keys);
		};
		this.key = this.value = this.primaryKey = null;
	}-*/;
	
}
