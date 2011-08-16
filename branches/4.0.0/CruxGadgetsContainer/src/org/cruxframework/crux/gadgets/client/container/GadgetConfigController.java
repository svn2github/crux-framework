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
package org.cruxframework.crux.gadgets.client.container;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.gadgets.client.GadgetContainerMsg;
import org.cruxframework.crux.gadgets.client.Gadgets;
import org.cruxframework.crux.gadgets.client.Gadgets.MetadataCallback;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("gadgetConfigController")
public class GadgetConfigController
{
	@Create
	protected GadgetContainerMsg messages;

	@Expose
	public void onLoadProduction()
	{
		load(false, "", "");
	}

	@Expose
	public void onLoadDebug()
	{
		load(true, "", ""); //TODO: resolver autenticacao
	}

	
	/**
	 * Load container configuration
	 * @param debug
	 */
	public void load(final boolean debug, final String userId, final String groupId)
	{
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
		{
			private int numTries = 0;
			@Override
			public boolean execute()
			{
				boolean ret = !checkLayoutManager();
				if (ret && numTries > 10)
				{
					Crux.getErrorHandler().handleError(messages.layoutManagerNotFound());
					return false;
				}
				if (!ret)
				{
					configure(debug, userId, groupId);
				}
				return ret;
			}
		}, 100);
	}
	
	//TODO: receber esses dados
	private native Array<String> getGadgets()/*-{
	    return $wnd.currentGadgets;
    }-*/;

	
	/**
	 * 
	 * @param debug
	 * @param groupId 
	 * @param userId 
	 */
	protected void configure(boolean debug, String userId, String groupId)
    {
		GadgetContainer.setLoaded();
		GadgetContainer.get().setDebug(debug);
		String url = Window.Location.getParameter("url");
//		configureContainerURL();
		configureLocale();
		configureCache();
		configureCurrentView(url);
		configureGadgetCanvasHeight();
		configureContainerParentUrl();
		
		Array<String> gadgets;
		if (StringUtils.isEmpty(url))
		{
			gadgets = getGadgets();
		}
		else
		{
			gadgets = CollectionFactory.createArray();
			gadgets.add(url);
		}
		Gadgets.loadGadgetsMetadata(gadgets, new MetadataCallback()
		{
			@Override
			public void onMetadataLoaded(Array<GadgetMetadata> metadata)
			{
				onGadgetsMetadataLoaded(metadata);
			}
		});
    }

	/**
	 * 
	 */
	protected void configureGadgetCanvasHeight()
    {
		GadgetContainer.get().setGadgetCanvasHeight(getGadgetCanvasHeight());
		GadgetContainer.get().setGadgetCanvasWidth("100%");
    }

	/*
	 * 
	 *
	protected void configureContainerURL()
    {
	    String containerURL = getContainerURL();
	    GadgetContainer.get().setContainerUrl(containerURL);
    }*/
	
	/**
	 * 
	 */
	protected void configureContainerParentUrl()
    {   //TODO: checar isso aki.... ta setando duas propriedades com o mesmo valor
		GadgetContainer.get().setParentUrl(getContainerURL());
    }

	protected void onGadgetsMetadataLoaded(Array<GadgetMetadata> metadata) //TODO: ajustar isso aki
	{
		//TODO: carregar configuracoes de urlbase, parentcontainerurl, currentView, userId, groupId
		GadgetContainer.get().setMetadata(getGadgetsMetadata(metadata));
		notifyListeners();
	}

	protected void configureCurrentView(String url)
    {
		ContainerView currentView = (StringUtils.isEmpty(url)?ContainerView.profile:ContainerView.canvas);
		GadgetContainer.get().setCurrentView(currentView);
    }

	protected void configureCache()
    {
		String nocache = Window.Location.getParameter("nocache");
		boolean cacheEnabled = (nocache==null || !nocache.equals("1"));
		GadgetContainer.get().setCacheEnabled(cacheEnabled);
    }	
	
	protected void configureLocale()
    {
	    String country = Window.Location.getParameter("country");
		String language = Window.Location.getParameter("lang");
		if (StringUtils.isEmpty(language) && StringUtils.isEmpty(country))
		{
			String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
			String[] localeParts = localeName.split("_");
			if (localeParts != null && localeParts.length > 0)
			{
				language = localeParts[0];
				if (localeParts.length > 1)
				{
					country = localeParts[1];
				}
			}
		}
		if (StringUtils.isEmpty(language))
		{
			language = "default";
		}
		if (StringUtils.isEmpty(country))
		{
			country = "default";
		}
		GadgetContainer.get().setLanguage(language);
		GadgetContainer.get().setCountry(country);
    }

	/**
	 * Retrieve the height to be used for gadgets when rendering on canvas view.
	 * @return
	 */
	protected native String getGadgetCanvasHeight()/*-{
	    return $wnd._gadgetCanvasHeight || null;
    }-*/;

	/**
	 * 
	 * @return
	 */
	protected native String getContainerURL()/*-{
		return ($doc.location + '')
	}-*/;
		
	/**
	 * 
	 * @param metadata
	 * @return
	 */
	protected Array<Array<GadgetMetadata>> getGadgetsMetadata(Array<GadgetMetadata> metadata)
    {
	    Array<Array<GadgetMetadata>> array = CollectionFactory.createArray();
		array.add(metadata);
		Array<GadgetMetadata> column1 = CollectionFactory.createArray();
		array.add(column1);
		Array<GadgetMetadata> column2 = CollectionFactory.createArray();
		array.add(column2);
	    return array;
    }
	
	private native void notifyListeners()/*-{
		$wnd.__configureLayoutManager();
	}-*/;

	private native boolean checkLayoutManager()/*-{
	    if (!$wnd.__configureLayoutManager){
	    	false;
	    }
	    return true;
    }-*/;
}
