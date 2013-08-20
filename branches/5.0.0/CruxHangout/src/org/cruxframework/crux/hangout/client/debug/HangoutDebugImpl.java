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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.collection.Map;
import org.cruxframework.crux.hangout.client.Hangout;
import org.cruxframework.crux.hangout.client.HangoutData;
import org.cruxframework.crux.hangout.client.HangoutLayout;
import org.cruxframework.crux.hangout.client.data.Participant;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HangoutDebugImpl implements Hangout 
{
	private FastList<ParticipantsEnabledHandler> participantsEnabledHandlers;
	private FastList<ParticipantsDisabledHandler> participantsDisabledHandlers;
	private FastList<ParticipantsAddedHandler> participantsAddedHandlers;
	private FastList<ParticipantsRemovedHandler> participantsRemovedHandlers;

	private Map<Participant> participants;
	private Participant me;
	
	protected HangoutDebugImpl()
	{
		participants = CollectionFactory.createMap();
		me = createParticipant("1234", "User Test 1", true, true, true, 0, "111", "User Test 1", "", false, false);
		Participant dummy = createParticipant("1235", "User Test 2", true, true, true, 0, "112", "User Test 2", "", false, false);
		participants.put(me.getId(), me);
		participants.put(dummy.getId(), dummy);
		participantsAddedHandlers = new FastList<Hangout.ParticipantsAddedHandler>();
		participantsDisabledHandlers = new FastList<Hangout.ParticipantsDisabledHandler>();
		participantsEnabledHandlers = new FastList<Hangout.ParticipantsEnabledHandler>();
		participantsRemovedHandlers = new FastList<Hangout.ParticipantsRemovedHandler>();
		
	}
	
	private native Participant createParticipant(String id, String displayName, boolean hasAppEnabled, 
			    								boolean hasCamera, boolean hasMicrophone, int displayIndex, 
			    								String personId, String personDisplayName, String personImageUrl,
			    								boolean isBroadcaster, boolean isInBroadcast)/*-{
	    var ret = [];
	    ret.id = id;
	    ret.displayName = displayName;
	    ret.hasAppEnabled = hasAppEnabled;
	    ret.hasCamera = hasCamera;
	    ret.hasMicrophone = hasMicrophone;
	    ret.displayIndex = displayIndex;
	    ret.isBroadcaster = isBroadcaster;
	    ret.isInBroadcast = isInBroadcast;
	    ret.person = [];
	    ret.person.id = personId;
	    ret.person.displayName = personDisplayName;
	    ret.person.image = [];
	    ret.person.image.url = personImageUrl;
	    return ret;
    }-*/;

	public static final void loadHangoutApi(ApiReadyHandler handler)
	{
		handler.onApiReady(new HangoutDebugImpl());
	}
	
	public final Array<Participant> getAppEnabledParticipants()
	{
		Array<String> keys = participants.keys();
		Array<Participant> values = CollectionFactory.createArray();
		
		for (int i=0; i< keys.size(); i++)
		{
			values.add(participants.get(keys.get(i)));
		}
		
		return values;
	}

	public final String getHangoutURL()
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}

	public final String getHangoutIdentifier()
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}
	
	public final String getCurrentParticipantLocale()
	{
		return "en-US";
	}
	
	public final String getPreferredHangoutLocale()
	{
		return "en-US";
	}
	
	public final Participant getParticipant(String participantId)
	{
		return participants.get(participantId);
	}
	
	public final Participant getCurrentParticipant()
	{
		return me;
	}
	
	public final String getCurrentParticipantId()
	{
		return me.getId();
	}
	
	public final Array<Participant> getHangoutParticipants()
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}

	public final String getHangoutTopic()
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}

	public final void hideApplication()
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}
	
	public final boolean isHangoutApiReady()
	{
		return true;
	}
	
	public final boolean isApplicationVisible()
	{
		return true;
	}

	public final boolean isHangoutPublic()
	{
		return true;
	}
		
	public final HandlerRegistration addAppVisibleHandler(AppVisibleHandler handler){
		throw new UnsupportedException("Unsupported on debug mode.");
	}

	public final HandlerRegistration addParticipantsEnabledHandler(final ParticipantsEnabledHandler handler){
		participantsEnabledHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = participantsEnabledHandlers.indexOf(handler);
				participantsEnabledHandlers.remove(index);
			}
		};
	}
	
	public final HandlerRegistration addParticipantsDisabledHandler(final ParticipantsDisabledHandler handler){
		participantsDisabledHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = participantsDisabledHandlers.indexOf(handler);
				participantsDisabledHandlers.remove(index);
			}
		};
	}
	
	public final HandlerRegistration addParticipantsAddedHandler(final ParticipantsAddedHandler handler){
		participantsAddedHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = participantsAddedHandlers.indexOf(handler);
				participantsAddedHandlers.remove(index);
			}
		};
	}
	
	public final HandlerRegistration addParticipantsRemovedHandler(final ParticipantsRemovedHandler handler){
		participantsRemovedHandlers.add(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				int index = participantsRemovedHandlers.indexOf(handler);
				participantsRemovedHandlers.remove(index);
			}
		};
	}
	
	public final HandlerRegistration addPreferredLocaleChangedHandler(PreferredLocaleChangedHandler handler){
		throw new UnsupportedException("Unsupported on debug mode.");
	}
	
	public final HandlerRegistration addPublicChangedHandler(PublicChangedHandler handler){
		throw new UnsupportedException("Unsupported on debug mode.");
	}
	
	public final HandlerRegistration addTopicChangedHandler(TopicChangedHandler handler){
		throw new UnsupportedException("Unsupported on debug mode.");
	}
	
	public final HangoutData getHangoutData()
	{
		return new HangoutDataDebugImpl();
	}
	
	public final HangoutLayout getHangoutLayout()
	{
		throw new UnsupportedException("Unsupported on debug mode.");
	}
}
