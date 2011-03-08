/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
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

import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="simplePager", library="gwt", targetWidget=SimplePager.class)
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="textlocation", type=TextLocation.class),
	@TagAttributeDeclaration(value="page", type=Integer.class),
	@TagAttributeDeclaration(value="pageStart", type=Integer.class)
})
public class SimplePagerFactory extends AbstractPagerFactory  
{
	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		
		String textLocationStr = metaElem.optString("textlocation");
		TextLocation textLocation = TextLocation.CENTER;
		if (!StringUtils.isEmpty(textLocationStr))
		{
			textLocation = TextLocation.valueOf(textLocationStr);
		}
		out.println("final "+className + " " + varName+" = new "+className+"("+TextLocation.class.getCanonicalName()+"."+textLocation.toString()+");");
		return varName;
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}

