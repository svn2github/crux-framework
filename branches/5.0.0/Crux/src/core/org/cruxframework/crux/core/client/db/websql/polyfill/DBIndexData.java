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

import org.cruxframework.crux.core.client.collection.Array;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBIndexData extends JavaScriptObject
{
	protected DBIndexData(){}
	
	public final native void setColumnNames(Array<String> columns)/*-{
		this.columnNames = columns;
	}-*/;
	
	public final native Array<String> getColumnNames()/*-{
		return this.columnNames;
	}-*/;
	
	public final native void setKeyPaths(Array<String> paths)/*-{
		this.keyPaths = paths;
	}-*/;
	
	public final native Array<String> getKeyPaths()/*-{
		return this.keyPaths;
	}-*/;

	public final native void setIndexParameters(DBIndexParameters optionalParameters)/*-{
		this.indexParameters = optionalParameters;
    }-*/;
	
	public final native DBIndexParameters getIndexParameters()/*-{
		return this.indexParameters;
	}-*/;
}