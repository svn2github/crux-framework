/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.core.client.screen;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.LazyPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A default lazy panel implementation, used by Crux engine to lazily parse the entire Document.
 * When a panel is declaratively created with visible attribute equals to false, an instance of 
 * that class is used to only parse the panel element and create the widget when it is first accessed
 * by {@code Screen.getWidget(String)} or when it becomes visible.
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class CruxLazyPanel extends LazyPanel implements br.com.sysmap.crux.core.client.screen.LazyPanel{
	private boolean initialized = false;
	private String innerHTML;
	private final String wizardId;
	
	/**
	 * Constructor
	 * @param innerHTML
	 * @param wizardId
	 */
	public CruxLazyPanel(String innerHTML, String wizardId)
	{
		this.innerHTML = innerHTML;
		this.wizardId = wizardId;
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.LazyPanel#ensureWidget()
	 */
	@Override
	public void ensureWidget()
	{
		if (!initialized)
		{
			ScreenFactory.getInstance().getScreen().cleanLazyDependentWidgets(wizardId);
			initialized = true;
		}
		super.ensureWidget();
	}

	/**
	 * @see com.google.gwt.user.client.ui.LazyPanel#createWidget()
	 */
	@Override
	protected Widget createWidget() 
	{
		if (ScreenFactory.getInstance().isParsing())
		{
			createWidgetAsync();
			return null;
		}
		else
		{
			doCreateWidget();
			return getWidget();
		}
	}			

	/**
	 * Schedule the doCreate method to run after the current event loop.
	 */
	private void createWidgetAsync()
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand()
		{
			public void execute()
			{
				doCreateWidget();
			}
		});
	}
			
	/**
	 * Create all internal widgets, declared on .crux.xml page.
	 */
	private void doCreateWidget()
	{
		Element lazyPanelElement = getElement();
		lazyPanelElement.setInnerHTML(innerHTML);
		innerHTML = null;
		//ScreenFactory.getInstance().parseDocument(lazyPanelElement);
		//TODO adaptar o lazy
	}
}