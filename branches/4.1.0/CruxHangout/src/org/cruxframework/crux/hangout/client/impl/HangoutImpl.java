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
import org.cruxframework.crux.hangout.client.Hangout;
import org.cruxframework.crux.hangout.client.HangoutData;
import org.cruxframework.crux.hangout.client.HangoutLayout;
import org.cruxframework.crux.hangout.client.data.Participant;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HangoutImpl extends JavaScriptObject implements Hangout 
{
	protected HangoutImpl(){}
	
	public static final void loadHangoutApi(ApiReadyHandler handler)
	{
		loadHangoutApi(handler, getHagoutObject());
	}
	
	public final native Array<Participant> getAppEnabledParticipants()/*-{
		return $wnd.gapi.hangout.getEnabledParticipants();
	}-*/;

	public final native String getHangoutURL()/*-{
		return $wnd.gapi.hangout.getHangoutUrl();
	}-*/;

	public final native String getHangoutIdentifier()/*-{
		return $wnd.gapi.hangout.getHangoutId();
	}-*/;
	
	public final native String getCurrentParticipantLocale()/*-{
		return $wnd.gapi.hangout.getLocalParticipantLocale();
	}-*/;
	
	public final native String getPreferredHangoutLocale()/*-{
		return $wnd.gapi.hangout.getPreferredLocale();
	}-*/;
	
	public final native Participant getParticipant(String participantId)/*-{
		return $wnd.gapi.hangout.getParticipantById(participantId);
	}-*/;
	
	public final native Participant getCurrentParticipant()/*-{
		return $wnd.gapi.hangout.getLocalParticipant();
	}-*/;
	
	public final native String getCurrentParticipantId()/*-{
		return $wnd.gapi.hangout.getLocalParticipantId();
	}-*/;
	
	public final native Array<Participant> getHangoutParticipants()/*-{
		return $wnd.gapi.hangout.getParticipants();
	}-*/;

	public final native String getHangoutTopic()/*-{
		return $wnd.gapi.hangout.getTopic();
	}-*/;

	public final native void hideApplication()/*-{
		$wnd.gapi.hangout.hideApp();
	}-*/;
	
	public final native boolean isHangoutApiReady()/*-{
		return ($wnd.gapi.hangout.isApiReady()?true:false);
	}-*/;
	
	public final native boolean isApplicationVisible()/*-{
		return ($wnd.gapi.hangout.isAppVisible()?true:false);
	}-*/;

	public final native boolean isHangoutPublic()/*-{
		return ($wnd.gapi.hangout.isPublic()?true:false);
	}-*/;
		
	public final HandlerRegistration addAppVisibleHandler(AppVisibleHandler handler){
		final JavaScriptObject func = nativeAddAppVisibleHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveAppVisibleHandler(func);
			}
		};
	}

	public final HandlerRegistration addParticipantsEnabledHandler(ParticipantsEnabledHandler handler){
		final JavaScriptObject func = nativeAddParticipantsEnabledHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveParticipantsEnabledHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addParticipantsDisabledHandler(ParticipantsDisabledHandler handler){
		final JavaScriptObject func = nativeAddParticipantsDisabledHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveParticipantsDisabledHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addParticipantsAddedHandler(ParticipantsAddedHandler handler){
		final JavaScriptObject func = nativeAddParticipantsAddedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveParticipantsAddedHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addParticipantsRemovedHandler(ParticipantsRemovedHandler handler){
		final JavaScriptObject func = nativeAddParticipantsRemovedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveParticipantsRemovedHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addPreferredLocaleChangedHandler(PreferredLocaleChangedHandler handler){
		final JavaScriptObject func = nativeAddPreferredLocaleChangedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemovePreferredLocaleChangedHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addPublicChangedHandler(PublicChangedHandler handler){
		final JavaScriptObject func = nativeAddPublicChangedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemovePublicChangedHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addTopicChangedHandler(TopicChangedHandler handler){
		final JavaScriptObject func = nativeAddTopicChangedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveTopicChangedHandler(func);
			}
		};
	}
	
	public final HangoutData getHangoutData()
	{
		return getHangoutDataImpl();
	}
	
	public final HangoutLayout getHangoutLayout()
	{
		return getHangoutLayoutImpl();
	}

	private native HangoutDataImpl getHangoutDataImpl()/*-{
		return [];
	}-*/;
	
	private native HangoutLayoutImpl getHangoutLayoutImpl()/*-{
		return [];
	}-*/;

	private static void loadHangoutApi(final ApiReadyHandler handler, final HangoutImpl hangout)
    {
		if (hangout == null)
		{
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
			{
				@Override
				public boolean execute()
				{
					HangoutImpl hagoutObject = getHagoutObject();
					if (hagoutObject == null)
					{
						return true;
					}
					else
					{
						loadHangoutApi(handler, hagoutObject);
						return false;
					}
				}
			}, 50);
		}
		else
		{
			if (hangout.isHangoutApiReady())
			{
				handler.onApiReady(hangout);
			}
			else
			{
				hangout.nativeAddApiReadyHandler(handler);
			}
		}
    }
	
	private static native HangoutImpl getHagoutObject()/*-{
		return [];
	}-*/;

	private native void nativeAddApiReadyHandler(ApiReadyHandler handler)/*-{
		$wnd.gapi.hangout.onApiReady.add(function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ApiReadyHandler::onApiReady(Lorg/cruxframework/crux/hangout/client/Hangout;)(this);
		});
	}-*/;

	private native void nativeRemoveAppVisibleHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onAppVisible.remove(func);
	}-*/;

	private native JavaScriptObject nativeAddAppVisibleHandler(AppVisibleHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.AppVisibleHandler::onAppVisibilityChange(Z)(eventObj.isAppVisible);
		};
		$wnd.gapi.hangout.onAppVisible.add(f);
		return f;
	}-*/;

	private native void nativeRemoveParticipantsEnabledHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onParticipantsEnabled.remove(func);
	}-*/;

	private native JavaScriptObject nativeAddParticipantsEnabledHandler(ParticipantsEnabledHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ParticipantsEnabledHandler::onParticipantsEnabled(Lorg/cruxframework/crux/core/client/collection/Array;)(eventObj.enabledParticipants);
		};
		$wnd.gapi.hangout.onParticipantsEnabled.add(f);
		return f;
	}-*/;
	
	private native void nativeRemoveParticipantsDisabledHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onParticipantsDisabled.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddParticipantsDisabledHandler(ParticipantsDisabledHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ParticipantsDisabledHandler::onParticipantsDisabled(Lorg/cruxframework/crux/core/client/collection/Array;)(eventObj.disabledParticipants);
		};
		$wnd.gapi.hangout.onParticipantsDisabled.add(f);
		return f;
	}-*/;
	
	private native void nativeRemoveParticipantsAddedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onParticipantsAdded.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddParticipantsAddedHandler(ParticipantsAddedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ParticipantsAddedHandler::onParticipantsAdded(Lorg/cruxframework/crux/core/client/collection/Array;)(eventObj.addedParticipants);
		};
		$wnd.gapi.hangout.onParticipantsAdded.add(f);
		return f;
	}-*/;
	
	private native void nativeRemoveParticipantsRemovedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onParticipantsRemoved.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddParticipantsRemovedHandler(ParticipantsRemovedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ParticipantsRemovedHandler::onParticipantsRemoved(Lorg/cruxframework/crux/core/client/collection/Array;)(eventObj.removedParticipants);
		};
		$wnd.gapi.hangout.onParticipantsRemoved.add(f);
		return f;
	}-*/;

	private native void nativeRemovePreferredLocaleChangedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onPreferredLocaleChanged.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddPreferredLocaleChangedHandler(PreferredLocaleChangedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.PreferredLocaleChangedHandler::onPreferredLocaleChanged(Ljava/lang/String;)(eventObj.preferredLocale);
		};
		$wnd.gapi.hangout.onPreferredLocaleChanged.add(f);
		return f;
	}-*/;
	
	private native void nativeRemovePublicChangedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onPublicChanged.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddPublicChangedHandler(PublicChangedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.PublicChangedHandler::onPublicChanged(Z)(eventObj.isPublic);
		};
		$wnd.gapi.hangout.onPublicChanged.add(f);
		return f;
	}-*/;
	
	private native void nativeRemoveTopicChangedHandler(JavaScriptObject func)/*-{
		$wnd.gapi.hangout.onTopicChanged.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddTopicChangedHandler(TopicChangedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.TopicChangedHandler::onTopicChanged(Ljava/lang/String;)(eventObj.topic);
		};
		$wnd.gapi.hangout.onTopicChanged.add(f);
		return f;
	}-*/;
}
