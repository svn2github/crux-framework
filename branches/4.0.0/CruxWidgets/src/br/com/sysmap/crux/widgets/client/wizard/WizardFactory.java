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

import br.com.sysmap.crux.core.client.Crux;
import br.com.sysmap.crux.core.client.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.client.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagChild;
import br.com.sysmap.crux.core.client.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.client.declarative.TagChildren;
import br.com.sysmap.crux.core.client.declarative.TagEvent;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEvents;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.event.bind.EvtBind;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.core.client.screen.parser.CruxMetaDataElement;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.creator.children.AllChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.widgets.client.event.CancelEvtBind;
import br.com.sysmap.crux.widgets.client.event.FinishEvtBind;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlHorizontalAlign;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlPosition;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlVerticalAlign;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

class WizardContext extends WidgetCreatorContext
{

	public String stepId;
	public String stepLabel;
	public String stepOnEnter;
	public String stepOnLeave;
	public String enabled;
}


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@DeclarativeFactory(id="wizard", library="widgets")
public class WizardFactory extends WidgetCreator<Wizard<?>, WizardContext>
{
	private WizardInstantiator instantiator = GWT.create(WizardInstantiator.class);
	
	@Override
    public Wizard<?> instantiateWidget(CruxMetaDataElement element, String widgetId) throws InterfaceConfigException
    {
	    String wizardContextObject = element.getProperty("wizardContextObject");
		return instantiator.createWizard(widgetId, wizardContextObject);
    }

	@Override
	@TagEvents({
		@TagEvent(FinishEvtBind.class),
		@TagEvent(CancelEvtBind.class)
	})
	public void processEvents(WizardContext context) throws InterfaceConfigException
	{
	    super.processEvents(context);
	}

	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("wizardContextObject")
	})
	public void processAttributes(WizardContext context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	@Override
	public void postProcess(WizardContext context) throws InterfaceConfigException
	{
		Wizard<?> widget = context.getWidget();
		widget.first();				
	}
	
	
	@Override
	@TagChildren({
		@TagChild(WizardChildrenProcessor.class)
	})
	public void processChildren(WizardContext context) throws InterfaceConfigException {}
	
	public static class WizardChildrenProcessor extends AllChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagChildren({
			@TagChild(NavigationBarProcessor.class),
			@TagChild(StepsProcessor.class),
			@TagChild(ControlBarProcessor.class)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="navigationBar", minOccurs="0")
	public static class NavigationBarProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="position", type=ControlPosition.class, defaultValue="north"),
			@TagAttributeDeclaration(value="showAllSteps", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="allowSelectStep", type=Boolean.class, defaultValue="true"),
			@TagAttributeDeclaration(value="verticalAlignment", type=ControlVerticalAlign.class, defaultValue="top"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=ControlHorizontalAlign.class, defaultValue="left"),
			@TagAttributeDeclaration("labelStyleName"),
			@TagAttributeDeclaration("horizontalSeparatorStyleName"),
			@TagAttributeDeclaration("verticalSeparatorStyleName")
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException 
		{
			Wizard<?> widget = context.getWidget();
			String positionAttr = context.readChildProperty("position");
			ControlPosition position = ControlPosition.north;
			if (!StringUtils.isEmpty(positionAttr))
			{
				position = ControlPosition.valueOf(positionAttr);
			}
			
			boolean vertical = position.equals(ControlPosition.east) || position.equals(ControlPosition.west);
			boolean showAllSteps = true;
			String showAllStepsAttr = context.readChildProperty("showAllSteps");
			if (!StringUtils.isEmpty(showAllStepsAttr ))
			{
				showAllSteps = Boolean.parseBoolean(showAllStepsAttr);
			}
			if (vertical)
			{
				ControlVerticalAlign verticalAlign = ControlVerticalAlign.top;
				String verticalAlignmentAttr = context.readChildProperty("verticalAlignment");
				if (!StringUtils.isEmpty(verticalAlignmentAttr))
				{
					verticalAlign = ControlVerticalAlign.valueOf(verticalAlignmentAttr);
				}
				widget.setNavigationBar(vertical, showAllSteps, position, verticalAlign);
			}
			else
			{
				ControlHorizontalAlign horizontalAlign = ControlHorizontalAlign.left;
				String horizontalAlignmentAttr = context.readChildProperty("horizontalAlignment");
				if (!StringUtils.isEmpty(horizontalAlignmentAttr))
				{
					horizontalAlign = ControlHorizontalAlign.valueOf(horizontalAlignmentAttr);
				}
				widget.setNavigationBar(vertical, showAllSteps, position, horizontalAlign);
			}
			
			processNavigationBarAttributes(context);

		}

		private void processNavigationBarAttributes(WizardContext context)
        {
			Wizard<?> widget = context.getWidget();
	        String allowSelectStep = context.readChildProperty("allowSelectStep");
			if (!StringUtils.isEmpty(allowSelectStep))
			{
				widget.getNavigationBar().setAllowSelectStep(Boolean.parseBoolean(allowSelectStep));
			}
			String labelStyleName = context.readChildProperty("labelStyleName");
			if (!StringUtils.isEmpty(labelStyleName))
			{
				widget.getNavigationBar().setLabelStyleName(labelStyleName);
			}
			String horizontalSeparatorStyleName = context.readChildProperty("horizontalSeparatorStyleName");
			if (!StringUtils.isEmpty(horizontalSeparatorStyleName))
			{
				widget.getNavigationBar().setHorizontalSeparatorStyleName(horizontalSeparatorStyleName);
			}
			String verticalSeparatorStyleName = context.readChildProperty("verticalSeparatorStyleName");
			if (!StringUtils.isEmpty(verticalSeparatorStyleName))
			{
				widget.getNavigationBar().setVerticalSeparatorStyleName(verticalSeparatorStyleName);
			}
        }
	}
	
	@TagChildAttributes(tagName="steps")
	public static class StepsProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagChildren({
			@TagChild(WizardStepsProcessor.class)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException {}
	}

	@TagChildAttributes(maxOccurs="unbounded")
	public static class WizardStepsProcessor extends ChoiceChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetStepProcessor.class),
			@TagChild(PageStepProcessor.class)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="widget")
	public static class WidgetStepProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagChildren({
			@TagChild(WidgetProcessor.class),
			@TagChild(CommandsProcessor.class)
		})
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true")
		})
		@TagEventsDeclaration({
			@TagEventDeclaration("onEnter"),
			@TagEventDeclaration("onLeave")
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException 
		{
			assert(context.getChildElement().containsKey("id")):Crux.getMessages().screenFactoryWidgetIdRequired();
			context.stepId = context.getChildElement().getProperty("id");
			context.stepLabel = context.readChildProperty("label");
			context.stepOnEnter = context.readChildProperty("onEnter");
			context.stepOnLeave = context.readChildProperty("onLeave");
			context.enabled = context.readChildProperty("enabled");
		}
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	public static class CommandsProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagChildren({
			@TagChild(WizardCommandsProcessor.class)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	public static class WizardCommandsProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="onCommand", required=true)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException 
		{
			assert(context.getChildElement().containsKey("id")):Crux.getMessages().screenFactoryWidgetIdRequired();
			Wizard<?> widget = context.getWidget();
			String id = context.getChildElement().getProperty("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.readChildProperty("label"));
			int order = Integer.parseInt(context.readChildProperty("order"));
			
			final Event commandEvent = EvtBind.getWidgetEvent(context.getChildElement(), "onCommand");
			
			WidgetStep<?> widgetStep = widget.getWidgetStep(context.stepId);
			widgetStep.addCommand(id, label, commandEvent, order);
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				widgetStep.getCommand(id).setStyleName(styleName);
			}
			String width = context.readChildProperty("width");
			if (!StringUtils.isEmpty(width))
			{
				widgetStep.getCommand(id).setWidth(width);
			}
			String height = context.readChildProperty("height");
			if (!StringUtils.isEmpty(height))
			{
				widgetStep.getCommand(id).setHeight(height);
			}
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext> 
	{
		@Override
		public void processChildren(WizardContext context) throws InterfaceConfigException
		{
			Widget childWidget = createChildWidget(context.getChildElement());
			Wizard<?> widget = context.getWidget();
			
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.stepLabel);
			
			WidgetStep<?> widgetStep = widget.addWidgetStep(context.stepId, label, childWidget);
			
			final Event onEnterEvent = Events.getEvent("onEnter", context.stepOnEnter);
			if (onEnterEvent != null)
			{
				widgetStep.addEnterEvent(onEnterEvent);
			}
			
			final Event onLeaveEvent = Events.getEvent("onLeave", context.stepOnLeave);
			if (onLeaveEvent != null)
			{
				widgetStep.addLeaveEvent(onLeaveEvent);
			}
			if (!StringUtils.isEmpty(context.enabled))
			{
				widget.setStepEnabled(context.stepId, Boolean.parseBoolean(context.enabled));
			}
		}
	}

	@TagChildAttributes(tagName="page")
	public static class PageStepProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="url", required=true),
			@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true")
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException
		{
			assert(context.getChildElement().containsKey("id")):Crux.getMessages().screenFactoryWidgetIdRequired();
			Wizard<?> widget = context.getWidget();
			String id = context.getChildElement().getProperty("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.readChildProperty("label"));
			String url = context.readChildProperty("url");
			widget.addPageStep(id, label, url);

			String enabled = context.readChildProperty("enabled");
			if (!StringUtils.isEmpty(enabled))
			{
				widget.setStepEnabled(id, Boolean.parseBoolean(enabled));
			}
		}
	}
	
	@TagChildAttributes(tagName="controlBar", minOccurs="0")
	public static class ControlBarProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="position", type=ControlPosition.class, defaultValue="south"),
			@TagAttributeDeclaration(value="verticalAlignment", type=ControlVerticalAlign.class, defaultValue="top"),
			@TagAttributeDeclaration(value="horizontalAlignment", type=ControlHorizontalAlign.class, defaultValue="right"),
			@TagAttributeDeclaration(value="spacing", type=Integer.class),
			@TagAttributeDeclaration(value="buttonsWidth", type=String.class),
			@TagAttributeDeclaration(value="buttonsHeight", type=String.class),
			@TagAttributeDeclaration(value="buttonsStyle", type=String.class),
			@TagAttributeDeclaration(value="backLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="nextLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="cancelLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="finishLabel", type=String.class, supportsI18N=true),
			@TagAttributeDeclaration(value="backOrder", type=Integer.class, defaultValue="0"),
			@TagAttributeDeclaration(value="nextOrder", type=Integer.class, defaultValue="1"),
			@TagAttributeDeclaration(value="finishOrder", type=Integer.class, defaultValue="2"),
			@TagAttributeDeclaration(value="cancelOrder", type=Integer.class, defaultValue="3")
		})
		@TagChildren({
			@TagChild(ControlBarCommandsProcessor.class)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException 
		{
			Wizard<?> widget = context.getWidget();
			String positionAttr = context.readChildProperty("position");
			ControlPosition position = ControlPosition.south;
			if (!StringUtils.isEmpty(positionAttr))
			{
				position = ControlPosition.valueOf(positionAttr);
			}
			
			boolean vertical = position.equals(ControlPosition.east) || position.equals(ControlPosition.west);
			if (vertical)
			{
				ControlVerticalAlign verticalAlign = ControlVerticalAlign.top;
				String verticalAlignmentAttr = context.readChildProperty("verticalAlignment");
				if (!StringUtils.isEmpty(verticalAlignmentAttr))
				{
					verticalAlign = ControlVerticalAlign.valueOf(verticalAlignmentAttr);
				}
				widget.setControlBar(vertical, position, verticalAlign);
			}
			else
			{
				ControlHorizontalAlign horizontalAlign = ControlHorizontalAlign.right;
				String horizontalAlignmentAttr = context.readChildProperty("horizontalAlignment");
				if (!StringUtils.isEmpty(horizontalAlignmentAttr))
				{
					horizontalAlign = ControlHorizontalAlign.valueOf(horizontalAlignmentAttr);
				}
				widget.setControlBar(vertical, position, horizontalAlign);
			}
			WizardControlBar<?> controlBar = widget.getControlBar();
			
			processControlBarAttributes(context, controlBar);
		}

		private void processControlBarAttributes(WizardContext context, WizardControlBar<?> controlBar)
        {
	        String spacing = context.readChildProperty("spacing");
			if (!StringUtils.isEmpty(spacing))
			{
				controlBar.setSpacing(Integer.parseInt(spacing));
			}
			
			String buttonsWidth = context.readChildProperty("buttonsWidth");
			if (!StringUtils.isEmpty(buttonsWidth))
			{
				controlBar.setButtonsWidth(buttonsWidth);
			}

			String buttonsHeight = context.readChildProperty("buttonsHeight");
			if (!StringUtils.isEmpty(buttonsHeight))
			{
				controlBar.setButtonsHeight(buttonsHeight);
			}

			String buttonsStyle = context.readChildProperty("buttonsStyle");
			if (!StringUtils.isEmpty(buttonsStyle))
			{
				controlBar.setButtonsStyle(buttonsStyle);
			}

			String backLabel = context.readChildProperty("backLabel");
			if (!StringUtils.isEmpty(backLabel))
			{
				controlBar.setBackLabel(ScreenFactory.getInstance().getDeclaredMessage(backLabel));
			}

			String nextLabel = context.readChildProperty("nextLabel");
			if (!StringUtils.isEmpty(nextLabel))
			{
				controlBar.setNextLabel(ScreenFactory.getInstance().getDeclaredMessage(nextLabel));
			}

			String cancelLabel = context.readChildProperty("cancelLabel");
			if (!StringUtils.isEmpty(cancelLabel))
			{
				controlBar.setCancelLabel(ScreenFactory.getInstance().getDeclaredMessage(cancelLabel));
			}

			String finishLabel = context.readChildProperty("finishLabel");
			if (!StringUtils.isEmpty(finishLabel))
			{
				controlBar.setFinishLabel(ScreenFactory.getInstance().getDeclaredMessage(finishLabel));
			}

			String backOrder = context.readChildProperty("backOrder");
			if (!StringUtils.isEmpty(backOrder))
			{
				controlBar.setBackOrder(Integer.parseInt(backOrder));
			}

			String nextOrder = context.readChildProperty("nextOrder");
			if (!StringUtils.isEmpty(nextOrder))
			{
				controlBar.setNextOrder(Integer.parseInt(nextOrder));
			}

			String cancelOrder = context.readChildProperty("cancelOrder");
			if (!StringUtils.isEmpty(cancelOrder))
			{
				controlBar.setCancelOrder(Integer.parseInt(cancelOrder));
			}

			String finishOrder = context.readChildProperty("finishOrder");
			if (!StringUtils.isEmpty(finishOrder))
			{
				controlBar.setFinishOrder(Integer.parseInt(finishOrder));
			}
        }
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	public static class ControlBarCommandsProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagChildren({
			@TagChild(ControlBarCommandProcessor.class)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException {}
	}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	public static class ControlBarCommandProcessor extends WidgetChildProcessor<Wizard<?>, WizardContext>
	{
		@Override
		@TagAttributesDeclaration({
			@TagAttributeDeclaration(value="id", required=true),
			@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
			@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
			@TagAttributeDeclaration("styleName"),
			@TagAttributeDeclaration("width"),
			@TagAttributeDeclaration("height"),
			@TagAttributeDeclaration(value="onCommand", required=true)
		})
		public void processChildren(WizardContext context) throws InterfaceConfigException 
		{
			assert(context.getChildElement().containsKey("id")):Crux.getMessages().screenFactoryWidgetIdRequired();
			String id = context.getChildElement().getProperty("id");
			String label = ScreenFactory.getInstance().getDeclaredMessage(context.readChildProperty("label"));
			int order = Integer.parseInt(context.readChildProperty("order"));
			
			final Event commandEvent = EvtBind.getWidgetEvent(context.getChildElement(), "onCommand");
			Wizard<?> widget = context.getWidget();
			
			widget.getControlBar().addCommand(id, label, commandEvent, order);
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				widget.getControlBar().getCommand(id).setStyleName(styleName);
			}
			String width = context.readChildProperty("width");
			if (!StringUtils.isEmpty(width))
			{
				widget.getControlBar().getCommand(id).setWidth(width);
			}
			String height = context.readChildProperty("height");
			if (!StringUtils.isEmpty(height))
			{
				widget.getControlBar().getCommand(id).setHeight(height);
			}
			
		}
	}
}
