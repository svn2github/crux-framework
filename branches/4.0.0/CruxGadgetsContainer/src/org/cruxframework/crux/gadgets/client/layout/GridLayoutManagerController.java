package org.cruxframework.crux.gadgets.client.layout;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.gadgets.client.GadgetContainerMsg;
import org.cruxframework.crux.gadgets.client.container.Gadget;
import org.cruxframework.crux.gadgets.client.container.GadgetContainer;
import org.cruxframework.crux.gadgets.client.container.GadgetMetadata;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("gridLayoutController")
public class GridLayoutManagerController
{
	@Create
	protected GadgetContainerMsg messages;
	
	@Create
	protected LayoutScreen screen;

	private GadgetShindigClassHandler shindigClassHandler;
	
	@Expose
	public void onLoad()
	{
		createNativeConfigureFunction(this);
	}
	
	/**
	 * Configure the Gadgets Dashboard. Called once, when the container page is loaded.
	 * @param configuration
	 */
	public void configure()
	{
		this.shindigClassHandler = createShindigClassHandler();
		configureContainer();
		renderGadgets();
	}

	/**
	 * 
	 * @param gadgetId
	 * @param styleName
	 * @param menuOptionsButtonElement
	 * @param hasSetting
	 */
	public void openMenuOptions(int gadgetId, String styleName, final Element menuOptionsButtonElement, boolean hasSetting)
	{
		class Wrapper extends UIObject
		{
			public Wrapper()
            {
	            setElement(menuOptionsButtonElement);
            }
		};
		
		DialogBox optionsDialog = new DialogBox(true);
		VerticalPanel optionsFlowPanel = new VerticalPanel();
		optionsDialog.setWidget(optionsFlowPanel);
		
		Anchor delete = createDeleteGadgetButton(gadgetId, optionsDialog);
		optionsFlowPanel.add(delete);
		
		if (hasSetting)
		{
			Anchor settings = createSettingsGadgetButton(gadgetId, optionsDialog);
			optionsFlowPanel.add(settings);
		}
		Anchor about = new Anchor(messages.aboutGadgetLink());
		optionsFlowPanel.add(about);

		optionsDialog.setStyleName(styleName);
		optionsDialog.showRelativeTo(new Wrapper());
	}
	
	/**
	 * 
	 * @param gadgetId
	 * @param profileView
	 */
	public void changeGadgetView(int gadgetId, boolean profileView)
	{
		UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
		if (profileView)
		{
			String gadgetUrl = GadgetContainer.get().getGadget(gadgetId).getUrl();
			if (StringUtils.isEmpty(gadgetUrl))
			{
				Crux.getErrorHandler().handleError(messages.gadgetNotFound(gadgetId));
			}
			urlBuilder.setParameter("url", gadgetUrl);
		}
		else
		{
			urlBuilder.removeParameter("url");
		}
		Window.Location.assign(urlBuilder.buildString());
	}
	
	/**
	 * Retrieve the DIV element used as wrapper to render the requested gadget 
	 * @param gadgetId gadget identifier
	 * @return
	 */
	public Element getGadgetChrome(int gadgetId)
	{
		Element element = DOM.getElementById(getGadgetChromeId(gadgetId));
		return element;
	}
	
	/**
	 * Render all gadgets
	 */
	protected void renderGadgets()
    {
		renderGadgets(GadgetContainer.get().getMetadata());
    }

	/**
	 * 
	 * @return
	 */
	protected GadgetShindigClassHandler createShindigClassHandler()
	{
		return new GadgetShindigClassHandler(this);
	}
	
	/**
	 * Create a hook function, called by GadgetConfigController to start the container configuration 
	 * @param controller
	 */
	protected native void createNativeConfigureFunction(GridLayoutManagerController controller)/*-{
		if ($wnd.__configureLayoutManager)
		{
			controller.@org.cruxframework.crux.gadgets.client.layout.GridLayoutManagerController::reportNativeConfigureFunctionError()();
		}
		else
		{
			$wnd.__configureLayoutManager = function(){
				controller.@org.cruxframework.crux.gadgets.client.layout.GridLayoutManagerController::configure()();
			};
		}
    }-*/;

	/**
	 * Report error caused by a duplicated layoutManager.
	 */
	protected void reportNativeConfigureFunctionError()
	{
		Crux.getErrorHandler().handleError(messages.duplicatedLayoutManager());
	}
	
	/**
	 * Configure the container.
	 */
	protected void configureContainer()
    {
	    createdGridDashboard();
		shindigClassHandler.createNativeLayoutManager();
		shindigClassHandler.createNativeGadgetClass();
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				makeDashboardSortable(GridLayoutManagerController.this);
			}
		});
    }

	/**
	 * Create the container grid for gadgets.
	 */
	protected void createdGridDashboard()
    {
	    Element gridContainer = screen.getGridContainer().getElement();
		gridContainer.setInnerHTML(generateGridDashboard());
    }

	/**
	 * 
	 * @param gadgetId
	 * @param optionsDialog 
	 * @return
	 */
	protected Anchor createSettingsGadgetButton(final int gadgetId, final DialogBox optionsDialog)
    {
	    Anchor settings = new Anchor(messages.settingsGadgetLink());
	    settings.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				GadgetContainer.get().getGadget(gadgetId).handleOpenUserPrefsDialog();
				optionsDialog.hide();
			}
		});
	    return settings;
    }

	/**
	 * 
	 * @param gadgetId
	 * @param optionsDialog
	 * @return
	 */
	protected Anchor createDeleteGadgetButton(final int gadgetId, final DialogBox optionsDialog)
    {
	    Anchor delete = new Anchor(messages.deleteGadgetLink());
	    delete.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				GadgetContainer.get().removeGadget(gadgetId);
				removeDom(gadgetId);
				optionsDialog.hide();
			}

			private void removeDom(int gadgetId)
            {
				Element gadgetChrome = DOM.getElementById(getGadgetChromeId(gadgetId));
				gadgetChrome.removeFromParent();
            }
		});
	    return delete;
    }
	
	
	/**
	 * Render the gadgets on the page.
	 * @param gadgetConfigs
	 * @param isDebug
	 * @param canvasHeight 
	 * @param canvasWidth 
	 */
	protected void renderGadgets(Array<Array<GadgetMetadata>> gadgetConfigs)
	{
		GadgetContainer container = GadgetContainer.get();

		int numCols = gadgetConfigs.size();
		for (int i=0; i < numCols; i++)
		{
			Array<GadgetMetadata> columns = gadgetConfigs.get(i);
			int numRows = columns.size();
			for (int j=0; j < numRows; j++)
			{
				Gadget gadget = container.createGadget(columns.get(j));
				container.addGadget(gadget);
				container.renderGadget(gadget);
			}
		}
	}
		
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetId
	 * @return
	 */
	protected String getGadgetChromeId(int gadgetId)
	{
		return "gadget-chrome-"+gadgetId;
	}
	
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetId
	 * @return
	 */
	protected String getGadgetId(Element gadgetChrome)
	{
		return gadgetChrome.getId().substring(14);
	}

	/**
	 * Generate the HTML structure for the Gadgets dashboard.
	 * @return
	 */
	protected String generateGridDashboard()
	{
		StringBuilder tableHtml = new StringBuilder();
		Array<Array<GadgetMetadata>> gadgetConfigs = GadgetContainer.get().getMetadata();
		tableHtml.append("<div class='LayoutGrid' style='width:100%;'>"); 
		
		int numColumns = gadgetConfigs.size();
		int colWidth = 100 / numColumns;
		int gadgetId = 0;
		for (int i=0; i< numColumns; i++)
		{
			Array<GadgetMetadata> column = gadgetConfigs.get(i);
			int numRows = column.size();
			tableHtml.append("<div class='LayoutColumn' style='width:"+colWidth+"%;float:left;padding-bottom:100px;'>"); 
			for (int j=0; j< numRows; j++)
			{
				tableHtml.append("<div id='"+getGadgetChromeId(gadgetId++)+"' class='gadgets-gadget-chrome'></div>"); 
			}
			tableHtml.append("</div>");
		}
		tableHtml.append("</div>");
		return tableHtml.toString();
	}
	
	/**
	 * Transform the Gadgets dashboard's HTML structure into a dragable container. It will consist in a collection of 
	 * columns with sortable dragable elements.
	 * @param controller
	 */
	protected native void makeDashboardSortable(GridLayoutManagerController controller)/*-{
		$wnd.$(function() {
			var draggingFrame;
			$wnd.$( ".LayoutColumn" ).sortable({
				connectWith: ".LayoutColumn",
				start: function(event, ui){
					var gadgetId = controller.@org.cruxframework.crux.gadgets.client.layout.GridLayoutManagerController::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
					
					var framePrefix = $wnd.shindig.container.gadgetClass.prototype.GADGET_IFRAME_PREFIX_;
					draggingFrame = $doc.getElementById( framePrefix+gadgetId );
					draggingFrame.style.display = 'none';
				},
				stop: function(event, ui){
					var gadgetId = controller.@org.cruxframework.crux.gadgets.client.layout.GridLayoutManagerController::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
					if (draggingFrame) {
						draggingFrame.style.display = '';
						draggingFrame = null;
					}
					var gadget = $wnd.shindig.container.getGadget(gadgetId);
  			    	gadget.refresh();
				}
			}).disableSelection();
		});	
	}-*/;
	
	/**
	 * Screen wrapper for template widgets.
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static interface LayoutScreen extends ScreenWrapper
	{
		HTML getGridContainer();
	}
}