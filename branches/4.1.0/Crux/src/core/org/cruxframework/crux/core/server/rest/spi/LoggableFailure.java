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
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class LoggableFailure extends RuntimeException
{
    private static final long serialVersionUID = 7544003108034286732L;
	private final int responseCode;

	public LoggableFailure(int responseCode)
    {
	    super();
		this.responseCode = responseCode;
    }

	public LoggableFailure(String arg0, Throwable arg1, int responseCode)
    {
	    super(arg0, arg1);
		this.responseCode = responseCode;
    }

	public LoggableFailure(String arg0, int responseCode)
    {
	    super(arg0);
		this.responseCode = responseCode;
    }

	public LoggableFailure(Throwable arg0, int responseCode)
    {
	    super(arg0);
		this.responseCode = responseCode;
    }

	public int getResponseCode()
    {
    	return responseCode;
    }
}