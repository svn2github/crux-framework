package org.cruxframework.crux.gadgets.client.layout;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.gadgets.client.container.ContainerView;
import org.cruxframework.crux.gadgets.client.container.Gadget;
import org.cruxframework.crux.gadgets.client.container.GadgetContainer;
import org.cruxframework.crux.widgets.client.rollingtabs.RollingTabPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("tabLayoutController")
public class TabLayoutManagerController extends GridLayoutManagerController
{
	private FastMap<Integer> openedCanvasGadgetIds = new FastMap<Integer>();
	private FastMap<Widget> openedCanvasGadgetTabs = new FastMap<Widget>();
	
	@Create
	protected TabLayoutScreen tabScreen;
	
	@Override
	public void changeGadgetView(int gadgetId, boolean profileView)
	{
		if (profileView)
		{
			String gadgetKey = Integer.toString(gadgetId);
			if (!openedCanvasGadgetIds.containsKey(gadgetKey))
			{
				openCanvasView(gadgetId);
			}
			else
			{
				closeCanvasView(gadgetId);
			}
		}
	}

	/**
	 * 
	 * @param gadgetId
	 */
	private void openCanvasView(int gadgetId)
    {
		String gadgetKey = Integer.toString(gadgetId);
	    RollingTabPanel tabManager = tabScreen.getLayoutTabManager();
	    GadgetContainer container = GadgetContainer.get();
	    Gadget profileGadget = container.getGadget(gadgetId);

	    profileGadget.deactivate();
	    Gadget canvasGadget = container.createGadget(profileGadget, ContainerView.canvas);
	    container.addGadget(canvasGadget);
	    SimplePanel newGadgetChrome = new SimplePanel();
	    newGadgetChrome.setHeight("100%");
	    newGadgetChrome.setWidth("100%");
	    newGadgetChrome.getElement().setId(getGadgetChromeId(canvasGadget.getId()));
	    tabManager.add(newGadgetChrome, getTabWidget(gadgetId, canvasGadget.getTitle()));
	    container.renderGadget(canvasGadget);
	    int widgetIndex = tabManager.getWidgetIndex(newGadgetChrome);
	    tabManager.selectTab(widgetIndex);

	    openedCanvasGadgetIds.put(gadgetKey, canvasGadget.getId());
	    openedCanvasGadgetTabs.put(gadgetKey, newGadgetChrome);
    }

	/**
	 * 
	 * @param gadgetId
	 */
	private void closeCanvasView(int gadgetId)
    {
		String gadgetKey = Integer.toString(gadgetId);
	    RollingTabPanel tabManager = tabScreen.getLayoutTabManager();
	    GadgetContainer container = GadgetContainer.get();
	    Gadget profileGadget = container.getGadget(gadgetId);

	    int canvasGadgetId = openedCanvasGadgetIds.remove(gadgetKey);
	    Widget canvasGadgetChrome = openedCanvasGadgetTabs.remove(gadgetKey);
	    
	    container.getGadget(canvasGadgetId).remove();
	    int widgetIndex = tabManager.getWidgetIndex(canvasGadgetChrome);
		if (widgetIndex == tabManager.getSelectedTab())
	    {
	    	tabManager.selectTab(0);
	    }

	    tabManager.remove(widgetIndex);
	    profileGadget.activate();
    }
	
	/**
	 * 
	 * @param profileGadgetId
	 * @param gadgetTitle
	 * @return
	 */
	public HorizontalPanel getTabWidget(final int profileGadgetId, String gadgetTitle)
	{
		HorizontalPanel flap = new HorizontalPanel();
		flap.setSpacing(0);

		Label title = new Label(gadgetTitle);
		title.setStyleName("tabLabel");
		flap.add(title);

		FocusWidget closeButton = new FocusWidget(new Label(" ").getElement()) {};
		closeButton.setStyleName("tabCloseButton");
		closeButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				event.stopPropagation();
				closeCanvasView(profileGadgetId);
			}
		});

		closeButton.addKeyDownHandler(new KeyDownHandler()
		{
			public void onKeyDown(KeyDownEvent event)
			{
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
				{
					event.stopPropagation();
					closeCanvasView(profileGadgetId);
				}
			}
		});
		closeButton.setVisible(true);
		flap.add(closeButton);
		return flap;
	}

	/**
	 * 
	 */
	@Override
	protected GadgetShindigClassHandler createShindigClassHandler()
	{
	    return new TabGadgetShindigClassHandler(this);
	}
	
	/**
	 * Screen wrapper for template widgets.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface TabLayoutScreen extends ScreenWrapper
	{
		RollingTabPanel getLayoutTabManager();
	}	
}