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

import org.json.JSONObject;

import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.widget.declarative.DeclarativeFactory;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.widget.declarative.TagChildren;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dockLayoutPanel", library="gwt", targetWidget=DockLayoutPanel.class)
public class DockLayoutPanelFactory extends AbstractDockLayoutPanelFactory<DockLayoutPanelContext>
{
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName();
		Unit unit = getUnit(metaElem.optString("unit"));
		out.println(className + " " + varName+" = new "+className+"("+Unit.class.getCanonicalName()+"."+unit.toString()+");");
		return varName;
	}
	
	@Override
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="unit", type=Unit.class)
	})
	public void processAttributes(SourcePrinter out, DockLayoutPanelContext context) throws CruxGeneratorException
	{
		super.processAttributes(out, context);
	}
	
	@Override
	@TagChildren({
		@TagChild(DockLayoutPanelProcessor.class)
	})		
	public void processChildren(SourcePrinter out, DockLayoutPanelContext context) throws CruxGeneratorException {}
	
	public static class DockLayoutPanelProcessor extends AbstractDockLayoutPanelProcessor<DockLayoutPanelContext>
	{
		@Override
		@TagChildren({
			@TagChild(DockLayoutPanelWidgetProcessor.class)
		})		
		public void processChildren(SourcePrinter out, DockLayoutPanelContext context) throws CruxGeneratorException
		{
			super.processChildren(out, context);
		}
	}
	
	public static class DockLayoutPanelWidgetProcessor extends AbstractDockPanelWidgetProcessor<DockLayoutPanelContext> {}
}
