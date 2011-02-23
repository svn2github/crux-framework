/*
 * Copyright 2011 Sysmap Solutions Software e Consultoria Ltda.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.sysmap.crux.gwt.rebind;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.sysmap.crux.core.client.utils.EscapeUtils;
import br.com.sysmap.crux.core.client.utils.StringUtils;
import br.com.sysmap.crux.core.i18n.MessagesFactory;
import br.com.sysmap.crux.core.rebind.CruxGeneratorException;
import br.com.sysmap.crux.core.rebind.GeneratorMessages;
import br.com.sysmap.crux.core.rebind.dto.DataObjects;
import br.com.sysmap.crux.core.rebind.screen.widget.EvtProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.ViewFactoryCreator.SourcePrinter;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreator;
import br.com.sysmap.crux.core.rebind.screen.widget.WidgetCreatorContext;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.FocusableFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasDataFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.HasKeyboardPagingPolicyFactory;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttribute;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChild;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildAttributes;
import br.com.sysmap.crux.core.rebind.screen.widget.declarative.TagChildren;
import br.com.sysmap.crux.gwt.client.DateFormatUtil;
import br.com.sysmap.crux.gwt.client.NumberFormatUtil;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.ImageLoadingCell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.view.client.ProvidesKey;


/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="pageSize", type = Integer.class),
	@TagAttribute(value="pageStart", type = Integer.class),
	@TagAttribute(value="rowCount", type = Integer.class)
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="dataObject", required=true)
})
public class AbstractHasDataFactory extends WidgetCreator<WidgetCreatorContext> 
	   implements FocusableFactory<WidgetCreatorContext>, HasKeyboardPagingPolicyFactory<WidgetCreatorContext>, 
	              HasDataFactory<WidgetCreatorContext>
{
	private GeneratorMessages messages = MessagesFactory.getMessages(GeneratorMessages.class);
	
	
	@Override
    public WidgetCreatorContext instantiateContext()
    {
	    return new WidgetCreatorContext();
    }

	@Override
	public String instantiateWidget(SourcePrinter out, JSONObject metaElem, String widgetId) throws CruxGeneratorException
	{
		String varName = createVariableName("widget");
		String className = getWidgetClassName()+"<"+getDataObject(metaElem)+">";
		String keyProvider = getkeyProvider(out, metaElem);
		out.println("final "+className + " " + varName+" = new "+className+"("+keyProvider+");");
		return varName;
	}

	/**
	 * @param metaElem
	 * @return
	 */
	protected String getDataObject(JSONObject metaElem)
    {
		String dataObject = metaElem.optString("dataObject");
		String dataObjectClass = DataObjects.getDataObject(dataObject);
		if (StringUtils.isEmpty(dataObjectClass))
		{
			throw new CruxGeneratorException(messages.dataObjectNotFound(dataObject, metaElem.optString("id")));
		}
		
		return dataObjectClass;
    }

	/**
	 * @param metaElem
	 * @return
	 */
	protected String getkeyProvider(SourcePrinter out, JSONObject metaElem)
    {
		String varName = createVariableName("provider");
		String dataObject = metaElem.optString("dataObject");
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
		out.println("}"); 
		
		return varName;
    }
	
	/**
	 * @param out
	 * @param metaElem
	 * @return
	 */
	protected String getCell(SourcePrinter out, JSONObject metaElem)
	{
		JSONObject child = ensureFirstChild(metaElem, false);
		String childName = getChildName(child);
		String cell = createVariableName("cell");
		
		out.print(AbstractCell.class.getCanonicalName()+" "+cell+"=("+AbstractCell.class.getCanonicalName()+")");
		if (childName.equals("customCell"))
		{
			getCustomCell(out, child);
		}
		else if (childName.equals("buttonCell"))
		{
			out.println("new "+ButtonCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("textCell"))
		{
			out.println("new "+TextCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("clickableTextCell"))
		{
			out.println("new "+ClickableTextCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("selectionCell"))
		{
			getSelectionCell(out, child);
		}
		else if (childName.equals("textInputCell"))
		{
			out.println("new "+TextInputCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("checkboxCell"))
		{
			getCheckboxCell(out, child);
		}
		else if (childName.equals("datePickerCell"))
		{
			getDatePickerCell(out, child);
		}
		else if (childName.equals("editTextCell"))
		{
			out.println("new "+EditTextCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("actionCell"))
		{
			getActionCell(out, child);
		}
		else if (childName.equals("dateCell"))
		{
			getDateCell(out, child);
		}
		else if (childName.equals("imageCell"))
		{
			out.println("new "+ImageCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("imageLoadingCell"))
		{
			out.println("new "+ImageLoadingCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("imageResourceCell"))
		{
			out.println("new "+ImageResourceCell.class.getCanonicalName()+"();");
		}
		else if (childName.equals("numberCell"))
		{
			getNumberCell(out, metaElem);
		}
		else if (childName.equals("safeHtmlCell"))
		{
			out.println("new "+SafeHtmlCell.class.getCanonicalName()+"();");
		}
		else
		{
			out.println("null;");
		}
		
		return cell;
	}

	/**
	 * @param out
	 * @param metaElem
	 */
	protected void getNumberCell(SourcePrinter out, JSONObject metaElem)
    {
	    String numberPattern = metaElem.optString("numberPattern");
	    if (numberPattern == null || numberPattern.length() == 0)
	    {
	    	numberPattern = NumberFormatUtil.DECIMAL_PATTERN;
	    }
	    out.println("new "+NumberCell.class.getCanonicalName()+"("+NumberFormatUtil.class.getCanonicalName()+".getNumberFormat("+
	    			EscapeUtils.quote(numberPattern)+"));");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getActionCell(SourcePrinter out, JSONObject child)
    {
	    String text = child.optString("text");
	    String delegateMethod = child.optString("delegateMethod");
	    
	    if (StringUtils.isEmpty(delegateMethod) || (StringUtils.isEmpty(text)))
	    {
	    	throw new CruxGeneratorException();//TODO message
	    }
	    //TODO: pegar tipo a partir do delegatemethod
	    out.println("new "+ActionCell.class.getCanonicalName()+"<"+">("+EscapeUtils.quote(text)+
	    		", new "+Delegate.class.getCanonicalName()+"<"+">(){");
	    out.println("void execute("+" object){");
	    EvtProcessor.printEvtCall(out, delegateMethod, "loadCell", null, null, this);
	    out.println("}");
	    out.println(");");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getDatePickerCell(SourcePrinter out, JSONObject child)
    {
	    String datePattern = child.optString("datePattern");
	    if (datePattern == null || datePattern.length() == 0)
	    {
	    	datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
	    }
	    out.println("new "+DatePickerCell.class.getCanonicalName()+"("+DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
	    		EscapeUtils.quote(datePattern)+"));");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getDateCell(SourcePrinter out, JSONObject child)
    {
	    String datePattern = child.optString("datePattern");
	    if (datePattern == null || datePattern.length() == 0)
	    {
	    	datePattern = DateFormatUtil.MEDIUM_DATE_PATTERN;
	    }
	    out.println("new "+DateCell.class.getCanonicalName()+"("+DateFormatUtil.class.getCanonicalName()+".getDateTimeFormat("+
	    		EscapeUtils.quote(datePattern)+"));");
    }
	
	/**
	 * @param out
	 * @param child
	 */
	protected void getCheckboxCell(SourcePrinter out, JSONObject child)
    {
	    boolean dependsOnSelection = false;
	    String dependsOnSelectionStr = child.optString("dependsOnSelection");
	    if (StringUtils.isEmpty(dependsOnSelectionStr))
	    {
	    	dependsOnSelection = Boolean.parseBoolean(dependsOnSelectionStr);
	    }
	    boolean handlesSelection = dependsOnSelection;
	    String handlesSelectionStr = child.optString("handlesSelection");
	    if (StringUtils.isEmpty(handlesSelectionStr))
	    {
	    	handlesSelection = Boolean.parseBoolean(handlesSelectionStr);
	    }
	    
	    out.println("new "+CheckboxCell.class.getCanonicalName()+"("+dependsOnSelection+", "+handlesSelection+");");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getSelectionCell(SourcePrinter out, JSONObject child)
    {
	    String options = createVariableName("options");
	    out.println(ArrayList.class.getCanonicalName()+"<String> "+options+" = "+ArrayList.class.getCanonicalName()+"<String>();");
	    JSONArray children = ensureChildren(child, true);
	    if (children != null)
	    {
	    	for (int i=0; i< children.length(); i++)
	    	{
	    		JSONObject optionElement = children.optJSONObject(i);
	    		String textOption = ensureTextChild(optionElement, true);
	    		out.println(options+".add("+EscapeUtils.quote(textOption)+");");
	    	}
	    }
	    out.println("new "+SelectionCell.class.getCanonicalName()+"("+options+");");
    }

	/**
	 * @param out
	 * @param child
	 */
	protected void getCustomCell(SourcePrinter out, JSONObject child)
    {
	    String cellFactoryMethod = child.optString("cellFactoryMethod");
	    if (StringUtils.isEmpty(cellFactoryMethod))
	    {
	    	throw new CruxGeneratorException();//TODO message
	    }
	    EvtProcessor.printEvtCall(out, cellFactoryMethod, "loadCell", null, null, this);
    }
	
	/**
	 * @author Thiago da Rosa de Bustamante
	 *
	 */
	@TagChildAttributes(minOccurs="1", maxOccurs="1")
	@TagChildren({
		@TagChild(CustomCellProcessor.class),
		@TagChild(ButtonCellProcessor.class),
		@TagChild(TextCellProcessor.class),
		@TagChild(ClickableTextCellProcessor.class),
		@TagChild(SelectionCellProcessor.class), 
		@TagChild(TextInputCellProcessor.class),
		@TagChild(CheckboxCellProcessor.class),
		@TagChild(DatePickerCellProcessor.class),
		@TagChild(EditTextCellProcessor.class),
		@TagChild(ActionCellProcessor.class),
		@TagChild(DateCellProcessor.class),
		@TagChild(ImageCellProcessor.class),
		@TagChild(ImageLoadingCellProcessor.class),
		@TagChild(ImageResourceCellProcessor.class),
		@TagChild(NumberCellProcessor.class),
		@TagChild(SafeHtmlCellProcessor.class)
	})
	public static class CellListChildProcessor extends ChoiceChildProcessor<WidgetCreatorContext> {}	
	
	@TagChildAttributes(tagName="customCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="cellFactoryMethod", required=true)
	})
	public static class CustomCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="buttonCell")
	public static class ButtonCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="textCell")
	public static class TextCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="clickableTextCell")
	public static class ClickableTextCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="selectionCell")
	@TagChildren({
		@TagChild(ListOptionProcessor.class)
	})
	public static class SelectionCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="option", type=String.class, minOccurs="0", maxOccurs="unbounded")
	public static class ListOptionProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="textInputCell")
	public static class TextInputCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="checkboxCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="dependsOnSelection", type=Boolean.class),
		@TagAttributeDeclaration(value="handlesSelection", type=Boolean.class)
	})
	public static class CheckboxCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="datePickerCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("datePattern")
	})
	public static class DatePickerCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="editTextCell")
	public static class EditTextCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="actionCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text", required=true),
		@TagAttributeDeclaration(value="delegateMethod", required=true)
	})
	public static class ActionCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="compositeCell")
	@TagChildren({
		@TagChild(CompositeChildProcessor.class)
	})
	public static class CompositeCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="column")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="horizontalAlignment", required=true),
		@TagAttributeDeclaration(value="verticalAlignment", required=true)//TODO: e o fieldUpdater??? 
	})
	@TagChildren({
		@TagChild(CellListChildProcessor.class)
	})
	public static class CompositeChildProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagChildAttributes(tagName="dateCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("datePattern")
	})
	public static class DateCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="imageCell")
	public static class ImageCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="imageLoadingCell")
	public static class ImageLoadingCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="imageResourceCell")
	public static class ImageResourceCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
		
	@TagChildAttributes(tagName="numberCell")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration("numberPattern")
	})
	public static class NumberCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
	@TagChildAttributes(tagName="safeHtmlCell")
	public static class SafeHtmlCellProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
}
