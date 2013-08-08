package org.cruxframework.crossdevice.showcase.rebind;

import org.cruxframework.crossdevice.showcase.client.widget.MenuDisplay;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library="showcase", id="menuDisplay", targetWidget=MenuDisplay.class)

@TagChildren({
	@TagChild(MenuDisplayFactory.MenuItemProcessor.class)
})
public class MenuDisplayFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="menuEntry")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="label", required=true, supportsI18N=true),
		@TagAttributeDeclaration(value="targetView", required=true)
	})
	public static class MenuItemProcessor extends WidgetChildProcessor<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String label = context.readChildProperty("label");
			String targetView = context.readChildProperty("targetView");
			out.print(context.getWidget() + ".addMenuEntry(" + getWidgetCreator().getDeclaredMessage(label) + ", " + EscapeUtils.quote(targetView));
			out.println(");");
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
