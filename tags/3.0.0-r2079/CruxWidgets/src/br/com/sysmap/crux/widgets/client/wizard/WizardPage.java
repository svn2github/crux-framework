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
package br.com.sysmap.crux.widgets.client.wizard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
public class WizardPage<T extends Serializable> extends AbstractWidgetStep<T>
{
	static final String PAGE_UNIQUE_ID = "__WizardPage_";
	
	private static RegisteredWizardDataSerializer dataSerializer;
	private Map<String, WizardCommandHandler<T>> commandHandlers = new HashMap<String, WizardCommandHandler<T>>();
	private Map<String, WizardCommandData> commands = new LinkedHashMap<String, WizardCommandData>();

	private String wizardId; 
	private WizardDataSerializer<T> wizardDataSerializer;
	
	@SuppressWarnings("unchecked")
    public WizardPage(String wizardId, String wizardDataId)
    {
		super((T)getWizardResource(wizardId, wizardDataId));
		
		Widget unique = Screen.get(PAGE_UNIQUE_ID);
		if (unique != null)
		{
			throw new WizardException(WidgetMsgFactory.getMessages().wizardPageDuplicatedWidgetOnPage());
		}
		Screen.add(PAGE_UNIQUE_ID, this);
		Element span = DOM.createSpan();
		RootPanel.getBodyElement().appendChild(span);
		initWidget(Label.wrap(span));
		super.setVisible(false);
		initWizardDataSerializer(wizardId, wizardDataId);
    }

	private static Serializable getWizardResource(String wizardId, String wizardDataId)
    {
	    if (!StringUtils.isEmpty(wizardDataId))
		{
			if (dataSerializer == null)
			{
				dataSerializer = GWT.create(RegisteredWizardDataSerializer.class);
			}
			WizardDataSerializer<?> wizardDataSerializer = (WizardDataSerializer<?>) dataSerializer.getWizardDataSerializer(wizardDataId);
			return wizardDataSerializer.getResource();
		}
		else
		{
			return null;	
		}
    }	
	
	@SuppressWarnings("unchecked")
	private void initWizardDataSerializer(String wizardId, String wizardDataId)
    {
	    this.wizardId = wizardId;
		if (!StringUtils.isEmpty(wizardDataId))
		{
			if (dataSerializer == null)
			{
				dataSerializer = GWT.create(RegisteredWizardDataSerializer.class);
			}
			wizardDataSerializer = (WizardDataSerializer<T>) dataSerializer.getWizardDataSerializer(wizardDataId);
			if (wizardDataSerializer != null)
			{
				wizardDataSerializer.setWizard(wizardId);
			}
		}
		else
		{
			wizardDataSerializer = null;	
		}
    }	

	@Override
	public boolean addCommand(String id, String label, WizardCommandHandler<T> handler, int order)
	{
		if (!commands.containsKey(id))
		{
			WizardCommandData commandData = new WizardCommandData(id, label, order);
			commands.put(id, commandData);
			commandHandlers.put(id, handler);
			return true;
		}
		return false;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public WizardCommandData getCommand(String id)
	{
		return commands.get(id);
	}
	
	/**
	 * @param command
	 * @return
	 */
	@Override
	public boolean removeCommand(String id)
	{
		if (commands.containsKey(id))
		{
			commands.remove(id);
			return true;
		}
		return false;
	}
	@Override
	public void setVisible(boolean visible)
	{
	}
	
	/**
	 * @param id
	 * @param label
	 * @param commandEvent
	 * @param order
	 */
	void addCommand(String id, String label, final Event commandEvent, int order)
	{
		WizardCommandHandler<T> handler = new WizardCommandHandler<T>()
		{
			public void onCommand(WizardCommandEvent<T> event)
			{
				Events.callEvent(commandEvent, event);
			}
		};
		addCommand(id, label, handler, order);
	}

	/**
	 * @param commandId
	 * @param wizardCommandEvent
	 */
	void fireCommandEvent(String commandId, WizardCommandEvent<T> wizardCommandEvent)
    {
		WizardCommandHandler<T> handler = commandHandlers.get(commandId);
		if (handler != null)
		{
			try
            {
	            handler.onCommand(wizardCommandEvent);
            }
            catch (RuntimeException e)
            {
            	Crux.getErrorHandler().handleError(WidgetMsgFactory.getMessages().wizardCommandError(commandId, e.getMessage()), e);
            }
		}
    }

	/**
	 * @param commandId
	 */
	void fireCommandEvent(String commandId)
	{
		WizardCommandEvent<T> wizardCommandEvent = new WizardCommandEvent<T>(new PageWizardProxy<T>(wizardId, wizardDataSerializer));
		fireCommandEvent(commandId, wizardCommandEvent);
	}

	/**
	 * @param previousStep
	 */
	void fireEnterEvent(String previousStep)
	{
		EnterEvent.fire(this, new PageWizardProxy<T>(wizardId, wizardDataSerializer), previousStep);
	}

	/**
	 * @param nextStep
	 */
	LeaveEvent<T> fireLeaveEvent(String nextStep)
	{
		return LeaveEvent.fire(this, new PageWizardProxy<T>(wizardId, wizardDataSerializer), nextStep);
	}

	/**
	 * @return
	 */
	WizardCommandData[] listCommands()
    {
	    return commands.values().toArray(new WizardCommandData[commands.size()]);
    }
}
