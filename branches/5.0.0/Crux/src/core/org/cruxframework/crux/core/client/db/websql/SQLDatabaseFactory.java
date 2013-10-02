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
package org.cruxframework.crux.core.client.db.websql;

import com.google.gwt.dom.client.PartialSupport;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@PartialSupport
public class SQLDatabaseFactory
{
	public static SQLDatabase openDatabaseNative(String name, String version, String displayName, double estimatedSize)
	{
		return openDatabaseNative(name, version, displayName, estimatedSize, null);
	}

	public static native SQLDatabase openDatabaseNative(String name, String version, String displayName, double estimatedSize, DatabaseCallback creationCallback)/*-{
		return $wnd.openDatabase(name, version, displayName, estimatedSize, function(db){
			if (creationCallback)
			{
				creationCallback.@org.cruxframework.crux.core.client.db.websql.SQLDatabaseFactory.DatabaseCallback::(Lorg/cruxframework/crux/core/client/db/websql/SQLDatabase;)(db);
			}
		});
	}-*/;
	
	public static native boolean isSupported()/*-{
		var sqlsupport = !!$wnd.openDatabase;
		return sqlsupport;
	}-*/;
	
	public static interface DatabaseCallback
	{
		void onCreated(SQLDatabase db);
	}
}
