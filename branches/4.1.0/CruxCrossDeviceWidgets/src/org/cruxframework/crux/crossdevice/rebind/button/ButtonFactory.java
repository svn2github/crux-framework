package org.cruxframework.crux.crossdevice.rebind.button;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.FocusableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAllFocusHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasHTMLFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.crossdevice.client.button.Button;
import org.cruxframework.crux.crossdevice.rebind.event.SelectEvtBind;

/**
 *
 * @author    Daniel Martins - <code>daniel@cruxframework.org</code>
 *
 */
@DeclarativeFactory(library="crossDevice", id="button", targetWidget=Button.class)

@TagEvents({
	@TagEvent(SelectEvtBind.class)
})	
public class ButtonFactory extends WidgetCreator<WidgetCreatorContext>
						implements 	HasAllFocusHandlersFactory<WidgetCreatorContext>, HasEnabledFactory<WidgetCreatorContext>,
									HasHTMLFactory<WidgetCreatorContext>, FocusableFactory<WidgetCreatorContext>
{
    @Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }
}
