package org.cruxframework.cruxsite.client;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.widgets.client.disposal.topmenudisposal.TopMenuDisposal;
import org.cruxframework.crux.widgets.client.event.SelectEvent;

import com.google.gwt.user.client.ui.Label;

@Controller("mainController")
public class MainController 
{
	@Expose
	public void showViewInfo(SelectEvent evt)
	{
		showView("info", evt);
	}

	@Expose
	public void showViewFast(SelectEvent evt)
	{
		showView("desempenho", evt);
	}
	
	@Expose
	public void showViewJava(SelectEvent evt)
	{
		showView("escrevaJava", evt);
	}
	
	@Expose
	public void showViewCrossDev(SelectEvent evt)
	{
		showView("multiplataforma", evt);
	}
	
	@Expose
	public void showViewOpenSource(SelectEvent evt)
	{
		showView("opensource", evt);
	}
	
	@Expose
	public void showViewAbout(SelectEvent evt)
	{
		showView("sobreocrux", evt);
	}
	
	@Expose
	public void showViewExamples(SelectEvent evt)
	{
		showView("exemplos", evt);
	}
	
	@Expose
	public void showAppHelloCross(SelectEvent evt)
	{
		showSampleApp(evt, "CrossDeviceHelloWorld");
	}
	
	@Expose
	public void showAppSite(SelectEvent evt)
	{
		showSampleApp(evt, "CruxSite");
	}
	
	@Expose
	public void showAppShowcase(SelectEvent evt)
	{
		showSampleApp(evt, "CrossDeviceShowcase");
	}

	/**
	 * @param evt
	 * @param nomeAplicacao
	 */
	private void showSampleApp(SelectEvent evt, String nomeAplicacao) 
	{
		showView("aplicacaoExemplo", evt);
		Label appName = (Label) View.getView("aplicacaoExemplo").getWidget("sampleAppName");
		appName.setText(nomeAplicacao);
	}
	
	/**
	 * @param viewName
	 * @param evt 
	 */
	private void showView(String viewName, SelectEvent evt) 
	{
		if(evt != null)
		{
			evt.setCanceled(true);
		}
		
		TopMenuDisposal disposal = (TopMenuDisposal) Screen.get("menuDisposal");
		disposal.showView(viewName, true);
	}
}
