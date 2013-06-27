package org.cruxframework.crux.core.client.screen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class BrowserNetworkTools  
{
	protected static IsOnline isOnline = GWT.create(IsOnline.class);

	public static boolean isOnline() {
		return isOnline.isOnline();
	}

	public interface IsOnline {
		public boolean isOnline();
	}

	public class IsOnlineDefault implements IsOnline {
		private boolean isOnline = false;

		private String imgSrc = "https://www.google.com.br/images/srpr/logo4w.png" + "?randomNumber=" + System.currentTimeMillis();
		private RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, imgSrc);
		private RequestCallback callback = new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {
				if(response.getStatusCode() == 404) {

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
			builder.setCallback(callback);
		}

		@Override
		public boolean isOnline() {
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

			return isOnline;
		}
	}

	public class IsOnlineSafari implements IsOnline {

		@Override
		public native boolean isOnline() /*-{
			alert('IsOnlineSafari');
		    return new $wnd.navigator.onLine();
		  }-*/;
	}
}
