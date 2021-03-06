/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.hangout.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class StateMetadata extends JavaScriptObject
{
	protected StateMetadata(){}
	
	public final native String getKey()/*-{
		return this.key;
	}-*/;
	
	public final native String getValue()/*-{
		return this.value;
	}-*/;

	public final native double getTimestamp()/*-{
		return this.timestamp;
	}-*/;

	public final native double getTimeDiff()/*-{
		return this.timediff;
	}-*/;
	
	public final StateMetadata clone()
	{
		return createMetadata(getKey(), getValue(), getTimestamp(), getTimeDiff());
	}
	
	private native StateMetadata createMetadata(String key, String value, double timestamp, double timediff)/*-{
		var ret = [];
		ret.key = key;
		ret.value = value;
		ret.timestamp = timestamp;
		ret.timediff = timediff;
	    return ret;
	}-*/;

}
