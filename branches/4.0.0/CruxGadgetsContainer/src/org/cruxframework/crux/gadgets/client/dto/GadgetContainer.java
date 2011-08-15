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
package org.cruxframework.crux.gadgets.client.dto;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetContainer extends JavaScriptObject
{
	protected GadgetContainer() {}
	
	public static native GadgetContainer get()/*-{
		return $wnd.shindig.container;
	}-*/;

	public final native String getCountry()/*-{
		return this.country_;
	}-*/;

	public final native void setCountry(String country)/*-{
		this.country_ = country;
	}-*/;

	public final native String getGadgetCanvasHeight()/*-{
		return this.canvasHeight_;
	}-*/;
	
	public final native void setGadgetCanvasHeight(String canvasHeight)/*-{
		this.canvasHeight_ = canvasHeight;
	}-*/;

	public final native String getGadgetCanvasWidth()/*-{
		return this.canvasWidth_;
	}-*/;
	
	public final native void setGadgetCanvasWidth(String canvasWidth)/*-{
		this.canvasWidth_ = canvasWidth;
	}-*/;

   public final native String getLanguage()/*-{
		return this.language_;
	}-*/;
	
	public final native void setLanguage(String language)/*-{
		this.language_ = language;
	}-*/;
	
	public final native boolean isCacheEnabled()/*-{
		return (this.nocache_?false:true);
	}-*/;
	
	public final native void setCacheEnabled(boolean cache)/*-{
		this.nocache_ = (cache?0:1);
	}-*/;
	
	public final native boolean isDebug()/*-{
		return (this.debug_?false:true);
	}-*/;
	
	public final native void setDebug(boolean debug)/*-{
		this.debug_ = debug;
	}-*/;

	private native String getCurrentViewNative()/*-{
		return this.view_;
	}-*/;
	
	private native void setCurrentViewNative(String view)/*-{
		this.view_ = view;
	}-*/;
	
	public final ContainerView getCurrentView()
	{
		String currentView = getCurrentViewNative();
		return (currentView != null && currentView.equals("canvas"))?ContainerView.canvas:ContainerView.profile;
	}
	
	public final void setCurrentView(ContainerView currentView)
	{
		setCurrentViewNative(currentView.toString());
	}
	
	public final native String getParentUrl()/*-{
		return this.parentUrl_;
	}-*/;
	
	public final native void setParentUrl(String parentUrl)/*-{
		this.parentUrl_ = parentUrl;
	}-*/;
	
	public final native Gadget getGadget(int gadgetId)/*-{
		return this.getGadget(gadgetId);
	}-*/;
	
	public final native Gadget createGadget(GadgetMetadata gadgetConfig)/*-{
		function isRequiresSubPub2(gadget)
		{
			var requiresPubSub2 = false;
			var arr = gadget.features;
			if (arr) {
				for(var i = 0; i < arr.length; i++) {
					if (arr[i] === "pubsub-2") {
						requiresPubSub2 = true;
						break;
					}
				}
			}
			return requiresPubSub2;
		}
		
		return this.createGadget(
						{debug: (this.debug_?1:0), 'specUrl': gadgetConfig.url, 
						'title': gadgetConfig.title, 'userPrefs': gadgetConfig.userPrefs, 
						'height': (this.view_==="canvas"?this.canvasHeight_:gadgetConfig.height), 
						'width': (this.view_==="canvas"?this.canvasWidth_:gadgetConfig.width), 
						'requiresPubSub2': isRequiresSubPub2(gadgetConfig)});
						//'secureToken': escape(generateSecureToken())});
	}-*/;
	//TODO gerar a secureToken
	
	public final Gadget createGadget(Gadget gadget, ContainerView view)
	{
		return createGadget(gadget, view.toString());
	}
	
	private native Gadget createGadget(Gadget gadget, String view)/*-{
		return this.createGadget(
						{debug: gadget.debug, 'specUrl': gadget.specUrl, 
						'title': gadget.title, 'userPrefs': gadget.userPrefs,
						'view': view, 
						'height': (view==="canvas"?this.canvasHeight_:gadget.height), 
						'width': (view==="canvas"!=null?this.canvasWidth_:gadget.width), 
						'requiresPubSub2': gadget.requiresPubSub2});
						//'secureToken': escape(generateSecureToken())});
	}-*/;

	public final native void addGadget(Gadget gadget)/*-{
		this.addGadget(gadget);
	}-*/;

	public final native void removeGadget(Gadget gadget)/*-{
		delete this.gadgets_[this.getGadgetKey_(gadget.id)];
	}-*/;
		
	public final native void renderGadget(Gadget gadget)/*-{
		this.renderGadget(gadget);
	}-*/;
}
