/*
 * Copyright 2011 cruxframework.org
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
package org.cruxframework.crux.core.server.rest.spi;

/**
 * Thrown when HTTP Not Acceptable (406) is encountered
 */
public class NotAcceptableException extends LoggableFailure
{
	private static final long serialVersionUID = -8466746321969478729L;

	public NotAcceptableException(String s)
	{
		super(s, 406);
	}

	public NotAcceptableException(String s, Throwable throwable)
	{
		super(s, throwable, 406);
	}

	public NotAcceptableException(Throwable throwable)
	{
		super(throwable, 406);
	}
}