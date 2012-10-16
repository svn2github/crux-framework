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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Hangout extends JavaScriptObject
{
	protected Hangout(){}
	
	public static final void loadHagoutApi(ApiReadyHandler handler)
	{
		loadHangoutApi(handler, getHagoutObject());
	}
	
	public final native Array<Participant> getAppEnabledParticipants()/*-{
		return this.getEnabledParticipants();
	}-*/;

	public final native String getHangoutURL()/*-{
		return this.getHangoutUrl();
	}-*/;

	public final native String getHangoutIdentifier()/*-{
		return this.getHangoutId();
	}-*/;
	
	public final native String getCurrentParticipantLocale()/*-{
		return this.getLocalParticipantLocale();
	}-*/;
	
	public final native String getPreferredHangoutLocale()/*-{
		return this.getPreferredLocale();
	}-*/;
	
	public final native Participant getParticipant(String participantId)/*-{
		return this.getParticipantById(participantId);
	}-*/;
	
	public final native Participant getCurrentParticipant()/*-{
		return this.getLocalParticipant();
	}-*/;
	
	public final native String getCurrentParticipantId()/*-{
		return this.getLocalParticipantId();
	}-*/;
	
	public final native Array<Participant> getHangoutParticipants()/*-{
		return this.getParticipants();
	}-*/;

	public final native String getHangoutTopic()/*-{
		return this.getTopic();
	}-*/;

	public final native void hideApplication()/*-{
		this.hideApp();
	}-*/;
	
	public final native boolean isHangoutApiReady()/*-{
		return (this.isApiReady()?true:false);
	}-*/;
	
	public final native boolean isApplicationVisible()/*-{
		return (this.isAppVisible()?true:false);
	}-*/;

	public final native boolean isHangoutPublic()/*-{
		return (this.isPublic()?true:false);
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
	
	private static void loadHangoutApi(final ApiReadyHandler handler, final Hangout hangout)
    {
		if (hangout == null)
		{
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
			{
				@Override
				public boolean execute()
				{
					Hangout hagoutObject = getHagoutObject();
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
	
	private static native Hangout getHagoutObject()/*-{
		return $wnd.gapi.hangout;
	}-*/;

	private native void nativeAddApiReadyHandler(ApiReadyHandler handler)/*-{
		this.onApiReady.add(function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ApiReadyHandler::onApiReady(Lorg/cruxframework/crux/hangout/client/Hangout;)(this);
		});
	}-*/;

	private native void nativeRemoveAppVisibleHandler(JavaScriptObject func)/*-{
		this.onAppVisible.remove(func);
	}-*/;

	private native JavaScriptObject nativeAddAppVisibleHandler(AppVisibleHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.AppVisibleHandler::onAppVisibilityChange(Z)(eventObj.isAppVisible);
		};
		this.onAppVisible.add(f);
		return f;
	}-*/;

	private native void nativeRemoveParticipantsEnabledHandler(JavaScriptObject func)/*-{
		this.onParticipantsEnabled.remove(func);
	}-*/;

	private native JavaScriptObject nativeAddParticipantsEnabledHandler(ParticipantsEnabledHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ParticipantsEnabledHandler::onParticipantsEnabled(Lorg/cruxframework/crux/core/client/collection/Array;)(eventObj.enabledParticipants);
		};
		this.onParticipantsEnabled.add(f);
		return f;
	}-*/;
	
	private native void nativeRemoveParticipantsDisabledHandler(JavaScriptObject func)/*-{
		this.onParticipantsDisabled.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddParticipantsDisabledHandler(ParticipantsDisabledHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.Hangout.ParticipantsDisabledHandler::onParticipantsDisabled(Lorg/cruxframework/crux/core/client/collection/Array;)(eventObj.disabledParticipants);
		};
		this.onParticipantsDisabled.add(f);
		return f;
	}-*/;
	
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
}
