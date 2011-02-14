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
package br.com.sysmap.crux.widgets.rebind.wizard;

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.AllChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvent;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEvents;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.widgets.client.wizard.WidgetStep;
import br.com.sysmap.crux.widgets.client.wizard.Wizard;
import br.com.sysmap.crux.widgets.client.wizard.WizardCommandEvent;
import br.com.sysmap.crux.widgets.client.wizard.WizardCommandHandler;
import br.com.sysmap.crux.widgets.client.wizard.WizardControlBar;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlHorizontalAlign;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlPosition;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.ControlVerticalAlign;
import br.com.sysmap.crux.widgets.client.wizard.Wizard.NoData;
import br.com.sysmap.crux.widgets.rebind.event.CancelEvtBind;
import br.com.sysmap.crux.widgets.rebind.event.FinishEvtBind;

class WizardContext extends WidgetCreatorContext
{

	public String stepId;
	public String stepLabel;
	public String stepOnEnter;
	public String stepOnLeave;
	public String enabled;
	public String wizardObject;
}


/**
 * @author Thiago da Rosa de Bustamante -
 *
 */
@DeclarativeFactory(id="wizard", library="widgets", targetWidget=Wizard.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration("wizardContextObject")
})
@TagEvents({
	@TagEvent(FinishEvtBind.class),
	@TagEvent(CancelEvtBind.class)
})
@TagChildren({
	@TagChild(WizardFactory.WizardChildrenProcessor.class)
})
public class WizardFactory extends WidgetCreator<WizardContext>
{
	private static GeneratorMessages messages = (GeneratorMessages)MessagesFactory.getMessages(GeneratorMessages.class);

	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
	    String wizardContextObject = metaElem.optString("wizardContextObject");
		String className = getGenericSignature(wizardContextObject);
		out.println(className + " " + varName+" = new "+className+"("+EscapeUtils.quote(widgetId)+", "+EscapeUtils.quote(wizardContextObject)+");");
		return varName;
	}

	/**
	 * @param wizardContextObject
	 * @return
	 */
	public String getGenericSignature(String wizardContextObject)
	{
	    String className = getWidgetClassName();
	    String wizardData = WizardDataObjects.getWizardData(wizardContextObject);
	    if (StringUtils.isEmpty(wizardData))
	    {
	    	return className+"<"+NoData.class.getCanonicalName()+">";
	    }
		return className +"<"+wizardData+">";
	}

	@Override
	public void processAttributes(SourcePrinter out, WizardContext context) throws CruxGeneratorException
	{
	    super.processAttributes(out, context);
	    
	    String wizardContextObject = context.getChildElement().optString("wizardContextObject");
	    String wizardData = WizardDataObjects.getWizardData(wizardContextObject);
	    if (StringUtils.isEmpty(wizardData))
	    {
	    	context.wizardObject = NoData.class.getCanonicalName();
	    }
	    else
	    {
	    	context.wizardObject = wizardData;
	    }
	}
	
	@Override
	public void postProcess(SourcePrinter out, WizardContext context) throws CruxGeneratorException
	{
		String widget = context.getWidget();
		out.println(widget+".first();");
	}
	
	@TagChildren({
		@TagChild(NavigationBarProcessor.class),
		@TagChild(StepsProcessor.class),
		@TagChild(ControlBarProcessor.class)
	})
	public static class WizardChildrenProcessor extends AllChildProcessor<WizardContext> {}
	
	@TagChildAttributes(tagName="navigationBar", minOccurs="0")
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
	public static class NavigationBarProcessor extends WidgetChildProcessor<WizardContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException 
		{
			String widget = context.getWidget();
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
				out.println(widget+".setNavigationBar("+vertical+", "+showAllSteps+", "+ControlPosition.class.getCanonicalName()+"."+position.toString()+", "+
						ControlVerticalAlign.class.getCanonicalName()+"."+verticalAlign.toString()+");");
			}
			else
			{
				ControlHorizontalAlign horizontalAlign = ControlHorizontalAlign.left;
				String horizontalAlignmentAttr = context.readChildProperty("horizontalAlignment");
				if (!StringUtils.isEmpty(horizontalAlignmentAttr))
				{
					horizontalAlign = ControlHorizontalAlign.valueOf(horizontalAlignmentAttr);
				}
				out.println(widget+".setNavigationBar("+vertical+", "+showAllSteps+", "+ControlPosition.class.getCanonicalName()+"."+position.toString()+", "+
						ControlHorizontalAlign.class.getCanonicalName()+"."+horizontalAlign.toString()+");");
			}
			
			processNavigationBarAttributes(out, context);
		}

		private void processNavigationBarAttributes(SourcePrinter out, WizardContext context)
        {
			String widget = context.getWidget();
	        String allowSelectStep = context.readChildProperty("allowSelectStep");
			if (!StringUtils.isEmpty(allowSelectStep))
			{
				out.println(widget+".getNavigationBar().setAllowSelectStep("+Boolean.parseBoolean(allowSelectStep)+");");
			}
			String labelStyleName = context.readChildProperty("labelStyleName");
			if (!StringUtils.isEmpty(labelStyleName))
			{
				out.println(widget+".getNavigationBar().setLabelStyleName("+EscapeUtils.quote(labelStyleName)+");");
			}
			String horizontalSeparatorStyleName = context.readChildProperty("horizontalSeparatorStyleName");
			if (!StringUtils.isEmpty(horizontalSeparatorStyleName))
			{
				out.println(widget+".getNavigationBar().setHorizontalSeparatorStyleName("+EscapeUtils.quote(horizontalSeparatorStyleName)+");");
			}
			String verticalSeparatorStyleName = context.readChildProperty("verticalSeparatorStyleName");
			if (!StringUtils.isEmpty(verticalSeparatorStyleName))
			{
				out.println(widget+".getNavigationBar().setVerticalSeparatorStyleName("+EscapeUtils.quote(verticalSeparatorStyleName)+");");
			}
        }
	}
	
	@TagChildAttributes(tagName="steps")
	@TagChildren({
		@TagChild(WizardStepsProcessor.class)
	})
	public static class StepsProcessor extends WidgetChildProcessor<WizardContext> {}

	@TagChildAttributes(maxOccurs="unbounded")
	@TagChildren({
		@TagChild(WidgetStepProcessor.class),
		@TagChild(PageStepProcessor.class)
	})
	public static class WizardStepsProcessor extends ChoiceChildProcessor<WizardContext> {}
	
	@TagChildAttributes(tagName="widget")
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
	public static class WidgetStepProcessor extends WidgetChildProcessor<WizardContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException 
		{
			String id = context.readChildProperty("id");
			if (StringUtils.isEmpty(id))
			{
				throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
			}
			
			context.stepId = id;
			context.stepLabel = context.readChildProperty("label");
			context.stepOnEnter = context.readChildProperty("onEnter");
			context.stepOnLeave = context.readChildProperty("onLeave");
			context.enabled = context.readChildProperty("enabled");
		}
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	@TagChildren({
		@TagChild(WizardCommandsProcessor.class)
	})
	public static class CommandsProcessor extends WidgetChildProcessor<WizardContext> {}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration(value="onCommand", required=true)
	})
	public static class WizardCommandsProcessor extends WidgetChildProcessor<WizardContext>
	{
		WizardCommandEvtBind commandEvtBind = new WizardCommandEvtBind();
		
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException 
		{
			String id = context.readChildProperty("id");
			if (StringUtils.isEmpty(id))
			{
				throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
			}
			
			String widget = context.getWidget();
			String label = getWidgetCreator().getDeclaredMessage(context.readChildProperty("label"));
			int order = Integer.parseInt(context.readChildProperty("order"));
			
			String onCommand = context.readChildProperty("onCommand");
			
			String widgetStep = getWidgetCreator().createVariableName("widgetStep");
			
			out.println(WidgetStep.class.getCanonicalName()+"<?> "+widgetStep+" = "+widget+".getWidgetStep("+EscapeUtils.quote(context.stepId)+");");
			out.println(widgetStep+".addCommand("+EscapeUtils.quote(id)+", "+label+", new "+
					WizardCommandHandler.class.getCanonicalName()+"<"+context.wizardObject+">(){");
			out.println("public void onCommand("+WizardCommandEvent.class.getCanonicalName()+"<"+context.wizardObject+"> event){");
			
			commandEvtBind.printEvtCall(out, onCommand, "event");

			out.println("}");
			out.println("}, "+order+");");
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				out.println(widgetStep+".getCommand("+EscapeUtils.quote(id)+").setStyleName("+EscapeUtils.quote(styleName)+");");
			}
			String width = context.readChildProperty("width");
			if (!StringUtils.isEmpty(width))
			{
				out.println(widgetStep+".getCommand("+EscapeUtils.quote(id)+").setWidth("+EscapeUtils.quote(width)+");");
			}
			String height = context.readChildProperty("height");
			if (!StringUtils.isEmpty(height))
			{
				out.println(widgetStep+".getCommand("+EscapeUtils.quote(id)+").setHeight("+EscapeUtils.quote(height)+");");
			}
		}
	}
	
	@TagChildAttributes(type=AnyWidget.class)
	public static class WidgetProcessor extends WidgetChildProcessor<WizardContext> 
	{
		private EnterEvtBind enterEvtBind = new EnterEvtBind();
		private LeaveEvtBind leaveEvtBind = new LeaveEvtBind();
		
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException
		{
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement());
			String widget = context.getWidget();
			
			String label = getWidgetCreator().getDeclaredMessage(context.stepLabel);
			
			String widgetStep = getWidgetCreator().createVariableName("widgetStep");
			out.println(WidgetStep.class.getCanonicalName()+"<?> "+widgetStep+" = "+widget+".addWidgetStep("+
					EscapeUtils.quote(context.stepId)+", "+label+", "+childWidget+");");
			
			if (!StringUtils.isEmpty(context.stepOnEnter))
			{
				enterEvtBind.processEvent(out, context.stepOnEnter, widgetStep, context.stepId);
			}
			
			if (!StringUtils.isEmpty(context.stepOnLeave))
			{
				leaveEvtBind.processEvent(out, context.stepOnLeave, widgetStep, context.stepId);
			}
			if (!StringUtils.isEmpty(context.enabled))
			{
				out.println(widget+".setStepEnabled("+EscapeUtils.quote(context.stepId)+", "+Boolean.parseBoolean(context.enabled)+");");
			}
		}
	}

	@TagChildAttributes(tagName="page")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="url", required=true),
		@TagAttributeDeclaration(value="enabled", type=Boolean.class, defaultValue="true")
	})
	public static class PageStepProcessor extends WidgetChildProcessor<WizardContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException
		{
			String id = context.readChildProperty("id");
			if (StringUtils.isEmpty(id))
			{
				throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
			}
			
			String widget = context.getWidget();
			String label = getWidgetCreator().getDeclaredMessage(context.readChildProperty("label"));
			String url = context.readChildProperty("url");
			out.println(widget+".addPageStep("+EscapeUtils.quote(id)+", "+label+", "+EscapeUtils.quote(url)+");");

			String enabled = context.readChildProperty("enabled");
			if (!StringUtils.isEmpty(enabled))
			{
				out.println(widget+".setStepEnabled("+EscapeUtils.quote(id)+", "+Boolean.parseBoolean(enabled)+");");
			}
		}
	}
	
	@TagChildAttributes(tagName="controlBar", minOccurs="0")
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
	public static class ControlBarProcessor extends WidgetChildProcessor<WizardContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException 
		{
			String widget = context.getWidget();
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
				out.println(widget+".setControlBar("+vertical+", "+ControlPosition.class.getCanonicalName()+"."+position.toString()
						+", "+ControlVerticalAlign.class.getCanonicalName()+"."+verticalAlign.toString()+");");
			}
			else
			{
				ControlHorizontalAlign horizontalAlign = ControlHorizontalAlign.right;
				String horizontalAlignmentAttr = context.readChildProperty("horizontalAlignment");
				if (!StringUtils.isEmpty(horizontalAlignmentAttr))
				{
					horizontalAlign = ControlHorizontalAlign.valueOf(horizontalAlignmentAttr);
				}
				out.println(widget+".setControlBar("+vertical+", "+ControlPosition.class.getCanonicalName()+"."+position.toString()+", "+
						ControlHorizontalAlign.class.getCanonicalName()+"."+horizontalAlign.toString()+");");
			}
			
			String controlBar = getWidgetCreator().createVariableName("controlBar");
			
			out.println(WizardControlBar.class.getCanonicalName()+" "+controlBar+" = "+widget+".getControlBar();");
			
			processControlBarAttributes(out, context, controlBar);
		}

		private void processControlBarAttributes(SourcePrinter out, WizardContext context, String controlBar)
        {
	        String spacing = context.readChildProperty("spacing");
			if (!StringUtils.isEmpty(spacing))
			{
				out.println(controlBar+".setSpacing("+Integer.parseInt(spacing)+");");
			}
			
			String buttonsWidth = context.readChildProperty("buttonsWidth");
			if (!StringUtils.isEmpty(buttonsWidth))
			{
				out.println(controlBar+".setButtonsWidth("+EscapeUtils.quote(buttonsWidth)+");");
			}

			String buttonsHeight = context.readChildProperty("buttonsHeight");
			if (!StringUtils.isEmpty(buttonsHeight))
			{
				out.println(controlBar+".setButtonsHeight("+EscapeUtils.quote(buttonsHeight)+");");
			}

			String buttonsStyle = context.readChildProperty("buttonsStyle");
			if (!StringUtils.isEmpty(buttonsStyle))
			{
				out.println(controlBar+".setButtonsStyle("+EscapeUtils.quote(buttonsStyle)+");");
			}

			String backLabel = context.readChildProperty("backLabel");
			if (!StringUtils.isEmpty(backLabel))
			{
				out.println(controlBar+".setBackLabel("+getWidgetCreator().getDeclaredMessage(backLabel)+");");
			}

			String nextLabel = context.readChildProperty("nextLabel");
			if (!StringUtils.isEmpty(nextLabel))
			{
				out.println(controlBar+".setNextLabel("+EscapeUtils.quote(getWidgetCreator().getDeclaredMessage(nextLabel))+");");
			}

			String cancelLabel = context.readChildProperty("cancelLabel");
			if (!StringUtils.isEmpty(cancelLabel))
			{
				out.println(controlBar+".setCancelLabel("+EscapeUtils.quote(getWidgetCreator().getDeclaredMessage(cancelLabel))+");");
			}

			String finishLabel = context.readChildProperty("finishLabel");
			if (!StringUtils.isEmpty(finishLabel))
			{
				out.println(controlBar+".setFinishLabel("+EscapeUtils.quote(getWidgetCreator().getDeclaredMessage(finishLabel))+");");
			}

			String backOrder = context.readChildProperty("backOrder");
			if (!StringUtils.isEmpty(backOrder))
			{
				out.println(controlBar+".setBackOrder("+Integer.parseInt(backOrder)+");");
			}

			String nextOrder = context.readChildProperty("nextOrder");
			if (!StringUtils.isEmpty(nextOrder))
			{
				out.println(controlBar+".setNextOrder("+Integer.parseInt(nextOrder)+");");
			}

			String cancelOrder = context.readChildProperty("cancelOrder");
			if (!StringUtils.isEmpty(cancelOrder))
			{
				out.println(controlBar+".setCancelOrder("+Integer.parseInt(cancelOrder)+");");
			}

			String finishOrder = context.readChildProperty("finishOrder");
			if (!StringUtils.isEmpty(finishOrder))
			{
				out.println(controlBar+".setFinishOrder("+Integer.parseInt(finishOrder)+");");
			}
        }
	}
	
	@TagChildAttributes(tagName="commands", minOccurs="0")
	@TagChildren({
		@TagChild(ControlBarCommandProcessor.class)
	})
	public static class ControlBarCommandsProcessor extends WidgetChildProcessor<WizardContext> {}
	
	@TagChildAttributes(tagName="command", maxOccurs="unbounded")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", required=true),
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="order", required=true, type=Integer.class),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration("width"),
		@TagAttributeDeclaration("height"),
		@TagAttributeDeclaration(value="onCommand", required=true)
	})
	public static class ControlBarCommandProcessor extends WidgetChildProcessor<WizardContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WizardContext context) throws CruxGeneratorException 
		{
			String id = context.readChildProperty("id");
			if (StringUtils.isEmpty(id))
			{
				throw new CruxGeneratorException(messages.screenFactoryWidgetIdRequired());
			}
			
			String label = getWidgetCreator().getDeclaredMessage(context.readChildProperty("label"));
			int order = Integer.parseInt(context.readChildProperty("order"));
			
			String onCommand = context.readChildProperty("onCommand");
			String commandEvent = getWidgetCreator().createVariableName("evt");
			out.println("final Event "+commandEvent+" = Events.getEvent("+EscapeUtils.quote("onCommand")+", "+ EscapeUtils.quote(onCommand)+");");

			String widget = context.getWidget();
			
			out.println(widget+".getControlBar().addCommand("+EscapeUtils.quote(id)+", "+label+", "+commandEvent+", "+order+");");
			
			String styleName = context.readChildProperty("styleName");
			if (!StringUtils.isEmpty(styleName))
			{
				out.println(widget+".getControlBar().getCommand("+EscapeUtils.quote(id)+").setStyleName("+EscapeUtils.quote(styleName)+");");
			}
			String width = context.readChildProperty("width");
			if (!StringUtils.isEmpty(width))
			{
				out.println(widget+".getControlBar().getCommand("+EscapeUtils.quote(id)+").setWidth("+EscapeUtils.quote(width)+");");
			}
			String height = context.readChildProperty("height");
			if (!StringUtils.isEmpty(height))
			{
				out.println(widget+".getControlBar().getCommand("+EscapeUtils.quote(id)+").setHeight("+EscapeUtils.quote(height)+");");
			}
		}
	}

	@Override
    public WizardContext instantiateContext()
    {
	    return new WizardContext();
    }
}
