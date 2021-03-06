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

import org.cruxframework.crux.core.client.Legacy;
import org.cruxframework.crux.core.client.screen.views.BindRootView;
import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;

/**
 * A marker interface used to generate screen wrappers. These wrappers are useful 
 * to avoid reference to widgets on screen made with strings. With a wrapper, you can 
 * invoke an accessor method for each widget. 
 * 
 * @author Thiago da Rosa de Bustamante
 * @deprecated Use ScreenWidgetAccessor instead
 */
@BindRootView
@Legacy
@Deprecated
public interface ScreenWrapper extends WidgetAccessor
{

}
