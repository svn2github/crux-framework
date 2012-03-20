package org.cruxframework.crux.core.utils;

import java.io.IOException;
import java.util.List;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.dto.DataObjects.Label;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.json.JSONObject;

import com.google.gwt.text.shared.Renderer;

/**
 *
 * @author daniel.martins - <code>daniel@cruxframework.org</code>
 *
 */
public class RendererUtils
{
	/**
	 *
	 * @param metaElem
	 * @return
	 */
	public static String getRenderer(SourcePrinter out, JSONObject metaElem, String dataObjectAttributeName, String rendererKeyVarName)
    {
		String varName = rendererKeyVarName;
		String dataObject = metaElem.optString(dataObjectAttributeName);
		String dataObjectClass = DataObjects.getDataObject(dataObject);
		List<Label> labels = DataObjects.getDataObjectLabels(dataObject);

		if (labels == null || labels.size() == 0)
		{
			return "(" + Renderer.class.getCanonicalName()+"<"+dataObjectClass+">)null";
		}

		out.println(Renderer.class.getCanonicalName()+"<"+dataObjectClass+"> "+varName+" = new "+
				Renderer.class.getCanonicalName()+"<"+dataObjectClass+">(){");

		out.println("public String render("+dataObjectClass+" object){");
		out.print("return (object == null) ? null :\"\"");
		for (Label label : labels)
        {
			out.print(" + object."+label.getLabelField() + " + " + EscapeUtils.quote(label.getSuffix()));
        }
		out.println(";");
		out.println("}");

		out.print("\n");

		out.println("public void render("+dataObjectClass+" object, " + Appendable.class.getCanonicalName() + " appendable) throws " + IOException.class.getCanonicalName() + "{");
		for (Label label : labels)
        {
			out.println("appendable.append(object." + label.getLabelField() + ");");
			out.println("appendable.append(" + EscapeUtils.quote(label.getSuffix()) + ");");
        }
		out.println("}");

		out.println("};");

		return varName;
    }

}
