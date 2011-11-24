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
package org.cruxframework.crux.core.client.screen;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Classes that implement this interface are rendered differently according to the client device type. 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface DeviceAdaptive extends IsWidget
{
	/**
	 * All devices supported by Crux CrossDevice engine
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static enum Device{pc, androids, androidTV, androidMobile, androidTablet, ios, iosTablet, mobiles, tablets, all}
	
	/**
	 * Used to map the all templates used by the target deviceAdaptive Widget
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static @interface Templates
	{
		Template[] value();
	}
	
	/**
	 * Used to map the template used by a specific device
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	public static @interface Template
	{
		String name();
		Device device();
	}
}
