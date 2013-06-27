package org.cruxframework.crux.core.client.screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

public class BrowserNetworkTools  
{
	private static IsOnline isOnline = null;

	private static IsOnline getStaticInstance() {
		if(isOnline == null) {
			isOnline = GWT.create(IsOnline.class); 
		} 
		return isOnline;
	}
	
	public static boolean isOnline() {
		return getStaticInstance().isOnline();
	}

	public interface IsOnline {
		public boolean isOnline();
	}

	public static class IsOnlineDefault implements IsOnline {
		private static Boolean isOnline;

		private String url = "https://www.google.com.br/images/srpr/logo4w.png" + "?randomNumber=" + System.currentTimeMillis();
		private RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		private RequestCallback callback = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if(response.getStatusCode() == 302) {
					isOnline = true;
				} else {
					isOnline = false;
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				isOnline = false;
			}
		};

		public IsOnlineDefault() {
			Window.alert("Default");
			builder.setCallback(callback);
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
				public boolean execute() {
					try {
						builder.send();
					} catch (RequestException e) {
						e.printStackTrace();
					}
					return true;
				}
			}, 1000);
		}

		@Override
		public boolean isOnline() {
			if(isOnline == null) {
				Scheduler.get().scheduleEntry(new RepeatingCommand() {
					@Override
					public boolean execute() {
						if(isOnline != null) {
							return false;
						}
						return true;
					}
				});
			}
			
			return isOnline;
		}
	}

	public static class IsOnlineSafari implements IsOnline {
		@Override
		public native boolean isOnline() /*-{
			alert('IsOnlineSafari');
		    return $wnd.navigator.onLine();
		  }-*/;
	}
	
	public static class IsOnlineFF implements IsOnline {

		@Override
		public native boolean isOnline() /*-{
			alert('IsOnlineFF');
		    return true;
		  }-*/;
	}
}
