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
package org.cruxframework.crux.gadgets.client.controller;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.gadgets.client.TabLayoutMsg;
import org.cruxframework.crux.gadgets.client.dto.GadgetMetadata;
import org.cruxframework.crux.gadgets.client.dto.GadgetsConfiguration;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("gadgetConfigController")
public class GadgetConfigController
{
	@Create
	protected TabLayoutMsg messages;
	
	@Expose
	public void onLoad()
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
					configure();
				}
				return ret;
			}
		}, 100);
	}

	//TODO: receber esses dados
	private native JsArrayString getGadgets()/*-{
	    return ['http://rssgadget.livewaresystems.com/gen/futebolbr/rssgadget/flamengo.xml', 
	            'http://rssgadget.livewaresystems.com/gen/futebolbr/rssgadget/vasco.xml',
	            'http://rssgadget.livewaresystems.com/gen/futebolbr/rssgadget/corinthians.xml'];
    }-*/;

	protected native void getGadgetsMetadata(String userCountry, 
			       							 String userLanguage, 
			       							 String containerView, 
			       							 String secureToken, 
			       							 JsArrayString gadgetUrls,
			       							GadgetConfigController controller)/*-{
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
				var requiresPubSub2 = false;
				var numGadgets = obj.data.gadgets.length;
				var result = [];
				for (var i=0; i<numGadgets; i++) {
					var gadgetMetadata = obj.data.gadgets[i];
					result[result.length] = gadgetMetadata;
				}
				controller.@org.cruxframework.crux.gadgets.client.controller.GadgetConfigController::setConfigurationMetadata(Lcom/google/gwt/core/client/JsArray;)(result);
			},
			makeRequestParams,
			"application/javascript"
		); 	
	
	}-*/;
	
	protected void configure()
    {
	    getGadgetsMetadata("default", "default", "default", "john.doe:john.doe:appid:cont:url:0:default", getGadgets(), this);
	    
    }

	protected native JsArray<JsArray<GadgetMetadata>> createMetadataGrid()/*-{
		return [];
	}-*/;
	
	protected native JsArray<GadgetMetadata> createMetadataArray()/*-{
		return [];
	}-*/;

	protected void setConfigurationMetadata(JsArray<GadgetMetadata> metadata) //TODO: ajustar isso aki
	{
		JsArray<JsArray<GadgetMetadata>> array = createMetadataGrid();
		array.push(metadata);
		array.push(createMetadataArray());
		array.push(createMetadataArray());
		GadgetsConfiguration configuration = new GadgetsConfiguration();
		configuration.setMetadata(array);
		nativeConfigure(configuration);
	}
	
	private native void nativeConfigure(GadgetsConfiguration config)/*-{
		$wnd.__configureLayoutManager(config);
	}-*/;

	protected native boolean checkLayoutManager()/*-{
	    if (!$wnd.__configureLayoutManager){
	    	false;
	    }
	    return true;
    }-*/;
}
