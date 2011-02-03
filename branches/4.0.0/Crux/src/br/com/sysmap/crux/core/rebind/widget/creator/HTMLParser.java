/**
 * 
 */
package br.com.sysmap.crux.core.rebind.widget.creator;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.rebind.widget.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.widget.ViewFactoryCreator.SourcePrinter;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class HTMLParser extends AttributeProcessor<WidgetCreatorContext>
{
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

