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
package br.com.sysmap.crux.gwt.rebind;

import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;

import com.google.gwt.user.client.ui.IntegerBox;

/**
 * A Factory for IntegerBox widgets
 * @author Thiago da Rosa de Bustamante
 */
@DeclarativeFactory(id="integerBox", library="gwt", targetWidget=IntegerBox.class)
@TagAttributes({
	@TagAttribute(value="value", type=Integer.class),
	@TagAttribute(value="maxLength", type=Integer.class),
	@TagAttribute(value="visibleLength", type=Integer.class)
})
public class IntegerBoxFactory extends ValueBoxBaseFactory 
{	
}
