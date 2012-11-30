package org.cruxframework.crux.crossdevice.client.promobanner;

import org.cruxframework.crux.crossdevice.client.event.SelectHandler;

/**
 * PromoBanner implementation for small devices without touch
 * @author Thiago da Rosa de Bustamante
 *
 */
class BannerImplSmallNoTouch extends BannerImplNoTouch
{
	@Override
    public void setLargeBannersHeight(String height)
    {
		// Do nothing
    }

	@Override
    public String getLargeBannersHeight()
    {
		return null;
    }

	@Override
    public void setSmallBannersHeight(String height)
    {
		setBannersHeight(height);
    }

	@Override
    public String getSmallBannersHeight()
    {
        return getBannersHeight();
    }

	@Override
    public void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
    }

	@Override
    public void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
    {
		// Do nothing
    }
}