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
package br.com.sysmap.crux.gwt.client;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.declarative.TagAttribute;
import br.com.sysmap.crux.core.client.declarative.TagAttributes;
import br.com.sysmap.crux.core.client.declarative.TagEventDeclaration;
import br.com.sysmap.crux.core.client.declarative.TagEventsDeclaration;
import br.com.sysmap.crux.core.client.event.Event;
import br.com.sysmap.crux.core.client.event.Events;
import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.ScreenLoadEvent;
import br.com.sysmap.crux.core.client.screen.ScreenLoadHandler;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.ComplexPanel;

class AbstractLayoutPanelContext extends WidgetCreatorContext
{
	int animationDuration = 0;
	FastList<Command> childProcessingAnimations;

	/**
	 * @param context
	 * @param command
	 */
	protected void addChildWithAnimation(Command command)
	{
		childProcessingAnimations.add(command);
	}
}

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AbstractLayoutPanelFactory<T extends ComplexPanel, C extends AbstractLayoutPanelContext> 
			    extends ComplexPanelFactory<T, C>
{
	@Override
	@TagAttributes({
		@TagAttribute(value="animationDuration", type=Integer.class, processor=AnimationDurationAttributeParser.class)
	})
	public void processAttributes(final C context) throws InterfaceConfigException
	{
		super.processAttributes(context);
	}
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static class AnimationDurationAttributeParser implements AttributeProcessor<AbstractLayoutPanelContext>
	{
		public void processAttribute(AbstractLayoutPanelContext context, String propertyValue) 
		{
			context.animationDuration = Integer.parseInt(propertyValue);
			context.childProcessingAnimations = new FastList<Command>();			
		}
	}
	
	
	@Override
	@TagEventsDeclaration({
		@TagEventDeclaration("onAnimationComplete"), 
		@TagEventDeclaration("onAnimationStep") 
	})
	public void processEvents(C context) throws InterfaceConfigException
	{
		super.processEvents(context);
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public void postProcess(final C context) throws InterfaceConfigException
    {
		final T widget = (T)context.getWidget();
		
		if (context.animationDuration > 0)
		{
			final Event onAnimationComplete = Events.getEvent("onAnimationComplete", context.readWidgetProperty("onAnimationComplete"));
			final Event onAnimationStep = Events.getEvent("onAnimationStep", context.readWidgetProperty("onAnimationStep"));
			if (onAnimationComplete != null  || onAnimationStep != null)
			{
				final LayoutAnimationEvent<T> animationEvent = new LayoutAnimationEvent<T>(widget, context.getWidgetId());
				addScreenLoadedHandler(new ScreenLoadHandler(){
					public void onLoad(ScreenLoadEvent screenLoadEvent)
					{
						runChildProcessingAnimations(context.childProcessingAnimations);
						((AnimatedLayout)widget).animate(context.animationDuration, new AnimationCallback(){
							public void onAnimationComplete()
							{
								if (onAnimationComplete != null)
								{
									Events.callEvent(onAnimationComplete, animationEvent);
								}
							}
							public void onLayout(Layer layer, double progress)
							{
								if (onAnimationStep != null)
								{
									Events.callEvent(onAnimationStep, animationEvent);
								}
							}
						});
					}
				});
			}
			else
			{
				addScreenLoadedHandler(new ScreenLoadHandler(){
					public void onLoad(ScreenLoadEvent screenLoadEvent)
					{
						runChildProcessingAnimations(context.childProcessingAnimations);
						((AnimatedLayout)widget).animate(context.animationDuration);
					}
				});
			}
		}		
    }
	
	/**
	 * 
	 * @param childProcessingAnimations
	 */
	protected void runChildProcessingAnimations(FastList<Command> childProcessingAnimations)
	{
		for (int i=0; i<childProcessingAnimations.size(); i++)
		{
			Command command = childProcessingAnimations.get(i);
			command.execute();
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
