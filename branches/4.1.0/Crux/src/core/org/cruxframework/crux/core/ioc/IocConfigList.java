/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.core.ioc;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class IocConfigList implements IocConfig
{
	private final Class<?> classAnnotatedWith;
	private final Class<?> classAssinableTo;
	private final String includingName;
	private final String excludingName;

	public IocConfigList(Class<?> classAssinableTo, Class<?> classAnnotatedWith, String includingName, String excludingName) 
    {
		this.classAssinableTo = classAssinableTo;
		this.classAnnotatedWith = classAnnotatedWith;
		this.includingName = includingName;
		this.excludingName = excludingName;
    }

	public boolean apliesTo(String className)
    {
	    // TODO Auto-generated method stub
	    return false;
    }

}
