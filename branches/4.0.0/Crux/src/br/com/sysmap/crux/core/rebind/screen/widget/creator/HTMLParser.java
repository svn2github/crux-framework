/**
 * 
 */
package br.com.sysmap.crux.core.rebind.screen.widget.creator;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.screen.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTMLParser extends AttributeProcessor<WidgetCreatorContext>
{
	public HTMLParser(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	@Override
    public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
    {
		String text = context.readWidgetProperty("text");
		if (text == null || text.length() ==0)
		{
			out.println(context.getWidget()+".setHTML("+EscapeUtils.quote(attributeValue)+");");
		}
    }
}

