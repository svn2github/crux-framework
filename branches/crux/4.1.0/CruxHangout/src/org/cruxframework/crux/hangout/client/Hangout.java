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
import org.cruxframework.crux.hangout.client.data.Participant;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface Hangout 
{
	Array<Participant> getAppEnabledParticipants();
	String getHangoutURL();
	String getHangoutIdentifier();
	String getCurrentParticipantLocale();
	String getPreferredHangoutLocale();
	Participant getParticipant(String participantId);
	Participant getCurrentParticipant();
	String getCurrentParticipantId();
	Array<Participant> getHangoutParticipants();
	String getHangoutTopic();
	void hideApplication();
	boolean isHangoutApiReady();
	boolean isApplicationVisible();
	boolean isHangoutPublic();
	HandlerRegistration addAppVisibleHandler(AppVisibleHandler handler);
	HandlerRegistration addParticipantsEnabledHandler(ParticipantsEnabledHandler handler);
	HandlerRegistration addParticipantsDisabledHandler(ParticipantsDisabledHandler handler);
	HandlerRegistration addParticipantsAddedHandler(ParticipantsAddedHandler handler);
	HandlerRegistration addParticipantsRemovedHandler(ParticipantsRemovedHandler handler);
	HandlerRegistration addPreferredLocaleChangedHandler(PreferredLocaleChangedHandler handler);
	HandlerRegistration addPublicChangedHandler(PublicChangedHandler handler);
	HandlerRegistration addTopicChangedHandler(TopicChangedHandler handler);
	HangoutData getHangoutData();
	HangoutLayout getHangoutLayout();
	
	public static interface ApiReadyHandler
	{
		void onApiReady(Hangout hangout);
	}
	
	public static interface AppVisibleHandler
	{
		void onAppVisibilityChange(boolean visible);
	}

	public static interface ParticipantsEnabledHandler
	{
		void onParticipantsEnabled(Array<Participant> participants);
	}

	public static interface ParticipantsDisabledHandler
	{
		void onParticipantsDisabled(Array<Participant> participants);
	}

	public static interface ParticipantsAddedHandler
	{
		void onParticipantsAdded(Array<Participant> participants);
	}

	public static interface ParticipantsRemovedHandler
	{
		void onParticipantsRemoved(Array<Participant> participants);
	}

	public static interface PreferredLocaleChangedHandler
	{
		void onPreferredLocaleChanged(String preferredLocale);
	}

	public static interface PublicChangedHandler
	{
		void onPublicChanged(boolean isPublic);
	}

	public static interface TopicChangedHandler
	{
		void onTopicChanged(String topic);
	}
}
