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
package br.com.sysmap.crux.core.client.event;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class EventException extends RuntimeException
{
	private static final long serialVersionUID = -1494786840509633213L;

	public EventException()
	{
	}

	/**
	 * @param message
	 */
	public EventException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public EventException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EventException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
