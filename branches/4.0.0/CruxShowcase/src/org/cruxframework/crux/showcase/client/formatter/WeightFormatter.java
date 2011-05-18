package org.cruxframework.crux.showcase.client.formatter;
import org.cruxframework.crux.core.client.formatter.Formatter;
import org.cruxframework.crux.core.client.formatter.InvalidFormatException;
import org.cruxframework.crux.core.client.formatter.annotation.FormatterName;

import com.google.gwt.i18n.client.NumberFormat;

@FormatterName("weight")
public class WeightFormatter implements Formatter {

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#unformat(java.lang.String)
	 */
	public Object unformat(String input) {
		
		if (input == null || !input.endsWith(" lbs.")) {
			return null;
		}

		return Integer.parseInt(input.substring(0, input.length() - 4));
	}

	/**
	 * @see org.cruxframework.crux.core.client.formatter.Formatter#format(java.lang.Object)
	 */
	public String format(Object input) throws InvalidFormatException {
		
		if (input == null) {
			return "";
		}

		if (!(input instanceof Integer)) {
			throw new InvalidFormatException();
		}

		return NumberFormat.getFormat("###").format((Integer) input)  + " lbs.";
	}
}