package org.cruxframework.cruxsite.rebind;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.crossdevice.client.event.SelectEvent;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;
import org.cruxframework.cruxsite.client.widget.SiteFace;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library="site", id="siteFace", targetWidget=SiteFace.class)

@TagChildren({
	@TagChild(SiteFaceFactory.MenuItemProcessor.class)
})
public class SiteFaceFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="menuEntry")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="crwalerUrl", required=true),
		@TagAttributeDeclaration(value="tooltip", supportsI18N=true)
	})
	@TagEventsDeclaration({
		@TagEventDeclaration("onSelect")
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String label = context.readChildProperty("label");
			String crwalerUrl = context.readChildProperty("crwalerUrl");
			String tooltip = context.readChildProperty("tooltip");
			String onSelect = context.readChildProperty("onSelect");

			out.print(context.getWidget() + ".addMenuEntry(" + getWidgetCreator().getDeclaredMessage(label)
					  + ", " + EscapeUtils.quote(crwalerUrl)
					  + ", " + getWidgetCreator().getDeclaredMessage(tooltip)
					  + ", ");
			processEvent(out, onSelect, getWidgetCreator());			
			out.println(");");
}
		
		/**
		 * Creates the declaration of a selectHandler
		 * @param out
		 * @param selectEventAttribute
		 * @param creator
		 * @param handlerVarName
		 */
		private void processEvent(SourcePrinter out, String selectEventAttribute, WidgetCreator<?> creator)
	    {
			if(!StringUtils.isEmpty(selectEventAttribute))
			{
				out.println("new " + SelectHandler.class.getCanonicalName()+"(){");
				out.println("public void onSelect("+SelectEvent.class.getCanonicalName()+" event){");
				EvtProcessor.printEvtCall(out, selectEventAttribute, "onSelect", SelectEvent.class, "event", creator);
				out.println("}");
				out.println("}");
			}
			else
			{
				out.print("null");
			}
	    }
	}
	
    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }

    @Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
