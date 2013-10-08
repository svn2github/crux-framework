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
import org.cruxframework.crux.core.client.db.websql.SQLTransaction;
import org.cruxframework.crux.core.client.db.websql.SQLTransaction.SQLStatementErrorCallback;
import org.cruxframework.crux.core.client.utils.StringUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.logging.client.LogConfiguration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBTransaction extends JavaScriptObject
{
	private static Logger logger = Logger.getLogger(DBTransaction.class.getName());

	static final String READ = "readonly";
    static final String READ_WRITE = "readwrite";
    static final String VERSION_TRANSACTION = "versionchange";
 
	protected DBTransaction(){}
	
	public native final boolean isRunning()/*-{
		return this.running;
	}-*/;
	
	final native void setRunning(boolean val)/*-{
		this.running = val;
	}-*/;
	
	public native final boolean isActive()/*-{
		return this.active;
	}-*/;
	
	final native void setActive(boolean val)/*-{
		this.active = val;
	}-*/;
	
	public native final boolean isAborted()/*-{
		return this.aborted;
	}-*/;

	final native void setAborted(boolean val)/*-{
		this.aborted = val;
	}-*/;

	public final native String getMode()/*-{
		return this.mode;
	}-*/;
	
	final native void setMode(String val)/*-{
		this.mode = val;
	}-*/;
	
	public final native DBDatabase getDatabase()/*-{
		return this.db;
	}-*/;
	
	final native void setDatabase(DBDatabase val)/*-{
		this.db = val;
	}-*/;

	final native void setStoreNames(JsArrayString val)/*-{
		this.storeNames = val;
	}-*/;

	final native void setError(DBError val)/*-{
		this.error = val;
	}-*/;

	final native SQLTransaction getSQLTransaction()/*-{
		return this.sqlTransaction;
	}-*/;

	final native void setSQLTransaction(SQLTransaction tx)/*-{
		this.sqlTransaction = tx;
	}-*/;
	
	final native Array<RequestOperation> getRequests()/*-{
		return this.requests;
	}-*/;
	
	final native void setRequests(Array<RequestOperation> r)/*-{
		this.requests = r;
	}-*/;
	
    public static DBObjectStore objectStore(DBTransaction db, String objectStoreName)
    {
        return DBObjectStore.create(objectStoreName, db, true);
    }
    
    public static void abort(DBTransaction db)
    {
    	db.abort(true);
    }
    
    public final void abort(boolean fireEvent)
    {
        if (!isAborted() && isActive())
        {
        	if (getSQLTransaction() != null)
        	{
        		getSQLTransaction().executeSQL("invalid sql statement", null, null, new SQLStatementErrorCallback()
        		{
        			@Override
        			public boolean onError(SQLTransaction tx, SQLError error)
        			{
        				return true;// tell web sql to rollback transaction
        			}
        		});
        		
        		setSQLTransaction(null);
        	}
        	setAborted(true);
        	setActive(false);
        	setRunning(false);
        	getRequests().clear();
        	if (fireEvent)
        	{
        		DBEvent evt = DBEvent.create("abort");
        		DBEvent.invoke("onabort", this, evt);
        	}
        }
    }
    
	protected final DBRequest addToTransactionQueue(RequestOperation operation, JavaScriptObject source, String[] supportedModes)
	{
		if (!isActive() && !StringUtils.unsafeEquals(getMode(), VERSION_TRANSACTION)) 
		{
			throwError("not active", "A request was placed against a transaction which is currently not active, or which is finished.");
			return null;
		}
		if (!isAllowedOperation(supportedModes))
		{
			throwError("Unsupported Operation", "The requested operation is not supported on ["+getMode()+"] transaction.");
			return null;
		}
		DBRequest request = createRequest(source);
		pushToQueue(request, operation);       
		return request;
	}
	    
	protected final DBRequest createRequest(JavaScriptObject source)
	{
		DBRequest request = DBRequest.create();
		request.setSource(source);
		request.setTransaction(this);
		return request;
	}
	
	protected final void pushToQueue(DBRequest request, RequestOperation operation)//args 
	{		
		getRequests().add(operation);
		operation.setTransaction(this);
		operation.setRequestIndex(getRequests().size()-1);
		operation.setRequest(request);
        // Start the queue for executing the requests
        executeRequests();
    }

	protected final boolean isAllowedOperation(String[] supportedModes)
	{
		for (String supported : supportedModes)
        {
	        if (StringUtils.unsafeEquals(getMode(), supported))
	        {
	        	return true;
	        }
        }
		//DBUtil.throwDOMException("ReadOnlyError", message)throwError(errorName, message)
		return false;
	}
	
    protected final DBError throwError(DBError error)
    {
    	return throwError(error.getName(), error.getMessage());
    }
    
    protected final DBError throwError(String errorName, String message)
    {
		if (LogConfiguration.loggingIsEnabled())
		{
			logger.log(Level.SEVERE, "An error has occurred on transaction. Error name [" +errorName+"]. Error message ["+message+"]");
		}
    	abort(false);
		DBEvent evt = DBEvent.create("error");
		DBError error = DBError.create(errorName, message);
		setError(error);
		DBEvent.invoke("onerror", this, evt);
		return error;
    }
	
	private final void executeRequests()
	{
		if (isRunning() && !StringUtils.unsafeEquals(getMode(), VERSION_TRANSACTION))
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.INFO, "Looks like the request set is already running. Transaction mode[" +getMode()+"].");
			}
			return;
		}
		setRunning(true);
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				if (!StringUtils.unsafeEquals(getMode(), VERSION_TRANSACTION) && !isActive())
				{
					throwError("not active", "A request was placed against a transaction which is currently not active, or which is finished");
				}
				
				SQLDatabase.SQLTransactionCallback transactionCallback = new SQLDatabase.SQLTransactionCallback()
				{
					@Override
					public void onTransaction(SQLTransaction tx)
					{
						setSQLTransaction(tx);
						executeRequest(0);
					}

				};
				SQLDatabase.SQLTransactionErrorCallback transactionErrorCallback = new SQLDatabase.SQLTransactionErrorCallback()
				{
					@Override
					public void onError(SQLError error)
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.SEVERE, "An error in transaction. Error name [" +error.getName()+"]. Error message ["+error.getMessage()+"]");
						}
						throwError(error.getName(), error.getMessage());
					}
				};
				SQLDatabase.SQLCallback successCallback = new SQLDatabase.SQLCallback()
				{
					@Override
					public void onSuccess()
					{
						if (LogConfiguration.loggingIsEnabled())
						{
							logger.log(Level.FINE, "Transaction completed.");
						}
						fireOnComplete();
						setRunning(false);
					}
				};
				if (StringUtils.unsafeEquals(READ, getMode()))
				{
					getDatabase().getSQLDatabase().readTransaction(transactionCallback, transactionErrorCallback, successCallback);
				}
				else
				{
					getDatabase().getSQLDatabase().transaction(transactionCallback, transactionErrorCallback, successCallback);
				}
			}
		});
	}
	
	private final void executeRequest(int requestIdx)
    {
		try
		{
			if (requestIdx >= getRequests().size())
			{
//				fireOnComplete();
				return;
			}
			
			final RequestOperation op = getRequests().get(requestIdx);
			op.doOperation(getSQLTransaction());
		}
		catch (Exception e) 
		{
			if (LogConfiguration.loggingIsEnabled())
			{
				logger.log(Level.SEVERE, "An exception occured in transaction. Error message ["+e.getMessage()+"]", e);
			}
			throwError("Request Error", e.getMessage());
		}
    }
	
	private final native void handleObjectNativeFunctions(DBTransaction db)/*-{
		this.abort = function(){
			@org.cruxframework.crux.core.client.db.websql.polyfill.DBTransaction::abort(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBTransaction;)(db);
		};

		this.objectStore = function(objectStoreName){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBTransaction::objectStore(Lorg/cruxframework/crux/core/client/db/websql/polyfill/DBTransaction;Ljava/lang/String;)(db,objectStoreName);
		};
		
		this.onabort= null;
		this.onerror= null;
		this.oncomplete= null;
	}-*/;

	private void fireOnComplete()
	{
		setActive(false);
		setRunning(false);
		getRequests().clear();
		DBEvent evt = DBEvent.create("complete");
		DBEvent.invoke("oncomplete", this, evt);
	}
	
	public static DBTransaction create(JsArrayString storeNames, String mode, DBDatabase db)
	{
		DBTransaction transaction = createObject().cast();
	    for (int i = 0; i < storeNames.length(); i++) 
	    {
	    	if (db.getObjectStoreNames().indexOf(storeNames.get(i)) < 0) 
	    	{
	    		DBUtil.throwDOMException("not found", 
	    				"The operation failed because the requested database object could not be found. For example, an object store did not exist but was being opened. Object name["+ 
	    				storeNames.get(i)+"]");
	    	}
	    }
	    transaction.setActive(true);
		transaction.setAborted(false);
		transaction.setRunning(false);
		transaction.setStoreNames(storeNames);
		transaction.setMode(mode);
		transaction.setDatabase(db);
		transaction.setError(null);
		Array<RequestOperation> requests = Array.createArray().cast();
		transaction.setRequests(requests);
		transaction.handleObjectNativeFunctions(transaction);
		return transaction;
	}
	
	public static DBTransaction create(JsArrayString storeNames, int mode, DBDatabase db)
	{
		String strMode;
		switch (mode)
        {
        	case 1:
        		strMode = READ_WRITE;
	        break;
        	case 2:
        		strMode = VERSION_TRANSACTION;
	        break;

        	default:
        		strMode = READ;
	        break;
        }
		
		return create(storeNames, strMode, db);
	}
	 
	static native void registerStaticFunctions()/*-{
		$wnd.__db_bridge__ = $wnd.__db_bridge__ || {};
		$wnd.__db_bridge__.IDBTransaction = {};
		$wnd.__db_bridge__.IDBTransaction.READ = "readonly";
		$wnd.__db_bridge__.IDBTransaction.READ_WRITE = "readwrite";
		$wnd.__db_bridge__.IDBTransaction.VERSION_TRANSACTION = "versionchange";
	}-*/;
	
	public static abstract class RequestOperation
	{
		private DBTransaction dbTransaction;
		private int requestIdx;
		private DBRequest request;

		public abstract void doOperation(SQLTransaction tx);
		
		private void setTransaction(DBTransaction dbTransaction)
		{
			this.dbTransaction = dbTransaction;
		}
		
		private void setRequestIndex(int requestIdx)
		{
			this.requestIdx = requestIdx;
		}

		protected void setRequest(DBRequest request)
		{
			this.request = request;
		}
		
		protected void onSuccess()
		{
			DBEvent evt = DBEvent.create("success");
			request.setReadyState("done");
			request.setError(null); 
            DBEvent.invoke("onsuccess", request, evt);
            dbTransaction.executeRequest(requestIdx+1);
		}
		
		protected void setResult(JavaScriptObject object)
		{
			request.setResult(object);
		}
		
		protected void setContentAsResult(JsArrayMixed object)
		{
			request.setContentAsResult(object);
		}

	    protected void throwError(String errorName, String message)
	    {
			DBError error = DBError.create(errorName, message);
	    	request.setReadyState("done");
	    	request.setError(error);
	    	request.setResult((String)null);
			DBEvent evt = DBEvent.create("error");
			DBEvent.invoke("onerror", request, evt);
	    	dbTransaction.throwError(error);
	    }
	}
}