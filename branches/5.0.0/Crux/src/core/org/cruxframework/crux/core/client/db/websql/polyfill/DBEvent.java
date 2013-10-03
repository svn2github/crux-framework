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
import com.google.gwt.core.client.JsDate;

/**
 * @author Thiago da Rosa de Bustamante
 */
public class DBEvent extends JavaScriptObject
{
	protected DBEvent() {}
	
	public final native String getType()/*-{
		return this.type;
	}-*/;
	
	public final native boolean isDebug()/*-{
		return this.debug;
	}-*/;
	
	public final native boolean isBubbles()/*-{
		return this.bubbles;
	}-*/;

	public final native boolean isCancelable()/*-{
		return this.cancelable;
	}-*/;

	public final native int getEventPhase()/*-{
		return this.eventPhase;
	}-*/;

	public final native JsDate getTimeStamp()/*-{
		return this.timeStamp;
	}-*/;
	
	public static final native DBEvent create(String type)/*-{
		return {
			"type": type,
			bubbles: false,
			cancelable: false,
			eventPhase: 0,
			timeStamp: new Date()
		};
	}-*/;
	
	public static native void invoke(String handler, JavaScriptObject context, DBEvent event)/*-{
        event.target = context;
        (typeof context[handler] === "function") && context[handler].apply(context, [event]);
	}-*/;
}


