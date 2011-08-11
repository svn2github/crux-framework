package org.cruxframework.crux.gadgets.client.controller;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.gadgets.client.TabLayoutMsg;
import org.cruxframework.crux.gadgets.client.dto.GadgetMetadata;
import org.cruxframework.crux.gadgets.client.dto.GadgetsConfiguration;
import org.cruxframework.crux.gadgets.client.dto.GadgetsConfiguration.ContainerView;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@Controller("gridLayoutController")
public class GridLayoutManagerController
{
	@Create
	protected TabLayoutMsg messages;
	
	@Create
	protected LayoutScreen screen;

	private GadgetsConfiguration configuration;
	
	@Expose
	public void onLoad()
	{
		createNativeConfigureFunction(this);
	}
	
	/**
	 * Configure the Gadgets Dashboard. Called once, when the container page is loaded.
	 * @param configuration
	 */
	public void configure(GadgetsConfiguration configuration)
	{
		this.configuration = configuration;
		configureContainer();
		String canvasHeight = null;
		String canvasWidth = null;
		if (configuration.getCurrentView() == ContainerView.canvas)
		{
			canvasHeight = getContainerHeight();
			canvasWidth = "100%";
		}
		renderGadgets(configuration.getMetadata(), configuration.isDebug(), canvasHeight, canvasWidth);
		
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
	 * Retrieve the height to be used for gadgets when rendering on canvas view.
	 * @return
	 */
	protected native String getContainerHeight()/*-{
	    return $wnd._containerHeight || null;
    }-*/;

	/**
	 * Create a hook function, called by GadgetConfigController to start the container configuration 
	 * @param controller
	 */
	protected native void createNativeConfigureFunction(GridLayoutManagerController controller)/*-{
		if ($wnd.__configureLayoutManager)
		{
			controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::reportNativeConfigureFunctionError()();
		}
		else
		{
			$wnd.__configureLayoutManager = function(config){
				controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::configure(Lorg/cruxframework/crux/gadgets/client/dto/GadgetsConfiguration;)(config);
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
		createInheritsFunction();
		createNativeLayoutManager(this);
		createNativeGadgetClass();
		configureGadgetClass();
    }

	/**
	 * Create the native javascript class used by shindig to render a gadget
	 */
	protected void createNativeGadgetClass()
    {
		createGadgetClass();
		createGadgetClassGetIframeUrlFunction();
		createGadgetClassGetAditionalParamsFunction(configuration.isCajaEnabled(), configuration.isDebug());
		createGadgetClassIsHomeViewFunction();
		createGadgetClassGetTitleBarContentFunction();
		createGadgetClassChangeViewFunction(this);
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
	 * Create a base function for native javascript classes inheritance.
	 */
	protected native void createInheritsFunction()/*-{
		Function.prototype.inherits = function(parentCtor) {
			function tempCtor() {};
			tempCtor.prototype = parentCtor.prototype;
			this.superClass_ = parentCtor.prototype;
			this.prototype = new tempCtor();
			this.prototype.constructor = this;
		}; 
	}-*/;
	
	/**
	 * Create the gadget Layout Manager and associate it with gadget.container.layoutManager.
	 * @param controller
	 */
	protected native void createNativeLayoutManager(GridLayoutManagerController controller)/*-{
		var CruxLayoutManager = {};
		
		CruxLayoutManager = function() {
		    $wnd.shindig.LayoutManager.call(this);
		};
		
		CruxLayoutManager.inherits($wnd.shindig.LayoutManager);
		
		CruxLayoutManager.prototype.getGadgetChrome = function(gadget) {
		    return controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::getGadgetChrome(I)(gadget.id);
		};
		
		$wnd.shindig.container.layoutManager = new CruxLayoutManager();
	}-*/;

	/**
	 * Create the javascript class used by shindig to render a gadget.
	 */
	protected native void createGadgetClass()/*-{
		$wnd.CruxGadgetClass = function(opt_params) {
    		$wnd.shindig.Gadget.call(this, opt_params);
			this.serverBase_ = '/gadgets/'; // default gadget server
			if (!this.view) {
				this.view = $wnd.shindig.container.view_;
			}

			if (!this.width) {
				this.width = '100%';
			}
			var subClass = this.requiresPubSub2 ? $wnd.shindig.OAAIfrGadget : $wnd.shindig.IfrGadget;
			for (var name in subClass) {
			    if (subClass.hasOwnProperty(name) && name !== "getIframeUrl") {
				    this[name] = subClass[name];
				}
			}     		
  		};

		$wnd.CruxGadgetClass.inherits($wnd.shindig.BaseIfrGadget);
	}-*/;
	
	/**
	 * Create the getIframeUrl function for the gadget class. 
	 */
	protected native void createGadgetClassGetIframeUrlFunction()/*-{
  		$wnd.CruxGadgetClass.prototype.getIframeUrl = function() {
			return this.serverBase_ + 'ifr?' +
				'container=' + this.CONTAINER +
				'&mid=' + this.id +
				'&nocache=' + $wnd.shindig.container.nocache_ +
				'&country=' + $wnd.shindig.container.country_ +
				'&lang=' + $wnd.shindig.container.language_ +
				'&view=' + this.view +
				(this.specVersion ? '&v=' + this.specVersion : '') +
				((!this.requiresPubSub2 && $wnd.shindig.container.parentUrl_) ? '&parent=' + encodeURIComponent($wnd.shindig.container.parentUrl_) : '') +
				//(this.debug ? '&debug=1' : '') +
				this.getAdditionalParams() +
				this.getUserPrefsParams() +
				(this.secureToken ? '&st=' + this.secureToken : '') +
				'&url=' + encodeURIComponent(this.specUrl) +
				(!this.requiresPubSub2? '#rpctoken=' + this.rpcToken:'') +
				(this.viewParams ?
				'&view-params=' + encodeURIComponent($wnd.gadgets.json.stringify(this.viewParams)) : '') +
				(this.hashData ? (!this.requiresPubSub2?'&':'#') + this.hashData : '');
  		};
	}-*/;
	
	/**
	 * Create the getAdditionalParams function for the gadget class. 
	 * 
	 * @param useCaja
	 * @param isDebug
	 */
	protected native void createGadgetClassGetAditionalParamsFunction(boolean useCaja, boolean isDebug)/*-{
  		$wnd.CruxGadgetClass.prototype.getAdditionalParams = function() {
    		var params = '';

    		if (useCaja) {
      			params += "&caja=1&libs=caja";
    		}
    		if (isDebug) {
      			params += "&debug=1";
    		}
    		return params;
  		};
	}-*/;
	
	/**
	 * Create the isHomeView function for the gadget class. 
	 */
	protected native void createGadgetClassIsHomeViewFunction()/*-{
  		$wnd.CruxGadgetClass.prototype.isHomeView = function() {
  			return this.view === "home" || this.view === "profile" || this.view === "default";
  		};
  	}-*/;
  		
	/**
	 * Create the getTitleBarContent function for the gadget class. 
	 */
	protected native void createGadgetClassGetTitleBarContentFunction()/*-{
		$wnd.CruxGadgetClass.prototype.getTitleBarContent = function(continuation) {
			
			var settingsButton = this.hasViewablePrefs_() ?
									'<a href="#" onclick="shindig.container.getGadget(' + this.id +
									').handleOpenUserPrefsDialog();return false;" class="' + this.cssClassTitleButtonSettings +
									'"></a>'
									: '';
			var toogleButton = '<a href="#" onclick="shindig.container.getGadget(' + this.id +
						 ').handleToggle();return false;" class="' + this.cssClassTitleButtonToogle +
						 '"></a>';

			var fullScreenButton = '<a href="#" onclick="shindig.container.getGadget(' + this.id +
						 ').changeView();return false;" class="' + 
						 (this.isHomeView()?this.cssClassTitleButtonFullScreen:this.cssClassTitleButtonRestoreScreen) +
						 '"></a>';						
									
			var menuButton = '<a href="#" onclick="shindig.container.getGadget(' + this.id +
						 ').changeView();return false;" class="' + this.cssClassTitleButtonMenu + '"></a>';						

			continuation('<div id="' + this.cssClassTitleBar + '-' + this.id +
						 '" class="' + this.cssClassTitleBar + '"><span id="' +
						 this.getIframeId() + '_title" class="' +
						 this.cssClassTitle + '">' + (this.title ? this.title : 'Title') + '</span><div class="' +
						 this.cssClassTitleButtonBar + '">' + settingsButton + fullScreenButton + toogleButton + menuButton + 
						 '</div></div>');
		}; 


		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonMenu = 'gadgets-gadget-title-button-menu';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonSettings = 'gadgets-gadget-title-button-settings';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonToogle = 'gadgets-gadget-title-button-toogle';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonFullScreen = 'gadgets-gadget-title-button-full-screen';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonRestoreScreen = 'gadgets-gadget-title-button-restore-screen';
	}-*/;
	//TODO: adicionar botao para permissoes opensocial
	//TODO: adicionar controle para informar se deve adicionar botao de navegacao na view canvas
	
	/**
	 * Create the openInCanvas function for the gadget class. 
	 */
	protected native void createGadgetClassChangeViewFunction(GridLayoutManagerController controller)/*-{
  		$wnd.CruxGadgetClass.prototype.changeView = function() {
			controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::changeGadgetView(IZ)(this.id, this.isHomeView());  		
  		};
	}-*/;
	
	
	/**
	 * Create the openInCanvas function for the gadget class. 
	 */
	protected native void configureGadgetClass()/*-{
  		$wnd.shindig.container.gadgetClass = $wnd.CruxGadgetClass;
	}-*/;
	
	/**
	 * 
	 * @param gadgetId
	 * @param homeView
	 */
	protected void changeGadgetView(int gadgetId, boolean homeView)
	{
		UrlBuilder urlBuilder = Window.Location.createUrlBuilder();
		if (homeView)
		{
			String gadgetUrl = getGadgetUrl(gadgetId);
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
	 * 
	 * @param gadgetId
	 * @return
	 */
	protected native String getGadgetUrl(int gadgetId)/*-{
		var gadget = $wnd.shindig.container.getGadget(gadgetId)
		return (gadget?gadget.specUrl:'');
	}-*/;
	
	/**
	 * Render the gadgets on the page.
	 * @param gadgetConfigs
	 * @param isDebug
	 * @param canvasHeight 
	 * @param canvasWidth 
	 */
	protected native void renderGadgets(JsArray<JsArray<GadgetMetadata>> gadgetConfigs, boolean isDebug, String canvasHeight, String canvasWidth)/*-{
		
		function isRequiresSubPub2(gadget)
		{
			var requiresPubSub2 = false;
			var arr = gadget.features;
			if (arr) {
				for(var i = 0; i < arr.length; i++) {
					if (arr[i] === "pubsub-2") {
						requiresPubSub2 = true;
						break;
					}
				}
			}
			return requiresPubSub2;
		}
		for (var i = 0; i < gadgetConfigs.length; i++) {
			var gadgetConfig = gadgetConfigs[i];
			for (var j = 0; j < gadgetConfig.length; j++) {
				var gadget = $wnd.shindig.container.createGadget(
						{debug: (isDebug?1:0), 'specUrl': gadgetConfig[j].url, 
						'title': gadgetConfig[j].title, 'userPrefs': gadgetConfig[j].userPrefs, 
						'height': (canvasHeight!=null?canvasHeight:gadgetConfig[j].height), 'width': (canvasWidth!=null?canvasWidth:gadgetConfig[j].width), 
						'requiresPubSub2': isRequiresSubPub2(gadgetConfig[j])});
						//'secureToken': escape(generateSecureToken())});
				$wnd.shindig.container.addGadget(gadget);
				$wnd.shindig.container.renderGadget(gadget);
			}
		}
	}-*/;
	
	//TODO gerar a secureToken
	
	/**
	 * Retrieve the DIV element used as wrapper to render the requested gadget 
	 * @param gadgetId gadget identifier
	 * @return
	 */
	protected Element getGadgetChrome(int gadgetId)
	{
		Element element = DOM.getElementById(getGadgetChromeId(Integer.toString(gadgetId)));
		return element;
	}
	
	/**
	 * Retrieve the identifier of the element used as wrapper to render the requested gadget
	 * @param gadgetId
	 * @return
	 */
	protected String getGadgetChromeId(String gadgetId)
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
		JsArray<JsArray<GadgetMetadata>> gadgetConfigs = configuration.getMetadata();
		tableHtml.append("<div class='LayoutGrid' style='width:100%;height:"+getContainerHeight()+";'>"); 
		
		int numColumns = gadgetConfigs.length();
		int colWidth = 100 / numColumns;
		int gadgetId = 0;
		for (int i=0; i< numColumns; i++)
		{
			JsArray<GadgetMetadata> column = gadgetConfigs.get(i);
			int numRows = column.length();
			tableHtml.append("<div class='LayoutColumn' style='width:"+colWidth+"%;float:left;padding-bottom:100px;'>"); 
			for (int j=0; j< numRows; j++)
			{
				tableHtml.append("<div id='"+getGadgetChromeId(Integer.toString(gadgetId++))+"' class='gadgets-gadget-chrome'></div>"); 
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
					var gadgetId = controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
					
					var framePrefix = $wnd.shindig.container.gadgetClass.prototype.GADGET_IFRAME_PREFIX_;
					draggingFrame = $doc.getElementById( framePrefix+gadgetId );
					draggingFrame.style.display = 'none';
				},
				stop: function(event, ui){
					var gadgetId = controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::getGadgetId(Lcom/google/gwt/user/client/Element;)(ui.item[0]);
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