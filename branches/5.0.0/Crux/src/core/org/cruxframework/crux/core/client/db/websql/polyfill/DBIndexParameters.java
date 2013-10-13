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
public class DBIndexParameters extends JavaScriptObject
{
	protected DBIndexParameters(){}
	
    public final native void setUnique(boolean value) /*-{
		this.unique = value;
	}-*/;
	
	public final native void setMultiEntry(boolean value) /*-{
		this.multiEntry = value;
	}-*/;
	
	public final native boolean isMultiEntry()/*-{
		return this.multiEntry;
	}-*/;
	
    public final native boolean isUnique() /*-{
		return this.unique;
	}-*/;
	
	public final static DBIndexParameters create() 
	{
		DBIndexParameters res = JavaScriptObject.createObject().cast();
		res.setUnique(false);
		res.setMultiEntry(false);
		return res;
	}
	
	public final static DBIndexParameters create(boolean unique,boolean multiEntry) 
	{
		DBIndexParameters res = JavaScriptObject.createObject().cast();
		res.setUnique(unique);
		res.setMultiEntry(multiEntry);
		return res;
	}
}