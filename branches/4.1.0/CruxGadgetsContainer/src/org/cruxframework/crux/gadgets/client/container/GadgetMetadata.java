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
package org.cruxframework.crux.gadgets.client.container;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetMetadata extends JavaScriptObject
{
	protected GadgetMetadata() {}
	
	public final native String getUrl()/*-{
		return this.url;
	}-*/;

	public final native String getTitle()/*-{
		return this.title;
	}-*/;

	public final native JavaScriptObject getUserPrefs()/*-{
		return this.userPrefs;
	}-*/;
	
	public final native int getHeight()/*-{
		return this.height;
	}-*/;
	
	public final native int getWidth()/*-{
		return this.width;
	}-*/;
	
}