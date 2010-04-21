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
package br.com.sysmap.crux.widgets.client.event.step;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Thiago da Rosa de Bustamante - <code>tr_bustamante@yahoo.com.br</code>
 *
 */
public class LeaveEvent extends GwtEvent<LeaveHandler> 
{
	private static Type<LeaveHandler> TYPE = new Type<LeaveHandler>();

	private boolean canceled;

	/**
	 * 
	 */
	protected LeaveEvent ()
	{
		super();
	}

	/**
	 * @return
	 */
	public static Type<LeaveHandler> getType()
	{
		return TYPE;
	}

	/**
	 * @param <I>
	 * @param source
	 * @return
	 */
	public static LeaveEvent fire(HasLeaveHandlers source)
	{
		LeaveEvent event = new LeaveEvent();
		source.fireEvent(event);
		return event;
	}

	@Override
	protected void dispatch(LeaveHandler handler)
	{
		handler.onLeave(this);
	}

	@Override
	public Type<LeaveHandler> getAssociatedType()
	{
		return TYPE;
	}

	/**
	 * @return the canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 * 
	 */
	public void cancel()
	{
		canceled = true;
	}
}
