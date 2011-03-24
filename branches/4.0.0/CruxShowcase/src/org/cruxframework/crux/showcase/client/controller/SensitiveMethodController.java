package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.showcase.client.remote.SensitiveServerServiceAsync;
import org.cruxframework.crux.widgets.client.dialog.MessageBox;


@Controller("sensitiveMethodController")
public class SensitiveMethodController {
	
	@Create
	protected SensitiveServerServiceAsync service;
	
	@Expose
	public void onClick()
	{
		service.sensitiveMethod(new AsyncCallbackAdapter<String>(this){
			@Override
			public void onComplete(String result){
				MessageBox.show("Sensitive Method", result, null);
			}
		});
	}

	@Expose
	public void onClickNoBlock()
	{
		service.sensitiveMethodNoBlock(new AsyncCallbackAdapter<String>(this){
			@Override
			public void onComplete(String result){
				MessageBox.show("Sensitive Method", result, null);
			}
		});
	}
}