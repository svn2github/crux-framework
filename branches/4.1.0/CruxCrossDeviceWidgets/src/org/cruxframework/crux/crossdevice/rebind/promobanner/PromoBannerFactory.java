package org.cruxframework.crux.crossdevice.rebind.promobanner;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasClickHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.crossdevice.client.promobanner.PromoBanner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Factory for PromoBanner widgets
 * @author Gesse Dafe
 */
@DeclarativeFactory(id="promoBanner", library="crossDevice", targetWidget=PromoBanner.class)
@TagAttributes({
	@TagAttribute(value="bannersHeight", required=true),
	@TagAttribute(value="transitionDuration", type=Integer.class, defaultValue="150"),
	@TagAttribute(value="autoTransitionInterval", type=Integer.class, defaultValue="5000")

})
@TagChildren({
	@TagChild(PromoBannerFactory.BannerProcessor.class)
})
public class PromoBannerFactory extends WidgetCreator<WidgetCreatorContext>
{
	@TagConstraints(minOccurs="0",maxOccurs="unbounded",tagName="banner")

	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="image", required=false),
		@TagAttributeDeclaration(value="smallImage", required=false),
		@TagAttributeDeclaration(value="largeImage", required=false),
		@TagAttributeDeclaration(value="title", required=true),
		@TagAttributeDeclaration(value="text", required=true),
		@TagAttributeDeclaration("styleName"),
		@TagAttributeDeclaration(value="buttonLabel", required=true)
	})
	public static class BannerProcessor extends WidgetChildProcessor<WidgetCreatorContext>
										implements HasClickHandlersFactory<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String handler = getWidgetCreator().createVariableName("clickHandler");
			processEvent(out, context.readChildProperty("onClick"), getWidgetCreator(), handler);

			String styleName = context.readChildProperty("styleName");
			styleName = StringUtils.isEmpty(styleName) ? null : EscapeUtils.quote(styleName);

			if(!StringUtils.isEmpty(context.readChildProperty("smallImage")))
			{
				out.println(context.getWidget() + ".addSmallBanner("
						+ EscapeUtils.quote(context.readChildProperty("smallImage")));
			}
			else if(!StringUtils.isEmpty(context.readChildProperty("largeImage")))
			{
				out.println(context.getWidget() + ".addLargeBanner("
						+ EscapeUtils.quote(context.readChildProperty("largeImage")));
			}
			else if (!StringUtils.isEmpty(context.readChildProperty("image")))
			{
				out.println(context.getWidget() + ".addDefaultBanner("
						+ EscapeUtils.quote(context.readChildProperty("image")));
			}
			else
			{
				throw new CruxGeneratorException("The attribute image, smallImage or largeImage is required for Banner.");
			}

			out.println(", " + getWidgetCreator().getDeclaredMessage(context.readChildProperty("title"))
					  + ", " + getWidgetCreator().getDeclaredMessage(context.readChildProperty("text"))
					  + ", " + styleName
					  + ", " + getWidgetCreator().getDeclaredMessage(context.readChildProperty("buttonLabel"))
					  + ", " + handler
					  + ");");
		}
	}

	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }

	/**
	 * Creates the declaration of a clickHandler
	 * @param out
	 * @param clickEventAttribute
	 * @param creator
	 * @param handlerVarName
	 */
	private static void processEvent(SourcePrinter out, String clickEventAttribute, WidgetCreator<?> creator, String handlerVarName)
    {
		if(!StringUtils.isEmpty(clickEventAttribute))
		{
			out.println(ClickHandler.class.getCanonicalName() + " " + handlerVarName + " = new " + ClickHandler.class.getCanonicalName()+"(){");
			out.println("public void onClick("+ClickEvent.class.getCanonicalName()+" event){");
			EvtProcessor.printEvtCall(out, clickEventAttribute, "onClick", ClickEvent.class, "event", creator);
			out.println("}");
			out.println("};");
		}
		else
		{
			out.println(ClickHandler.class.getCanonicalName() + " " + handlerVarName + " = null;");
		}
    }

	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
