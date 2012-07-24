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
package org.cruxframework.crux.crossdevice.client.slideshow.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Feed extends JavaScriptObject
{
	protected Feed(){}
	
	public final native JsArray<Entry> getEntries() /*-{
	    return this.feed.entry;
    }-*/;
	
	public final native String getTitle() /*-{
	    return this.feed.title.$t;
	}-*/;
	
	public final native String getIcon() /*-{
	    return this.feed.icon.$t;
	}-*/;
}
