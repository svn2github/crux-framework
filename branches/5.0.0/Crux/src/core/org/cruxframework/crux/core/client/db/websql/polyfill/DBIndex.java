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
import org.cruxframework.crux.core.client.db.websql.SQLResultSetRowList;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBTransaction.RequestOperation;
import org.cruxframework.crux.core.client.db.websql.polyfill.DBUtil.EncodeCallback;
import org.cruxframework.crux.core.client.utils.JsUtils;

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
		DBIndex index = DBIndex.createObject().cast();
		index.setName(indexName);
		index.setObjectStore(dbObjectStore);
		
//		dbObjectStore.getMetadata().getIndexList()
		
		
		return index;
	}
	
	final void createIndex(final String indexName, final Array<String> keyPath, final DBIndexParameters optionalParameters)
	{
		if (keyPath == null || keyPath.size() == 0)
		{
			DBUtil.throwDOMException("Data Error", "Invalid KeyPath for index ["+indexName+"].");
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
							logger.log(Level.FINE, "Creating index ["+indexName+"] on Object Store ["+getObjectStore().getName()+"].");
						}
						final Map<DBIndexData> indexData = getObjectStore().getMetadata().getIndexData();
						if (indexData.containsKey(indexName))
						{
		                    DBUtil.throwDOMException("Data Error", "Index already exists on store");
						}
						Array<String> objectStoreIndexColumns = getObjectStore().getMetadata().getIndexColumns().cast();
						
						final Array<String> indexColumnNames = getColumnNames(keyPath);
						DBIndexData index = DBIndexData.createObject().cast();
						index.setKeyPaths(keyPath);
						index.setColumnNames(indexColumnNames);
						index.setIndexParameters(optionalParameters);
						indexData.put(indexName, index);
						
						StringBuilder sql = new StringBuilder("ALTER TABLE \"").append(getObjectStore().getName()).append("\"");
						boolean hasColumnsToUpdate = false;
						for (int i=0; i < indexColumnNames.size(); i++)
						{
							String col = indexColumnNames.get(i);
							if (objectStoreIndexColumns.indexOf(col) == -1)
							{
								if (hasColumnsToUpdate)
								{
									sql.append(",");
								}
								else
								{
									sql.append(" ADD ");
								}
								hasColumnsToUpdate = true;
								objectStoreIndexColumns.add(col);
								sql.append(col).append(" BLOB");
								if (optionalParameters.isUnique())
								{
									sql.append(" UNIQUE");
								}
							}
						}
						if (hasColumnsToUpdate)
						{
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
				                    updateIndex(op, keyPath, indexData, indexColumnNames, tx);
								}
							}, getErrorHandler());
						}
						else
						{
							if (LogConfiguration.loggingIsEnabled())
							{
								logger.log(Level.FINE, "Index Successfully created.");
							}
							op.onSuccess();
						}
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

	private void updateIndex(final RequestOperation op, final Array<String> keyPath, final Map<DBIndexData> indexData, final Array<String> indexColumnNames, SQLTransaction tx)
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
				updateIndexEntries(keyPath, indexColumnNames, tx, rs);
				updateIndexMetadata(op, indexData, indexColumnNames, tx);
			}
		}, getErrorHandler());
    }
	
	private void updateIndexMetadata(final RequestOperation op, final Map<DBIndexData> indexData, final Array<String> indexColumnNames, final SQLTransaction tx)
    {
        DBUtil.encodeValue(indexData, new EncodeCallback()
		{
			@Override
			public void onEncode(String encoded)
			{
				String sql = "UPDATE __sys__ SET indexData = ?, indexColumns = ?  WHERE name = ?";
				if (LogConfiguration.loggingIsEnabled())
				{
					logger.log(Level.FINE, "Running SQL ["+sql+"].");
				}
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				args.push(encoded);
				args.push(DBUtil.encodeKey(indexColumnNames));
				args.push(getObjectStore().getName());
				tx.executeSQL(sql, args, new SQLTransaction.SQLStatementCallback()
				{
					@Override
					public void onSuccess(SQLTransaction tx, SQLResultSet rs)
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Index Successfully created.");
						}
						getObjectStore().setReadyState("createIndex-"+getName(), true);
						op.onSuccess();
					}
				}, getErrorHandler());
			}
		});
    }

	private void updateIndexEntries(final Array<String> keyPath, final Array<String> indexColumnNames, final SQLTransaction tx, SQLResultSet rs)
    {
        SQLResultSetRowList rows = rs.getRows();
		int length = rows.length();
		for (int i = 0; i < length; i++)
		{
			try
			{
				JavaScriptObject valueObject = DBUtil.decodeValue(JsUtils.readStringPropertyValue(rows.itemObject(i), "value"));
				StringBuilder sql = new StringBuilder("UPDATE \""+getObjectStore().getName()+"\"");
				JsArrayMixed args = JsArrayMixed.createArray().cast();
				for (int j=0; j<keyPath.size(); j++)
				{
					if (j==0)
					{
						sql.append(" SET ");
					}
					else
					{
						sql.append(",");
					}
					sql.append(indexColumnNames.get(j)).append("= ?");
					JsUtils.readPropertyValue(valueObject, keyPath.get(j), args);
				}
				sql.append(" WHERE key = ?");
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
	
	private Array<String> getColumnNames(Array<String> keyPath)
    {
		Array<String> columns = CollectionFactory.createArray();
		for (int i=0; i < keyPath.size(); i++)
		{
			columns.add(keyPath.get(i).replace('.', '_'));
		}
		
	    return columns;
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
}
