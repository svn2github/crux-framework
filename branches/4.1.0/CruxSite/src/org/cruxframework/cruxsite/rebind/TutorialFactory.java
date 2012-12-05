package org.cruxframework.cruxsite.rebind;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.cruxsite.client.widget.Tutorial;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library="site", id="tutorial", targetWidget=Tutorial.class)

@TagAttributes({
	@TagAttribute(value="learnDescription", supportsI18N=true),
	@TagAttribute("estimatedTime"),
	@TagAttribute("largeImage"),
	@TagAttribute("smallImage"),
	@TagAttribute(value="tutorialUrl", required=true),
	@TagAttribute(value="tutorialTitle", supportsI18N=true),
	@TagAttribute(value="tutorialSubtitle", supportsI18N=true)
})
public class TutorialFactory extends WidgetCreator<WidgetCreatorContext>
{
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
