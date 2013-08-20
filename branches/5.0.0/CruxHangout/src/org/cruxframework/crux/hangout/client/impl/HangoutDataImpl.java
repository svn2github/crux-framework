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
package org.cruxframework.crux.hangout.client.impl;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.hangout.client.HangoutData;
import org.cruxframework.crux.hangout.client.data.StateMetadata;
//
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HangoutDataImpl extends JavaScriptObject implements HangoutData
{
	protected HangoutDataImpl(){}
	
	public final native void clearValue(String key)/*-{
		$wnd.gapi.hangout.data.clearValue(key);
	}-*/;
	
	public final native Array<String> getKeys()/*-{
		return $wnd.gapi.hangout.data.getKeys();
	}-*/;

	public final native String getValue(String key)/*-{
		return $wnd.gapi.hangout.data.getValue(key);
	}-*/;

	public final native Map<String> getState()/*-{
		return $wnd.gapi.hangout.data.getState();
	}-*/;

	public final native Map<StateMetadata> getStateMetadata()/*-{
		return $wnd.gapi.hangout.data.getStateMetadata();
	}-*/;

	public final native void setValue(String key, String value)/*-{
		$wnd.gapi.hangout.data.setValue(key, value);
	}-*/;

	public final native void submitDelta(Map<String> updates, Array<String> removes)/*-{
		$wnd.gapi.hangout.data.submitDelta(updates, removes);
	}-*/;
	
	public final native void sendMessage(String message)/*-{
		$wnd.gapi.hangout.data.sendMessage(message);
	}-*/;
	
	public final HandlerRegistration addMessageReceivedHandler(MessageReceivedHandler handler){
		final JavaScriptObject func = nativeAddMessageReceivedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveMessageReceivedHandler(func);
			}
		};
	}

	public final HandlerRegistration addStateChangedHandler(StateChangedHandler handler){
		final JavaScriptObject func = nativeAddStateChangedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveStateChangedHandler(func);
			}
		};
	}

	private native void nativeRemoveMessageReceivedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.data.onMessageReceived.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddMessageReceivedHandler(MessageReceivedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.HangoutData.MessageReceivedHandler::onMessageReceived(Ljava/lang/String;Ljava/lang/String;)(eventObj.senderId,eventObj.message);
		};
		$wnd.gapi.hangout.data.onMessageReceived.add(f);
		return f;
	}-*/;

	private native void nativeRemoveStateChangedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.data.onStateChanged.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddStateChangedHandler(StateChangedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.HangoutData.StateChangedHandler::onStateChanged(Lorg/cruxframework/crux/core/client/collection/Array;Lorg/cruxframework/crux/core/client/collection/Map;Lorg/cruxframework/crux/core/client/collection/Array;Lorg/cruxframework/crux/core/client/collection/Map;)(eventObj.addedKeys,eventObj.metadata,eventObj.removedKeys,eventObj.state);
		};
		$wnd.gapi.hangout.data.onStateChanged.add(f);
		return f;
	}-*/;
}
