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
package br.com.sysmap.crux.gwt.rebind;

import java.util.ArrayList;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.gwt.client.LayoutAnimationEvent;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;

class AbstractLayoutPanelContext extends WidgetCreatorContext
{
	int animationDuration = 0;
	ArrayList<String> childProcessingAnimations;

	/**
	 * @param context
	 * @param command
	 */
	protected void addChildWithAnimation(String command)
	{
		childProcessingAnimations.add(command);
	}
}


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="animationDuration", type=Integer.class, processor=AbstractLayoutPanelFactory.AnimationDurationAttributeParser.class)
})
@TagEventsDeclaration({
	@TagEventDeclaration("onAnimationComplete"), 
	@TagEventDeclaration("onAnimationStep") 
})
public abstract class AbstractLayoutPanelFactory<C extends AbstractLayoutPanelContext> 
			    extends ComplexPanelFactory<C>
{
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class AnimationDurationAttributeParser extends AttributeProcessor<AbstractLayoutPanelContext>
	{
		@Override
		public void processAttribute(SourcePrinter out, AbstractLayoutPanelContext context, String attributeValue)
		{
			context.animationDuration = Integer.parseInt(attributeValue);
			context.childProcessingAnimations = new ArrayList<String>();			
		}
	}
	
	@Override
    public void postProcess(SourcePrinter out, C context) throws CruxGeneratorException
    {
    	String widget = context.getWidget();
		
		if (context.animationDuration > 0)
		{
			String onAnimationComplete =context.readWidgetProperty("onAnimationComplete");
			String onAnimationStep =context.readWidgetProperty("onAnimationStep");
			
			if (!StringUtils.isEmpty(onAnimationComplete) || !StringUtils.isEmpty(onAnimationStep))
			{
				String onAnimationCompleteEvt = createVariableName("evt");
				String onAnimationStepEvt = createVariableName("evt");
				String layoutAnimationEvent = createVariableName("evt");
				String eventClassName = LayoutAnimationEvent.class.getCanonicalName()+"<"+getWidgetClassName()+">";
				printlnPostProcessing(eventClassName+" "+layoutAnimationEvent+" = new "+eventClassName+"("+widget+", "+context.getWidgetId()+");");

				out.println("final Event "+onAnimationCompleteEvt+" = Events.getEvent("+EscapeUtils.quote("onAnimationComplete")+", "+ EscapeUtils.quote(onAnimationComplete)+");");
				out.println("final Event "+onAnimationStepEvt+" = Events.getEvent("+EscapeUtils.quote("onAnimationStep")+", "+ EscapeUtils.quote(onAnimationStep)+");");

				runChildProcessingAnimations(context.childProcessingAnimations);
				
				printlnPostProcessing(widget+".animate("+context.animationDuration+", new "+AnimationCallback.class.getCanonicalName()+"(){");
				printlnPostProcessing("public void onAnimationComplete(){");
				printlnPostProcessing("if (onAnimationComplete != null){");
				printlnPostProcessing("Events.callEvent("+onAnimationCompleteEvt+", "+layoutAnimationEvent+");");
				printlnPostProcessing("}");
				printlnPostProcessing("}");
				printlnPostProcessing("public void onLayout(Layer layer, double progress){");
				printlnPostProcessing("if (onAnimationStep != null){");
				printlnPostProcessing("Events.callEvent("+onAnimationStepEvt+", "+layoutAnimationEvent+");");
				printlnPostProcessing("}");
				printlnPostProcessing("}");
				printlnPostProcessing("});");				
			}
			else
			{
				runChildProcessingAnimations(context.childProcessingAnimations);
				printlnPostProcessing(widget+".animate("+context.animationDuration+");");
			}
		}		
    }
	
	/**
	 * 
	 * @param childProcessingAnimations
	 */
	protected void runChildProcessingAnimations(ArrayList<String> childProcessingAnimations)
	{
		for (int i=0; i<childProcessingAnimations.size(); i++)
		{
			String command = childProcessingAnimations.get(i);
			printlnPostProcessing(command);
		}
	}

	/**
	 * @param sizeUnit
	 * @return
	 */
	public static Unit getUnit(String sizeUnit)
	{
		Unit unit;
		if (!StringUtils.isEmpty(sizeUnit))
		{
			unit = Unit.valueOf(sizeUnit);
		}
		else
		{
			unit = Unit.PX;
		}
		return unit;
	}
}
