package org.cruxframework.crux.gadgets.client.controller;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.ScreenWrapper;
import org.cruxframework.crux.gadgets.client.TabLayoutMsg;
import org.cruxframework.crux.gadgets.client.dto.GadgetMetadata;
import org.cruxframework.crux.gadgets.client.dto.GadgetsConfiguration;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
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
		configureContainer(configuration);
		renderGadgets(configuration.getMetadata(), configuration.isDebug());
		
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
	 * Create the container grid for gadgets and the layoutManager to control that grid.
	 * @param configuration
	 */
	protected void configureContainer(GadgetsConfiguration configuration)
    {
	    Element gridContainer = screen.getGridContainer().getElement();
		StringBuilder tableHtml = new StringBuilder();
		generateGridDashboard(tableHtml, configuration.getMetadata());
		gridContainer.setInnerHTML(tableHtml.toString());
		createInheritsFunction();
		createNativeLayoutManager(this);
		createNativeGadgetClass(this, configuration.useCaja(), configuration.isDebug());
		setContainerParentURL(configuration.getContainerParentUrl());
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
	 * Create the gadget Layout Manager and associate it with gadget.container.layoutManager
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
	 * Create the javascript class used by shindig to render a gadget and associate it with 
	 * shindig.container.gadgetClass
	 * @param useCaja
	 * @param isDebug
	 */
	protected native void createNativeGadgetClass(GridLayoutManagerController controller, boolean useCaja, boolean isDebug)/*-{
		var CruxGadgetClass = function(opt_params) {
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

  		CruxGadgetClass.inherits($wnd.shindig.BaseIfrGadget);

  		CruxGadgetClass.prototype.getIframeUrl = function() {
			return this.serverBase_ + 'ifr?' +
				'container=' + this.CONTAINER +
				'&mid=' + this.id +
				'&nocache=' + $wnd.shindig.container.nocache_ +
				'&country=' + $wnd.shindig.container.country_ +
				'&lang=' + $wnd.shindig.container.language_ +
				'&view=' + this.view +
				(this.specVersion ? '&v=' + this.specVersion : '') +
				((!this.requiresPubSub2 && $wnd.shindig.container.parentUrl_) ? '&parent=' + encodeURIComponent($wnd.shindig.container.parentUrl_) : '') +
				(this.debug ? '&debug=1' : '') +
				this.getAdditionalParams() +
				this.getUserPrefsParams() +
				(this.secureToken ? '&st=' + this.secureToken : '') +
				'&url=' + encodeURIComponent(this.specUrl) +
				(!this.requiresPubSub2? '#rpctoken=' + this.rpcToken:'') +
				(this.viewParams ?
				'&view-params=' + encodeURIComponent(gadgets.json.stringify(this.viewParams)) : '') +
				(this.hashData ? (!this.requiresPubSub2?'&':'#') + this.hashData : '');
  		};

  		CruxGadgetClass.prototype.getAdditionalParams = function() {
    		var params = '';

    		if (useCaja) {
      			params += "&caja=1&libs=caja";
    		}
    		if (isDebug) {
      			params += "&debug=1";
    		}
    		return params;
  		};

  		CruxGadgetClass.prototype.isHomeView = function() {
  			return this.view === "home" || this.view === "profile" || this.view === "default";
  		};
  		
		CruxGadgetClass.prototype.getTitleBarContent = function(continuation) {
			var settingsButton = this.hasViewablePrefs_() ?
									'<a href="#" onclick="shindig.container.getGadget(' + this.id +
									').handleOpenUserPrefsDialog();return false;" class="' + this.cssClassTitleButtonSettings +
									'"></a>'
									: '';
			var toogleButton = '<a href="#" onclick="shindig.container.getGadget(' + this.id +
						 ').handleToggle();return false;" class="' + this.cssClassTitleButtonToogle +
						 '"></a>';						
			var fullScreenButton = this.isHomeView()?'<a href="#" onclick="shindig.container.getGadget(' + this.id +
						 ').openInCanvas();return false;" class="' + this.cssClassTitleButtonFullScreen +
						 '"></a>':'';						
									
			continuation('<div id="' + this.cssClassTitleBar + '-' + this.id +
						 '" class="' + this.cssClassTitleBar + '"><span id="' +
						 this.getIframeId() + '_title" class="' +
						 this.cssClassTitle + '">' + (this.title ? this.title : 'Title') + '</span><div class="' +
						 this.cssClassTitleButtonBar + '">' + settingsButton + toogleButton + fullScreenButton +
						 '</div></div>');
		}; 

  		CruxGadgetClass.prototype.openInCanvas = function() {
			controller.@org.cruxframework.crux.gadgets.client.controller.GridLayoutManagerController::goToCanvas(I)(gadget.id);  		
  		};

		CruxGadgetClass.prototype.cssClassTitleButtonSettings = 'gadgets-gadget-title-button-settings';
		CruxGadgetClass.prototype.cssClassTitleButtonToogle = 'gadgets-gadget-title-button-toogle';
		CruxGadgetClass.prototype.cssClassTitleButtonFullScreen = 'gadgets-gadget-title-button-full-screen';
		 
  		$wnd.shindig.container.gadgetClass = CruxGadgetClass;
	}-*/;//TODO: adicionar botao para permissoes opensocial
	//TODO: adicionar controle para informar se deve adicionar botao de navegacao na view canvas
	
	/**
	 * 
	 * @param gadgetId
	 */
	protected void goToCanvas(int gadgetId)
	{
		
	}
	
	/**
	 * Set the parentUrl property of the shindig container.
	 * @param parentURL
	 */
	protected native void setContainerParentURL(String parentURL)/*-{
		$wnd.shindig.container.setParentUrl(parentURL);
	}-*/;
	
	/**
	 * Render the gadgets on the page.
	 * @param gadgetConfigs
	 * @param isDebug
	 */
	protected native void renderGadgets(JsArray<JsArray<GadgetMetadata>> gadgetConfigs, boolean isDebug)/*-{
		
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
						'height': gadgetConfig[j].height, 'width': gadgetConfig[j].width, 
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
	 * @param tableHtml
	 */
	protected void generateGridDashboard(StringBuilder tableHtml, JsArray<JsArray<GadgetMetadata>> gadgetConfigs)
	{
		tableHtml.append("<div class='LayoutGrid' style='width:100%;'>"); 
		
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
	}
	
	/**
	 * Transform the Gadgets dashboard's HTML structure into a dragable container. It will consist in a collection of 
	 * columns with sortable dragable elements.
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