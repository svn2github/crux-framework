package org.cruxframework.crux.smartgwt.rebind.rte;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartgwt.client.rte.ListPropertiesDialog;

/**
 * Factory for ListPropertiesDialog SmartGWT
 * @author Flávia Yeshua
 *
 */
@DeclarativeFactory(library="smartgwt", id="listPropertiesDialog", targetWidget=ListPropertiesDialog.class)

@TagAttributes({
	@TagAttribute("applyButtonTitle"),
	@TagAttribute("cancelButtonTitle"),
	@TagAttribute("title")
}) 

public class ListPropertiesDialogFactory extends WidgetCreator<WidgetCreatorContext>
							
{
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
