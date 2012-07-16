package org.cruxframework.crux.crossdevice.client.promobanner;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;

import com.google.gwt.event.dom.client.ClickHandler;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
@Templates({
//	@Template(name="promoBannerLarge", device=Device.largeDisplayArrows),
	@Template(name="promoBannerLargeTouch", device=Device.largeDisplayTouch),
	@Template(name="promoBannerLargeMouse", device=Device.largeDisplayMouse),
//	@Template(name="promoBannerSmall", device=Device.smallDisplayArrows),
	@Template(name="promoBannerSmallTouch", device=Device.smallDisplayTouch)
})
public interface PromoBanner extends DeviceAdaptive
{
	public void setBannersHeight(String height);
	public String getBannersHeight();

	public void setTransitionDuration(int transitionDuration);
	public int getTransitionDuration();

	public void setAutoTransitionInterval(int autoTransitionInterval);
	public int getAutoTransitionInterval();

	public void addSmallBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, ClickHandler onclick);
	public void addLargeBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, ClickHandler onclick);
	public void addDefaultBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, ClickHandler onclick);
}
