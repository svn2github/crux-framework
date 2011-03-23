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
package org.cruxframework.crux.core.client.screen;

/**
 * Contains the available lazyPanel wrapping models. {@code wrapChildren} is used 
 * by widgets that needs to create some of its children lazily. {@code wrapWholeWidget}
 * is used when the whole widget must be rendered lazily, like when {@code ScreenFactory}
 * is parsing the CruxMetaData and find an invisible panel.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public enum LazyPanelWrappingType
{
	wrapChildren, wrapWholeWidget
}
