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

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class VideoCanvas extends JavaScriptObject
{
	protected VideoCanvas(){}
	
	public final native double getAspectRatio()/*-{
		return this.getAspectRatio();
	}-*/;

	public final native double getHeight()/*-{
		return this.getHeight();
	}-*/;

	public final native double getWidth()/*-{
		return this.getWidth();
	}-*/;

	public final native double getLeft()/*-{
		return this.getPosition().left;
	}-*/;

	public final native double getTop()/*-{
		return this.getPosition().top;
	}-*/;
	
	public final native VideoFeed getVideoFeed()/*-{
		return this.getVideoFeed();
	}-*/;
	
	public final native boolean isVisible()/*-{
		return this.isVisible();
	}-*/;

	/**
	 * 
	 * @param height
	 * @return The new width, based on current aspect ratio
	 */
	public final native double setHeight(int height)/*-{
		return this.setHeight(height).width;
	}-*/;

	public final native void setPosition(int left, int top)/*-{
		this.setPosition(left, top);
	}-*/;
	
	public final native void setVideoFeed(VideoFeed videoFeed)/*-{
		this.setVideoFeed(videoFeed);
	}-*/;
	
	public final native void setVisible(boolean visible)/*-{
		this.setVisible(visible);
	}-*/;

	/**
	 * 
	 * @param width
	 * @return The new height, based on current aspect ratio
	 */
	public final native double setWidth(int width)/*-{
		return this.setWidth(width).height;
	}-*/;
}
