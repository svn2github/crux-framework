package org.cruxframework.crux.crossdevice.client.promobanner;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.controller.crossdevice.DeviceAdaptiveController;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.client.utils.StyleUtils;
import org.cruxframework.crux.crossdevice.client.button.Button;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;
import org.cruxframework.crux.widgets.client.animation.Animation.Callback;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A cross device widget to show a banner carrousel of images. This widget accept different images for small 
 * and for large devices and can be controlled by mouses, touches or arrows, according to the running device.
 *    
 * @author Thiago da Rosa e Bustamante
 */
public class PromoBanner extends Composite
{
	private BannerImpl impl;
	
	/**
	 * Widget implementation.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static abstract class BannerImpl extends Composite
	{
		protected VerticalPanel promoBanner;
		protected FocusPanel focusPanel;
		protected FlowPanel banners;
		protected HorizontalPanel bullets;
		protected int autoTransitionInterval = 5000;
		protected AutoTransiteTimer autoTransiteTimer;
		
		protected BannerImpl()
		{
			promoBanner = new VerticalPanel();
			promoBanner.setWidth("100%");
			
			focusPanel = new FocusPanel();
			
			banners = new FlowPanel();
			banners.setStyleName("bannersArea");
			banners.getElement().getStyle().setPosition(Position.RELATIVE);
			
			focusPanel.add(banners);
			
			bullets = new HorizontalPanel();
			bullets.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			bullets.setStyleName("bullets");
			
			promoBanner.add(focusPanel);
			promoBanner.add(bullets);
			promoBanner.setCellHorizontalAlignment(bullets, HasHorizontalAlignment.ALIGN_CENTER);
			
			autoTransiteTimer = new AutoTransiteTimer(this);
			autoTransiteTimer.reschedule();

			initWidget(promoBanner);
		}
		
		public void addDefaultBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
		{
			addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
		}

		public void setAutoTransitionInterval(int autoTransitionInterval)
		{
			this.autoTransitionInterval = autoTransitionInterval;
			autoTransiteTimer.reschedule();
		}

		public int getAutoTransitionInterval()
		{
			return autoTransitionInterval;
		}

		public void showBanner(int i, boolean slideToRight)
		{
			autoTransiteTimer.reschedule();

			if(i > getBannersCount() - 1)
			{
				i = 0;
			}

			if(i < 0)
			{
				i = getBannersCount() - 1;
			}

			showWidget(i, slideToRight);
			switchActiveBullet(i);
		}

		protected void setBannersHeight(String height)
        {
	        banners.setHeight(height);
        }


		protected String getBannersHeight()
        {
	        return banners.getElement().getStyle().getHeight();
        }
		
		protected void addBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
		{
			SimplePanel panel = new SimplePanel();

			if(styleName != null)
			{
				panel.setStyleName(styleName);
			}

			panel.getElement().getStyle().setBackgroundImage("url(" + imageURL + ")");
			panel.setHeight(getBannersHeight());
			panel.setWidth("100%");

			boolean hasTitle = !StringUtils.isEmpty(title);
			boolean hasText = !StringUtils.isEmpty(text);
			boolean hasButtonLabel = !StringUtils.isEmpty(buttonLabel) ;
			if (hasTitle || hasText || hasButtonLabel || onclick != null)
			{
				VerticalPanel messagePanel = new VerticalPanel();
				messagePanel.setStyleName("messagePanel");

				if (hasTitle)
				{
					Label titleLbl = new Label(title);
					titleLbl.setStyleName("title");
					messagePanel.add(titleLbl);
				}
				if (hasText)
				{
					Label textLbl = new Label(text);
					textLbl.setStyleName("text");
					messagePanel.add(textLbl);
				}
				
				if(onclick != null && hasButtonLabel)
				{
					Button btn = new Button();
					btn.setStyleName("button");
					btn.setText(buttonLabel);
					btn.addSelectHandler(onclick);
					messagePanel.add(btn);
				}

				panel.add(messagePanel);
			}
			doAddBanner(panel);

			Label bullet = new Label();
			final int targetIndex = getBannersCount() - 1;
			bullet.addClickHandler(
				new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						showBanner(targetIndex);
					}
				}
			);
			bullet.setStyleName("bullet");
			bullets.add(bullet);

			if(!hasVisibleBanner())
			{
				showBanner(0);
			}
		}

		protected void showBanner(int i)
		{
			autoTransiteTimer.reschedule();

			if(i > getBannersCount() - 1)
			{
				i = 0;
			}

			if(i < 0)
			{
				i = getBannersCount() - 1;
			}

			showWidget(i);
			switchActiveBullet(i);
		}

		protected void switchActiveBullet(int i)
		{
			for(int b = 0; b < bullets.getWidgetCount(); b++)
			{
				Widget bullet = bullets.getWidget(b);
				if(b == i)
				{
					bullet.addStyleDependentName("active");
				}
				else
				{
					bullet.removeStyleDependentName("active");
				}
			}
		}

		public abstract void setLargeBannersHeight(String height);
		public abstract String getLargeBannersHeight();
		public abstract void setSmallBannersHeight(String height);
		public abstract String getSmallBannersHeight();
		public abstract void setTransitionDuration(int transitionDuration);
		public abstract int getTransitionDuration();
		public abstract void showWidget(int i, boolean slideToRight);
		public abstract void showWidget(int i);
		public abstract int getNextBanner();
		public abstract int getBannersCount();
		public abstract void doAddBanner(SimplePanel panel);
		public abstract void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick);
		public abstract void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick);
		public abstract boolean hasVisibleBanner();

		protected static class AutoTransiteTimer extends Timer
		{
			private BannerImpl promoBanner;

			public AutoTransiteTimer(BannerImpl promoBanner)
			{
				this.promoBanner = promoBanner;
			}

			@Override
			public void run()
			{
				promoBanner.showBanner(promoBanner.getNextBanner(), true);
			}

			public void reschedule()
			{
				this.cancel();
				this.scheduleRepeating(promoBanner.autoTransitionInterval);
			}
		}
	}

	/**
	 * Implementation for non touch devices
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	static abstract class BannerImplNoTouch extends BannerImpl
	{
		protected HorizontalSwapPanel swapPanel;
		protected Label leftArrow;
		protected Label rightArrow;
		protected int visibleBanner = -1;
		protected FastList<SimplePanel> panels = new FastList<SimplePanel>();
		protected boolean controlsEnabled;
		
		
		protected BannerImplNoTouch()
		{
			super();
			swapPanel = new HorizontalSwapPanel();
			swapPanel.setUseFadeTransitions(true);
			banners.add(swapPanel);

			swapPanel.setHeight("100%");
			swapPanel.setWidth("100%");

			focusPanel.addKeyDownHandler(new KeyDownHandler()
			{
				@Override
				public void onKeyDown(KeyDownEvent event)
				{
					if (controlsEnabled)
					{
						if(event.isLeftArrow())
						{
							showBanner(visibleBanner - 1, false);
						}
						else if(event.isRightArrow())
						{
							showBanner(visibleBanner + 1, true);
						}
					}
				}
			});

			leftArrow = new Label();
			leftArrow.setStyleName("leftArrow");
			leftArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
			leftArrow.addClickHandler( new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					if (controlsEnabled)
					{
						showBanner(visibleBanner - 1, false);
					}
				}
			});

			rightArrow = new Label();
			rightArrow.setStyleName("rightArrow");
			rightArrow.getElement().getStyle().setPosition(Position.ABSOLUTE);
			rightArrow.addClickHandler( new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					if (controlsEnabled)
					{
						showBanner(visibleBanner + 1, true);
					}
				}
			});


			banners.add(leftArrow);
			banners.add(rightArrow);

			Scheduler.get().scheduleDeferred(
				new ScheduledCommand()
				{
					public void execute()
					{
						adjustPositions();
					}
				}
			);
		}
		
		@Override
		public void doAddBanner(SimplePanel panel)
		{
			panels.add(panel);
		}
		
		@Override
		public int getNextBanner()
		{
			return visibleBanner + 1;
		}
		
		@Override
		public boolean hasVisibleBanner()
		{
		    return visibleBanner >= 0;
		}
		
		@Override
		public void showWidget(int i)
		{
			assert(i >= 0 && i < panels.size()) : "Invalid index";
			visibleBanner = i;
			swapPanel.setCurrentWidget(panels.get(i));
		}

		@Override
		public void showWidget(int i, boolean slideToRight)
		{
			assert(i >= 0 && i < panels.size()) : "Invalid index";
			visibleBanner = i;
			setControlsEnabled(false);
			swapPanel.transitTo(panels.get(i), slideToRight?Direction.FORWARD:Direction.BACKWARDS, new Callback()
			{
				
				@Override
				public void onTransitionCompleted()
				{
					setControlsEnabled(true);
				}
			});
		}
		
		protected void setControlsEnabled(boolean controlsEnabled)
        {
	       this.controlsEnabled = controlsEnabled; 
	       if (this.controlsEnabled)
	       {
	    	   StyleUtils.removeStyleDependentName(leftArrow.getElement(), "disabled");
	    	   StyleUtils.removeStyleDependentName(rightArrow.getElement(), "disabled");
	       }
	       else
	       {
	    	   autoTransiteTimer.reschedule();
	    	   StyleUtils.addStyleDependentName(leftArrow.getElement(), "disabled");
	    	   StyleUtils.addStyleDependentName(rightArrow.getElement(), "disabled");
	       }
        }

		@Override
		public int getBannersCount()
		{
			return panels.size();			
		}
		
		@Override
		public void setTransitionDuration(int transitionDuration)
		{
			this.swapPanel.setTransitionDuration(transitionDuration);
		}

		@Override
		public int getTransitionDuration()
		{
			return swapPanel.getTransitionDuration();
		}
		
		@Override
		protected void setBannersHeight(String height)
		{
			super.setBannersHeight(height);
			swapPanel.setHeight(height);
		}
		
		protected void adjustPositions()
		{
			int containerHeight = banners.getElement().getClientHeight();

			rightArrow.getElement().getStyle().setRight(0, Unit.PX);
			rightArrow.getElement().getStyle().setTop((containerHeight - rightArrow.getElement().getClientHeight())/2, Unit.PX);

			leftArrow.getElement().getStyle().setLeft(0, Unit.PX);
			leftArrow.getElement().getStyle().setTop((containerHeight - leftArrow.getElement().getClientHeight())/2, Unit.PX);
		}
	}
	static class BannerImplSmallNoTouch extends BannerImplNoTouch
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
	
	static class BannerImplLargeNoTouch extends BannerImplNoTouch
	{
		@Override
        public void setLargeBannersHeight(String height)
        {
			setBannersHeight(height);
        }

		@Override
        public String getLargeBannersHeight()
        {
	        return getBannersHeight();
        }

		@Override
        public void setSmallBannersHeight(String height)
        {
	        // Do nothing
        }

		@Override
        public String getSmallBannersHeight()
        {
	        return null;
        }

		@Override
        public void addSmallBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
        {
	        // Do nothing
        }

		@Override
        public void addLargeBanner(String imageURL, String title, String text, String styleName, String buttonLabel, SelectHandler onclick)
        {
			addBanner(imageURL, title, text, styleName, buttonLabel, onclick);
        }
	}
	
	/**
	 * Constructor
	 */
	public PromoBanner()
	{
		impl = GWT.create(BannerImpl.class);
		initWidget(impl);
		setStyleName("xdev-PromoBanner");
	}

	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    DeviceAdaptiveController.applyWidgetDependentStyleNames(getElement());
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
	    super.setStyleName(style, add);
	    DeviceAdaptiveController.applyWidgetDependentStyleNames(getElement());
	}
	
	public void setLargeBannersHeight(String height)
	{
		impl.setLargeBannersHeight(height);
	}
	
	public String getLargeBannersHeight()
	{
		return impl.getLargeBannersHeight();
	}

	public void setSmallBannersHeight(String height)
	{
		impl.setSmallBannersHeight(height);
	}
	
	public String getSmallBannersHeight()
	{
		return impl.getSmallBannersHeight();
	}

	public void setTransitionDuration(int transitionDuration)
	{
		impl.setTransitionDuration(transitionDuration);
	}
	
	public int getTransitionDuration()
	{
		return impl.getTransitionDuration();
	}

	public void setAutoTransitionInterval(int autoTransitionInterval)
	{
		impl.setAutoTransitionInterval(autoTransitionInterval);
	}
	
	public int getAutoTransitionInterval()
	{
		return impl.getAutoTransitionInterval();
	}

	public void addSmallBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addSmallBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}
	
	public void addLargeBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addLargeBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}
	
	public void addDefaultBanner(String imageURL, String title, String text,  String styleName, String buttonLabel, SelectHandler onclick)
	{
		impl.addDefaultBanner(imageURL, title, text, styleName, buttonLabel, onclick);
	}
}
