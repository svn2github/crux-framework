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
package org.cruxframework.crux.hangout.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Participant extends JavaScriptObject
{
	protected Participant(){}
	
	public final native String getId()/*-{
		return this.id;
	}-*/;
	
	public final native int getDisplayIndex()/*-{
		return (this.displayIndex?this.displayIndex:0);
	}-*/;

	public final native boolean hasMicrophoneInstalled()/*-{
		return (this.hasMicrophone?true:false);
	}-*/;

	public final native boolean hasCameraInstalled()/*-{
		return (this.hasCamera?true:false);
	}-*/;

	public final native boolean hasApplicationEnabled()/*-{
		return (this.hasAppEnabled?true:false);
	}-*/;

	public final native boolean isTheBroadcaster()/*-{
		return (this.isBroadcaster?true:false);
	}-*/;

	public final native boolean isInBroadcastHangout()/*-{
		return (this.isInBroadcast?true:false);
	}-*/;
	
	public final native String getDisplayName()/*-{
		return this.person.displayName;
	}-*/;

	public final native String getImageUrl()/*-{
		return this.person.image.url;
	}-*/;
	
	public final native String getPersonId()/*-{
		return this.person.id;
	}-*/;
}
