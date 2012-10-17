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

import org.cruxframework.crux.hangout.client.data.DefaultVideoFeed;
import org.cruxframework.crux.hangout.client.data.VideoCanvas;
import org.cruxframework.crux.hangout.client.data.VideoFeed;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HangoutLayout extends JavaScriptObject
{
	protected HangoutLayout(){}
	
	public final native VideoFeed createParticipantVideoFeed(String participantId)/*-{
		return this.createParticipantVideoFeed(participantId);
	}-*/;
	
	public final native DefaultVideoFeed getDefaultVideoFeed()/*-{
		return this.getDefaultVideoFeed();
	}-*/;
	
	public final native VideoCanvas getVideoCanvas()/*-{
		return this.getVideoCanvas();
	}-*/;

	public final native void dismissNotice()/*-{
		this.dismissNotice();
	}-*/;

	public final native void displayNotice(String message, boolean permanent)/*-{
		this.displayNotice(message, permanent);
	}-*/;

	public final native boolean hasNotice()/*-{
		return this.hasNotice();
	}-*/;

	public final native boolean isChatPaneVisible()/*-{
		return this.isChatPaneVisible();
	}-*/;
	
	public final native void setChatPaneVisible(boolean visible)/*-{
		this.setChatPaneVisible(visible);
	}-*/;

	public final HandlerRegistration addChatPaneVisibleHandler(ChatPaneVisibleHandler handler){
		final JavaScriptObject func = nativeAddChatPaneVisibleHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveChatPaneVisibleHandler(func);
			}
		};
	}
	
	public final HandlerRegistration addHasNoticeHandler(HasNoticeHandler handler){
		final JavaScriptObject func = nativeAddHasNoticeHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveHasNoticeHandler(func);
			}
		};
	}
	
	private native void nativeRemoveChatPaneVisibleHandler(JavaScriptObject func)/*-{
		this.onChatPaneVisible.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddChatPaneVisibleHandler(ChatPaneVisibleHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.HangoutLayout.ChatPaneVisibleHandler::onChatPaneVisible(Z)(eventObj.isChatPaneVisible);
		};
		this.onChatPaneVisible.add(f);
		return f;
	}-*/;

	private native void nativeRemoveHasNoticeHandler(JavaScriptObject func)/*-{
		this.onHasNotice.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddHasNoticeHandler(HasNoticeHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.HangoutLayout.HasNoticeHandler::onHasNotice(Z)(eventObj.hasNotice);
		};
		this.onHasNotice.add(f);
		return f;
	}-*/;

	public static interface ChatPaneVisibleHandler
	{
		void onChatPaneVisible(boolean isChatPaneVisible);
	}
	
	public static interface HasNoticeHandler
	{
		void onHasNotice(boolean hasNotice);
	}
	
}
