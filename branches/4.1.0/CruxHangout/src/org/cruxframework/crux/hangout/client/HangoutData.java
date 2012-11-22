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

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HangoutData
{
	void clearValue(String key);
	Array<String> getKeys();
	String getValue(String key);
	Map<String> getState();
	Map<StateMetadata> getStateMetadata();
	void setValue(String key, String value);
	void submitDelta(Map<String> updates, Array<String> removes);
	void sendMessage(String message);
	HandlerRegistration addMessageReceivedHandler(MessageReceivedHandler handler);
	HandlerRegistration addStateChangedHandler(StateChangedHandler handler);

	public static interface MessageReceivedHandler
	{
		void onMessageReceived(String senderId, String message);
	}
	
	public static interface StateChangedHandler
	{
		void onStateChanged(Array<StateMetadata> addedKeys, Map<StateMetadata> metadata, Array<String> removedKeys, Map<String> state);
	}
}
