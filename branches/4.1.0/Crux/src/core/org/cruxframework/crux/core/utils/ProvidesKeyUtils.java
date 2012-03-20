package org.cruxframework.crux.core.utils;

import org.cruxframework.crux.core.rebind.dto.DataObjects;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import org.json.JSONObject;

import com.google.gwt.view.client.ProvidesKey;

/**
 *
 * @author daniel.martins - <code>daniel@cruxframework.org</code>
 *
 */

public class ProvidesKeyUtils
{
	/**
	 *
	 * @param metaElem
	 * @return
	 */
	public static String getkeyProvider(SourcePrinter out, JSONObject metaElem, String dataObjectAttributeName, String providesKeyVarName)
    {
		String varName = providesKeyVarName;
		String dataObject = metaElem.optString(dataObjectAttributeName);
		String dataObjectClass = DataObjects.getDataObject(dataObject);
		String[] dataObjectIds = DataObjects.getDataObjectIdentifiers(dataObject);
		if (dataObjectIds == null || dataObjectIds.length == 0)
		{
			return "("+ProvidesKey.class.getCanonicalName()+"<"+dataObjectClass+">)null";
		}

		out.println(ProvidesKey.class.getCanonicalName()+"<"+dataObjectClass+"> "+varName+"=new "+
				ProvidesKey.class.getCanonicalName()+"<"+dataObjectClass+">(){");

		out.println("public Object getKey("+dataObjectClass+" item){");
		out.print("return (item == null) ? null :\"\"");
		for (String id : dataObjectIds)
        {
			out.print("+item."+id);
        }
		out.println(";");

		out.println("}");
		out.println("};");

		return varName;
    }

}
