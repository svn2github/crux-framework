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
package org.cruxframework.crux.hangout.client.debug;

import java.util.Date;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.hangout.client.HangoutData;
import org.cruxframework.crux.hangout.client.data.StateMetadata;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HangoutDataDebugImpl implements HangoutData
{
	private Map<String> state;
	private Map<StateMetadata> stateMetadata;
	private FastList<StateChangedHandler> stateChangeHandlers;
	
	protected HangoutDataDebugImpl()
	{
		state = CollectionFactory.createMap();
		stateMetadata = CollectionFactory.createMap();
		stateChangeHandlers = new FastList<HangoutData.StateChangedHandler>();
	}
	
	public final void clearValue(String key)
	{
		if (state.containsKey(key))
		{
			state.remove(key);
			stateMetadata.remove(key);
			Array<String> removed = CollectionFactory.createArray();
			Array<StateMetadata> added = CollectionFactory.createArray();
			removed.add(key);
			fireStateChangeEvent(added, removed);
		}
	}
	
	public final Array<String> getKeys()
	{
		return state.keys();
	}

	public final String getValue(String key)
	{
		return state.get(key);
	}

	public final Map<String> getState()
	{
		return state;
	}
	public final Map<StateMetadata> getStateMetadata()
	{
		return stateMetadata;
	}
	
	public final void setValue(String key, String value)
	{
		state.put(key, value);
		StateMetadata metadata = stateMetadata.get(key);
		boolean isNewKey = (metadata == null);
		metadata = createMetadata(key, value, new Date().getTime(), 0);
		stateMetadata.put(key, metadata);
		Array<String> removed = CollectionFactory.createArray();
		Array<StateMetadata> added = CollectionFactory.createArray();
		if (isNewKey)
		{
			added.add(metadata);
		}
		fireStateChangeEvent(added, removed);
	}
	
	private native StateMetadata createMetadata(String key, String value, double timestamp, double timediff)/*-{
		var ret = [];
		ret.key = key;
		ret.value = value;
		ret.timestamp = timestamp;
		ret.timediff = timediff;
	    return ret;
	}-*/;

	public final void submitDelta(Map<String> updates, Array<String> removes)
	{
		Array<StateMetadata> added = CollectionFactory.createArray();

		Array<String> addedKeys = updates.keys();
		for (int i=0; i < addedKeys.size(); i++)
		{
			String key = addedKeys.get(i);
			state.put(key, updates.get(key));
			StateMetadata metadata = stateMetadata.get(key);
			if (metadata == null)
			{
				metadata = createMetadata(key, updates.get(key), new Date().getTime(), 0);
				stateMetadata.put(key, metadata);
			}
			added.add(metadata);
		}
		for (int i=0; i< removes.size(); i++)
		{
			String key = removes.get(i);
			clearValue(key);
		}
		
		fireStateChangeEvent(added, removes);
	}
	
	public final void sendMessage(String message)
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}

	public final HandlerRegistration addMessageReceivedHandler(MessageReceivedHandler handler)	
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}

	public final HandlerRegistration addStateChangedHandler(final StateChangedHandler handler)
	{
		stateChangeHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = stateChangeHandlers.indexOf(handler);
				stateChangeHandlers.remove(index);
			}
		};
	}
	
	private void fireStateChangeEvent(Array<StateMetadata> addedKeys, Array<String> removedKeys)
	{
		for (int i=0; i< stateChangeHandlers.size(); i++)
		{
			stateChangeHandlers.get(i).onStateChanged(addedKeys, stateMetadata, removedKeys, state);
		}
	}
}
