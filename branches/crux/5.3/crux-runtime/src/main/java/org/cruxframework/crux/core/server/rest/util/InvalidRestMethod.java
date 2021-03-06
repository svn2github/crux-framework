/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.server.rest.util;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class InvalidRestMethod extends Exception
{
    private static final long serialVersionUID = 4639813337483501704L;

	public InvalidRestMethod()
    {
	    super();
    }

	public InvalidRestMethod(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public InvalidRestMethod(String message)
    {
	    super(message);
    }

	public InvalidRestMethod(Throwable cause)
    {
	    super(cause);
    }
}
