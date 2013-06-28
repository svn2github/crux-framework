package org.cruxframework.crux.core.client.screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * Inspect network conditions and give requested feedback to user.
 * @author samuel.cardoso
 */
public class BrowserNetworkTools  
{
	private static IsOnline isOnline = null;

	/**
	 * TODO: javadoc!!!
	 * @return 
	 */
	public static boolean isOnline() {
		return getStaticInstance().isOnline();
	}
	
	private static IsOnline getStaticInstance() {
		if(isOnline == null) {
			isOnline = GWT.create(IsOnline.class); 
		} 
		return isOnline;
	}
	
	public interface IsOnline {
		public boolean isOnline();
	}

	/**
	 * TODO: javadoc!!!
	 * @return 
	 */
	public static class IsOnlineDefault implements IsOnline {
		private static final int REPEATING_INTERVAL = 5000;
		private static final long RANDOM_TOKEN = System.currentTimeMillis();
		private static final String DUMMY_ID = "dummyId_" + RANDOM_TOKEN;
		private static final String URL = "http://127.0.0.1:8888/index.html" + "?randomToken=" + RANDOM_TOKEN;
		private static final String URL_IMG = "https://www.google.com.br/images/srpr/logo4w.png" + "?randomToken=" + RANDOM_TOKEN;
		private static final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL);
		
		private static Boolean isOnline;

		public IsOnlineDefault() {
			builder.setCallback(new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == 200) {
						isOnline = true;
					} else {
						isOnline = false;
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					isOnline = false;
				}
			});
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
				public boolean execute() {
					try {
						builder.send();
					} catch (RequestException e) {
						e.printStackTrace();
					}
					return true;
				}
			}, REPEATING_INTERVAL);
		}

		@Override
		public boolean isOnline() {
			if(isOnline == null) {
				//Window.alert("by image");
				return isOnlineUsingSyncRequestJSNative(URL_IMG);
			}
			//Window.alert("by requestCallback");
			Window.alert(isOnline.toString());
			return isOnline;
		}
		
		@Deprecated
		private native boolean isOnlineUsingSyncRequestNative(String URL) /*-{
			var xhReq = new XMLHttpRequest();
		 	xhReq.open("GET", URL, false);
		 	xhReq.send(null);
		 	if(xhReq.status == 200) { return true; }
		 	return false;
	  	}-*/;
		
		//not working so far...
		private boolean isOnlineUsingSyncRequest() {
			Image img = new Image(URL);
			img.getElement().getStyle().setPosition(Position.ABSOLUTE);
			img.getElement().getStyle().setLeft(-1000, Unit.PX);

			final Element div = Document.get().createDivElement();
			div.setId(DUMMY_ID);
			//not working:
			//RootPanel.getBodyElement().insertFirst(div); || DOM.appendChild(RootPanel.getBodyElement(), (com.google.gwt.user.client.Element) div);
			Document.get().getBody().appendChild(div);
			
			img.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					Document.get().getBody().removeChild(div);
					isOnline = true;
				}
			});
			
			img.addErrorHandler(new ErrorHandler() {
				@Override
				public void onError(ErrorEvent event) {
					Document.get().getBody().removeChild(div);
					isOnline = false;
				}
			});
			
			if(img.getHeight() > 0) {
				isOnline = true;
			} else {
				isOnline = false;
			}
			return isOnline;
		}
		
		@Deprecated
		private native boolean isOnlineUsingSyncRequestJSNative(String URL) /*-{
			try {
				var online = false;
				var div = document.createElement('div');
				var image = document.createElement('img');
				image.src = URL;
				div.appendChild(image);
				image.style.visibility = "hidden";
				document.body.appendChild(div);
				//onload property is not used here because doesn't have full JS support in all browsers
				if(image.height>0){
				    online = true;
				}
				div.removeChild(image);
				delete image;
				return online;
			} catch(err)
			{
			  alert(err);
			}
		  }-*/;
	}

	/**
	 * TODO: javadoc!!!
	 * @return 
	 */
	public static class IsOnlineSafari implements IsOnline {
		@Override
		public native boolean isOnline() /*-{
			alert('IsOnlineSafari');
		    return $wnd.navigator.onLine();
		  }-*/;
	}
	
	/**
	 * TODO: javadoc!!!
	 * @return 
	 */
	public static class IsOnlineFF implements IsOnline {

		@Override
		public native boolean isOnline() /*-{
			alert('IsOnlineFF');
		    return true;
		  }-*/;
	}
}
