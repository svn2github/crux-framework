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
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.crossdevice.client.event.SelectEvent;
import org.cruxframework.crux.crossdevice.client.event.SelectHandler;
import org.cruxframework.crux.crossdevice.client.promobanner.PromoBanner;

/**
 * Factory for PromoBanner widgets
 * @author Gesse Dafe
 */
@DeclarativeFactory(id="promoBanner", library="crossDevice", targetWidget=PromoBanner.class)
@TagAttributes({
	@TagAttribute(value="largeBannersHeight", required=true),
	@TagAttribute(value="smallBannersHeight", required=true),
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
	@TagEventsDeclaration({
		@TagEventDeclaration("onSelect")
	})
	public static class BannerProcessor extends WidgetChildProcessor<WidgetCreatorContext>
										implements HasClickHandlersFactory<WidgetCreatorContext>
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String handler = getWidgetCreator().createVariableName("selectHandler");
			processEvent(out, context.readChildProperty("onSelect"), getWidgetCreator(), handler);

			String styleName = context.readChildProperty("styleName");
			styleName = StringUtils.isEmpty(styleName) ? null : EscapeUtils.quote(styleName);

			boolean hasSmallImage = !StringUtils.isEmpty(context.readChildProperty("smallImage"));
			boolean hasLargeImage = !StringUtils.isEmpty(context.readChildProperty("largeImage"));
			boolean hasDefaultImage = !StringUtils.isEmpty(context.readChildProperty("image"));

			if(hasSmallImage)
			{
				out.println(context.getWidget() + ".addSmallBanner("
						+ EscapeUtils.quote(context.readChildProperty("smallImage")));
			}
			if(hasLargeImage)
			{
				out.println(context.getWidget() + ".addLargeBanner("
						+ EscapeUtils.quote(context.readChildProperty("largeImage")));
			}
			if (hasDefaultImage)
			{
				out.println(context.getWidget() + ".addDefaultBanner("
						+ EscapeUtils.quote(context.readChildProperty("image")));
			}
			if ((hasDefaultImage && (hasLargeImage || hasSmallImage)) || (hasLargeImage && !hasSmallImage) 
				|| (hasSmallImage && !hasLargeImage) || (hasSmallImage && hasLargeImage && hasDefaultImage))
			{
				throw new CruxGeneratorException("You must inform a small image and a large image, or a defaultImage");
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
	 * Creates the declaration of a selectHandler
	 * @param out
	 * @param selectEventAttribute
	 * @param creator
	 * @param handlerVarName
	 */
	private static void processEvent(SourcePrinter out, String selectEventAttribute, WidgetCreator<?> creator, String handlerVarName)
    {
		if(!StringUtils.isEmpty(selectEventAttribute))
		{
			out.println(SelectHandler.class.getCanonicalName() + " " + handlerVarName + " = new " + SelectHandler.class.getCanonicalName()+"(){");
			out.println("public void onSelect("+SelectEvent.class.getCanonicalName()+" event){");
			EvtProcessor.printEvtCall(out, selectEventAttribute, "onSelect", SelectEvent.class, "event", creator);
			out.println("}");
			out.println("};");
		}
		else
		{
			out.println(SelectHandler.class.getCanonicalName() + " " + handlerVarName + " = null;");
		}
    }
}
