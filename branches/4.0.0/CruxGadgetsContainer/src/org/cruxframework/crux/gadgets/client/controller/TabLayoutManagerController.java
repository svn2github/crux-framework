package org.cruxframework.crux.gadgets.client.controller;

import org.cruxframework.crux.core.client.collection.FastMap;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.gadgets.client.dto.ContainerView;
import org.cruxframework.crux.gadgets.client.dto.Gadget;
import org.cruxframework.crux.gadgets.client.dto.GadgetContainer;
import org.cruxframework.crux.widgets.client.rollingtabs.RollingTabPanel;

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
			RollingTabPanel tabManager = tabScreen.getLayoutTabManager();
			String gadgetKey = Integer.toString(gadgetId);
			GadgetContainer container = getConfiguration().getContainer();
			Gadget profileGadget = container.getGadget(gadgetId);
			if (!openedCanvasGadgetIds.containsKey(gadgetKey))
			{
				profileGadget.deactivate();
				Gadget canvasGadget = container.createGadget(profileGadget, ContainerView.canvas);
				container.addGadget(canvasGadget);
				SimplePanel newGadgetChrome = new SimplePanel();
				newGadgetChrome.setHeight("100%");
				newGadgetChrome.setWidth("100%");
				newGadgetChrome.getElement().setId(getGadgetChromeId(canvasGadget.getId()));
				tabManager.add(newGadgetChrome, canvasGadget.getTitle());
				container.renderGadget(canvasGadget);
				int widgetIndex = tabManager.getWidgetIndex(newGadgetChrome);
				tabManager.selectTab(widgetIndex);

				openedCanvasGadgetIds.put(gadgetKey, canvasGadget.getId());
				openedCanvasGadgetTabs.put(gadgetKey, newGadgetChrome);
			}
			else
			{
				int canvasGadgetId = openedCanvasGadgetIds.remove(gadgetKey);
				Widget canvasGadgetChrome = openedCanvasGadgetTabs.remove(gadgetKey);
				
				container.getGadget(canvasGadgetId).remove();
				tabManager.remove(canvasGadgetChrome);
				profileGadget.activate();
			}
		}
	}
	
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