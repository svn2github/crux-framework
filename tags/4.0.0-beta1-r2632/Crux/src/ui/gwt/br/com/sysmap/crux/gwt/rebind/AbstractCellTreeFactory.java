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
import br.com.sysmap.crux.core.rebind.screen.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasKeyboardSelectionPolicyFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasOpenHandlersFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;

import com.google.gwt.view.client.TreeViewModel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="treeViewModelFactoryMethod", required=true),
	@TagAttributeDeclaration(value="getValueMethod", required=true)
})
public class AbstractCellTreeFactory extends WidgetCreator<WidgetCreatorContext> implements 
							HasCloseHandlersFactory<WidgetCreatorContext>, HasOpenHandlersFactory<WidgetCreatorContext>, 
							HasKeyboardSelectionPolicyFactory<WidgetCreatorContext>
{
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		String model = getViewModel(out, context.getWidgetElement());
		String value = getValue(out, context.getWidgetElement());
		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+model+", "+value+");");
	}
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }	
	
	protected String getViewModel(SourcePrinter out, JSONObject metaElem)
    {
		String viewModelFactorymethod = metaElem.optString("treeViewModelFactoryMethod");
		String viewModel = createVariableName("viewModel");
		assert (!StringUtils.isEmpty(viewModelFactorymethod));
		out.print(TreeViewModel.class.getCanonicalName()+" "+ viewModel + " = ("+TreeViewModel.class.getCanonicalName()+")");
		EvtProcessor.printEvtCall(out, viewModelFactorymethod, "loadTreeViewModel", (String)null, null, this);
		return viewModel;
    }

	protected String getValue(SourcePrinter out, JSONObject metaElem)
    {
		String getValuemethod = metaElem.optString("getValueMethod");
		String value = createVariableName("value");
		assert (!StringUtils.isEmpty(getValuemethod));
		out.print("Object "+ value + " = ");
		EvtProcessor.printEvtCall(out, getValuemethod, "loadValue", (String)null, null, this);
		return value;
    }
}
