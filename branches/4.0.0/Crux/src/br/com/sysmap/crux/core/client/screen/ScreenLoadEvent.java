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


import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class ScreenLoadEvent extends GwtEvent<ScreenLoadHandler>
{
	protected static final Type<ScreenLoadHandler> TYPE = new Type<ScreenLoadHandler>();

	
	protected ScreenLoadEvent()
	{
	}
	
	@Override
	protected void dispatch(ScreenLoadHandler handler)
	{
		handler.onLoad(this);
	}

	@Override
	public Type<ScreenLoadHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static <T> void fire(ScreenFactory source) 
	{
		source.fireEvent(new ScreenLoadEvent());
	}	
}
