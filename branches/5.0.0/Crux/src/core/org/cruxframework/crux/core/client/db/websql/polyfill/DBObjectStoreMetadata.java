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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBObjectStoreMetadata extends JavaScriptObject
{
	protected DBObjectStoreMetadata(){}
	
	public final native void setName(String name)/*-{
		this.name = name;
	}-*/;
	
	public final native String getName()/*-{
		return this.name;
	}-*/;
	
	public final native void setIndexList(String listAsString)/*-{
		this.indexList = listAsString;
	}-*/;
	
	public final native String getIndexList()/*-{
		return this.indexList;
	}-*/;
	
	public final native void setAutoInc(boolean autoIncrement)/*-{
		this.autoInc = autoIncrement;
	}-*/;
	
	public final native boolean isAutoInc()/*-{
		if (typeof this.autoInc == "string")
		{
			return (this.autoInc == 'true');
		}
		return this.autoInc;
	}-*/;

	public final native void setKeyPath(String keyPath)/*-{
		this.keyPath = keyPath;
	}-*/;

	public final native String getKeyPath()/*-{
		return this.keyPath;
	}-*/;
}