/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.widgets.client.util.draganddrop;

import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAction;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.DragAndDropFeature;
import org.cruxframework.crux.widgets.client.util.draganddrop.GenericDragEventHandler.Draggable;

import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Encloses the logic and contracts needed resize a widget when user drags a part of it
 * @author Gesse Dafe
 */
public class ResizeCapability
{
	/**
	 * Makes a widget able to be resized by dragging a part of it, usually a right bottom corner. 
	 * @param movable
	 */
	public static void addResizeCapability(Resizable<?> resizable, int minWidth, int minHeight)
	{
		GenericDragEventHandler handler = new GenericDragEventHandler(new DragResizeAction(resizable, minWidth, minHeight));
		handler.applyTo(resizable);
	}
	
	/**
	 * The contract to implement a widget which can be resized  
	 * @author Gesse Dafe
	 */
	public static interface Resizable<K extends IsWidget & HasAllMouseHandlers> extends Draggable<K>
	{
		public void setDimensions(int w, int h);
		public int getAbsoluteWidth();
		public int getAbsoluteHeight();
	}
	
	/**
	 * The logic needed to resize a widget when user drags a part of it 
	 * @author Gesse Dafe
	 */
	public static class DragResizeAction extends DragAction<Resizable<?>> 
	{
		private int minWidth;
		private int minHeight;
		private int originalWidth;
		private int originalHeight;
		
		/**
		 * @param movable
		 * @param minWidth
		 * @param minHeight
		 */
		public DragResizeAction(Resizable<?> resizable, int minWidth, int minHeight)
		{
			super(resizable);
			this.minWidth = minWidth;
			this.minHeight = minHeight;
		}
		
		@Override
		public void onStartDrag()
		{
			originalWidth = getDraggable().getAbsoluteWidth();
			originalHeight = getDraggable().getAbsoluteHeight();
		}

		@Override
		public void onDrag(int x, int y, int dragStartX, int dragStartY)
		{
			int deltaX = x - dragStartX;
			int deltaY = y - dragStartY;
			int newWidth = originalWidth + deltaX;
			int newHeight = originalHeight + deltaY;
			getDraggable().setDimensions(newWidth >= minWidth ? newWidth : minWidth, newHeight >= minHeight ? newHeight : minHeight);
		}

		@Override
		public DragAndDropFeature getFeature()
		{
			return DragAndDropFeature.RESIZE;
		}
	}
}
