package org.cruxframework.crux.crossdevice.client.promobanner;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.utils.StyleUtils;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;


/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
@Controller("promoBannerSmallTouchController")
public class PromoBannerSmallTouchController extends BasePromoBannerController
{

	@Override
	protected void init()
	{
		super.init();

		focusPanel.addTouchStartHandler(new TouchStartHandler()
		{
			@Override
			public void onTouchStart(TouchStartEvent event)
			{

			}
		});

		focusPanel.addTouchEndHandler(new TouchEndHandler()
		{
			@Override
			public void onTouchEnd(TouchEndEvent event)
			{

			}
		});
	}

	@Override
	protected void applyWidgetDependentStyleNames()
	{
		StyleUtils.addStyleDependentName(getElement(), DeviceAdaptive.Size.small.toString());
	}

	public void addSmallBanner(String backgroundImageURL, String title, String text, String buttonLabel, ClickHandler onclick)
	{
		addSmallBanner(backgroundImageURL, title, text, null, buttonLabel, onclick);
	}

	@Override
	public void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, ClickHandler onclick)
	{
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}

	@Override
	public void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, ClickHandler onclick)
	{

	}
}
