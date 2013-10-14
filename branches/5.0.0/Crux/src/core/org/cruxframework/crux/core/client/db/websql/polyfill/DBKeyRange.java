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
import com.google.gwt.core.client.JsArrayMixed;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DBKeyRange extends JavaScriptObject
{
	protected DBKeyRange (){}
	
	public final native boolean isLowerOpen()/*-{
		if (this.lowerOpen)
		{
			return true
		}
		return false;
	}-*/;
	
	public final native boolean isUpperOpen()/*-{
		if (this.upperOpen)
		{
			return true
		}
		return false;
	}-*/;
	
	public final native boolean hasLowerBound()/*-{
		if (typeof this.lower !== "undefined")
		{
			return true
		}
		return false;
	}-*/;
	
	public final native boolean hasUpperBound()/*-{
		if (typeof this.upper !== "undefined")
		{
			return true
		}
		return false;
	}-*/;

	public final native JsArrayMixed getBounds()/*-{
		var result = [];
		if (this.lower)
		{
			result.push(this.lower);
		}
		if (this.upper)
		{
			result.push(this.upper);
		}
		return result;
	}-*/;

	public final native JsArrayMixed getBounds(int index)/*-{
		var result = [];
		if (this.lower)
		{
			result.push(this.lower[index]);
		}
		if (this.upper)
		{
			result.push(this.upper[index]);
		}
		return result;
	}-*/;

	private static native DBKeyRange createNative(JsArrayMixed bounds)/*-{
		return {
			lower: bounds[0], 
			upper: bounds[1], 
			lowerOpen: bounds[2], 
			upperOpen: bounds[3], 
		};
	}-*/;

	static native void registerStaticFunctions()/*-{
		$wnd.__db_bridge__ = $wnd.__db_bridge__ || {};
		$wnd.__db_bridge__.IDBKeyRange = {};
		$wnd.__db_bridge__.IDBKeyRange.only = function(value){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBKeyRange::createNative(Lcom/google/gwt/core/client/JsArrayMixed;)([value, value, false, false]);
		};
		$wnd.__db_bridge__.IDBKeyRange.lowerBound = function(value, open){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBKeyRange::createNative(Lcom/google/gwt/core/client/JsArrayMixed;)([value, undefined, open, undefined]);
		};
		$wnd.__db_bridge__.IDBKeyRange.upperBound = function(value, open){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBKeyRange::createNative(Lcom/google/gwt/core/client/JsArrayMixed;)([undefined, value, undefined, open]);
		};
		$wnd.__db_bridge__.IDBKeyRange.bound = function(lower, upper, lowerOpen, upperOpen){
			return @org.cruxframework.crux.core.client.db.websql.polyfill.DBKeyRange::createNative(Lcom/google/gwt/core/client/JsArrayMixed;)([lower, upper, lowerOpen, upperOpen]);
		};
	}-*/;
}
