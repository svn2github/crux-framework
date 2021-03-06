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
package org.cruxframework.crux.hangout.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DefaultVideoFeed extends VideoFeed
{
	protected DefaultVideoFeed(){}
	
	public final HandlerRegistration addDisplayedParticipantChangedHandler(DisplayedParticipantChangedHandler handler){
		final JavaScriptObject func = nativeAddDisplayedParticipantChangedHandler(handler);
		return new HandlerRegistration()
		{
			@Override
			public void removeHandler()
			{
				nativeRemoveDisplayedParticipantChangedHandler(func);
			}
		};
	}

	private native void nativeRemoveDisplayedParticipantChangedHandler(JavaScriptObject func)/*-{
		this.onDisplayedParticipantChanged.remove(func);
	}-*/;
	
	private native JavaScriptObject nativeAddDisplayedParticipantChangedHandler(DisplayedParticipantChangedHandler handler)/*-{
		var f = function(eventObj) {
		  handler.@org.cruxframework.crux.hangout.client.data.DefaultVideoFeed.DisplayedParticipantChangedHandler::onDisplayedParticipantChanged(Ljava/lang/String;)(eventObj.displayedParticipant);
		};
		this.onDisplayedParticipantChanged.add(f);
		return f;
	}-*/;

	public static interface DisplayedParticipantChangedHandler
	{
		void onDisplayedParticipantChanged(String displayedParticipant);
	}
	
	
}
