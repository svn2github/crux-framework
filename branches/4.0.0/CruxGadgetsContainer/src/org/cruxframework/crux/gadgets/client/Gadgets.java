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
package org.cruxframework.crux.gadgets.client;

import java.util.HashMap;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.gadgets.client.container.GadgetContainer;
import org.cruxframework.crux.gadgets.client.container.GadgetMetadata;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class Gadgets
{
	private static HashMap<String, GadgetMetadata> loadedGadgetsMetadata = new HashMap<String, GadgetMetadata>();

	/**
	 * 
	 * @param gadgets
	 */
	public static void loadGadgetsMetadata(Array<String> gadgets, final MetadataCallback callback)
    {
		final Array<GadgetMetadata> alreadyLoadedGadgets = CollectionFactory.createArray();
		Array<String> toLoadGadgets = CollectionFactory.createArray();
		
		for (int i=0; i < gadgets.size(); i++)
        {
			String gadgetUrl = gadgets.get(i);
	        if (loadedGadgetsMetadata.containsKey(gadgetUrl))
	        {
	        	alreadyLoadedGadgets.add(loadedGadgetsMetadata.get(gadgetUrl));
	        }
	        else
	        {
	        	toLoadGadgets.add(gadgetUrl);
	        }
        }
		
		if (toLoadGadgets.size() > 0)
		{
			loadGadgetsMetadata(GadgetContainer.get().getCountry(), 
					GadgetContainer.get().getLanguage(), 
					GadgetContainer.get().getCurrentView().toString(), 
					"john.doe:john.doe:appid:cont:url:0:default", //TODO passar a secureToken aki
					toLoadGadgets, new MetadataCallback()
					{
						@Override
						public void onMetadataLoaded(Array<GadgetMetadata> metadata)
						{
							int length = metadata.size();
							for (int i=0; i< length; i++)
							{
								GadgetMetadata gadgetMetadata = metadata.get(i);
								alreadyLoadedGadgets.add(gadgetMetadata);
								loadedGadgetsMetadata.put(gadgetMetadata.getUrl(), gadgetMetadata);
							}
							callback.onMetadataLoaded(alreadyLoadedGadgets);
						}
					});
		}
		else
		{
			callback.onMetadataLoaded(alreadyLoadedGadgets);
		}
    }
	
	/**
	 * 
	 * @param userCountry
	 * @param userLanguage
	 * @param containerView
	 * @param secureToken
	 * @param gadgetUrls
	 * @param controller
	 */
	public static native void loadGadgetsMetadata(String userCountry, 
			       							 String userLanguage, 
			       							 String containerView, 
			       							 String secureToken, 
			       							 Array<String> gadgetUrls,
			       							 MetadataCallback callback)/*-{
		var request = {
			context: {
				country: userCountry,
				language: userLanguage,
				view: containerView,
				container: "default"
			},
			gadgets: []
		};

		for (var i=0; i<gadgetUrls.length; i++) {
			request.gadgets[request.gadgets.length] = {url: gadgetUrls[i], moduleId: 1};
		}

		var makeRequestParams = {
			"CONTENT_TYPE" : "JSON",
			"METHOD" : "POST",
			"POST_DATA" : $wnd.gadgets.json.stringify(request)
		};

		var url = "/gadgets/metadata?st=" + secureToken;
		$wnd.gadgets.io.makeNonProxiedRequest(url,
			function (obj) {
				var numGadgets = obj.data.gadgets.length;
				var result = [];
				for (var i=0; i<numGadgets; i++) {
					var gadgetMetadata = obj.data.gadgets[i];
					result[result.length] = gadgetMetadata;
				}
				callback.@org.cruxframework.crux.gadgets.client.Gadgets.MetadataCallback::onMetadataLoaded(Lorg/cruxframework/crux/core/client/collection/Array;)(result);
			},
			makeRequestParams,
			"application/javascript"
		); 	
	
	}-*/;

	/**
	 * 
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface MetadataCallback
	{
		void onMetadataLoaded(Array<GadgetMetadata> metadata);
	}	
}
