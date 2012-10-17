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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.hangout.client.data.StateMetadata;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HangoutData extends JavaScriptObject
{
	protected HangoutData(){}
	
	public final native void clearValue(String key)/*-{
		this.clearValue(key);
	}-*/;
	
	public final native Array<String> getKeys()/*-{
		return this.getKeys();
	}-*/;

	public final native String getValue(String key)/*-{
		return this.getValue(key);
	}-*/;

	public final native Map<String> getState()/*-{
		return this.getState();
	}-*/;

	public final native Map<StateMetadata> getStateMetadata()/*-{
		return this.getStateMetadata();
	}-*/;

	public final native void setValue(String key, String value)/*-{
		this.setValue(key, value);
	}-*/;

	public final native void submitDelta(Map<String> updates, Array<String> removes)/*-{
		this.submitDelta(updates, removes);
	}-*/;
	
	public final native void sendMessage(String message)/*-{
		this.sendMessage(message);
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
		this.onMessageReceived.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddMessageReceivedHandler(MessageReceivedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.HangoutData.MessageReceivedHandler::onMessageReceived(Ljava/lang/String;Ljava/lang/String;)(eventObj.senderId,eventObj.message);
		};
		this.onMessageReceived.add(f);
		return f;
	}-*/;

	private native void nativeRemoveStateChangedHandler(JavaScriptObject func)/*-{
		this.onStateChanged.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddStateChangedHandler(StateChangedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.HangoutData.StateChangedHandler::onStateChanged(Lorg/cruxframework/crux/core/client/collection/Array;Lorg/cruxframework/crux/core/client/collection/Map;Lorg/cruxframework/crux/core/client/collection/Array;Lorg/cruxframework/crux/core/client/collection/Map;)(eventObj.addedKeys,eventObj.metadata,eventObj.removedKeys,eventObj.state);
		};
		this.onStateChanged.add(f);
		return f;
	}-*/;

	public static interface MessageReceivedHandler
	{
		void onMessageReceived(String senderId, String message);
	}
	
	public static interface StateChangedHandler
	{
		void onStateChanged(Array<StateMetadata> addedKeys, Map<StateMetadata> metadata, Array<String> removedKeys, Map<String> state);
	}
}
