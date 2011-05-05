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
package org.cruxframework.crux.widgets.client.decoratedbutton;

import com.google.gwt.user.client.ui.Button;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DecoratedButtonCSS3Impl extends Button implements DecoratedButtonIntf
{
	private static final String DEFAULT_STYLE_NAME = "crux-DecoratedButtonCSS3";
	
	public DecoratedButtonCSS3Impl()
    {
		super();
		setStyleName(DEFAULT_STYLE_NAME);
    }
}