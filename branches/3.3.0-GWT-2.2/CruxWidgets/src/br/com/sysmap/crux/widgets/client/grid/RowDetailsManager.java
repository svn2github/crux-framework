package br.com.sysmap.crux.widgets.client.grid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.sysmap.crux.core.client.collection.FastList;
import br.com.sysmap.crux.core.client.datasource.DataSourceRecord;
import br.com.sysmap.crux.core.client.screen.InterfaceConfigException;
import br.com.sysmap.crux.core.client.screen.Screen;
import br.com.sysmap.crux.core.client.screen.ScreenFactory;
import br.com.sysmap.crux.widgets.client.WidgetMsgFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.Widget;

/**
 * Manages the logic for create, index and destroy the widgets
 * 	used as row's details.
 * 
 * @author Gesse Dafe
 */
public class RowDetailsManager 
{
	private static long nextRowDetailId = 0;
	private Map<DataSourceRecord<?>, Boolean> loadedDetails;
	private FastList<String> detailWidgetsIds;
	private final RowDetailsDefinition rowDetailsDefinition;
	
	/**
	 * @param rowDetailsDefinition
	 */
	public RowDetailsManager(RowDetailsDefinition rowDetailsDefinition) 
	{
		this.rowDetailsDefinition = rowDetailsDefinition;
		this.detailWidgetsIds = new FastList<String>();
		this.loadedDetails = new HashMap<DataSourceRecord<?>, Boolean>();
	}
	
	/**
	 * Clears the index of loaded details and destroys the related widgets.
	 */
	public void reset()
	{
		this.loadedDetails.clear();
		clearRendering();
	}
	
	/**
	 * Destroys the details widgets
	 */
	public void clearRendering()
	{
		for(int i = 0; i < this.detailWidgetsIds.size(); i++)
		{
			String id = this.detailWidgetsIds.get(i);
			if(Screen.get(id) != null)
			{
				Screen.remove(id);
			}
		}
	}
	
	/**
	 * Creates the widget to be used as a row detail
	 * @param widgetsByOriginalId A map that will be populated with the widgets declared for the current RowDetailsPanel.
	 * @return
	 */
	public Widget createWidget(Map<String, Widget> widgetsByOriginalId)
	{
		try
		{
			Element clone = (Element) rowDetailsDefinition.getDetailTemplate().cloneNode(true);
			String baseId = generateWidgetIdSufix(); 
			incrementIds(clone, baseId);
			ScreenFactory factory = ScreenFactory.getInstance();
			Widget createdWidget = factory.newWidget(clone, clone.getId(), factory.getMetaElementType(clone), true);
			
			for(int i = 0; i < this.detailWidgetsIds.size(); i++)
			{
				String id = this.detailWidgetsIds.get(i);
				if(id.endsWith(baseId))
				{
					String originalId = id.substring(0, id.indexOf(baseId));
					widgetsByOriginalId.put(originalId, Screen.get(id));
				}
			}
			
			return createdWidget;
		}
		catch (InterfaceConfigException e)
		{
			GWT.log(e.getMessage(), e);
			throw new RuntimeException(WidgetMsgFactory.getMessages().errorCreatingWidgetForRowDetails());
		}
	}

	/**
	 * Generates and sets a random ID on the given element and its children.
	 * @param template
	 * @param baseId 
	 */
	private void incrementIds(Element template, String baseId)
	{
		String newId = template.getId() + baseId;
		this.detailWidgetsIds.add(newId);
		
		template.setId(newId);
		NodeList<Node> children = template.getChildNodes();
		if(children != null)
		{
			int length = children.getLength();
			for(int i = 0; i < length; i++)
			{
				Node childNode = children.getItem(i);
				if(Element.is(childNode))
				{
					Element childElement = Element.as(childNode);
					incrementIds(childElement, baseId);
				}
			}
		}
	}
	
	/**
	 * Creates an incremental suffix to avoid ID collisions.
	 * @return
	 */
	private String generateWidgetIdSufix()
	{
		if(nextRowDetailId == 0)
		{
			nextRowDetailId = new Date().getTime();
		}
		
		return "_#rowDetail#_" + (++nextRowDetailId) + "_#";
	}
	
	/**
	 * @return <code>true</code> if the details related to this record have already being loaded.
	 */
	public boolean isDetailLoaded(DataSourceRecord<?> record)
	{
		return this.loadedDetails.get(record) != null;
	}

	/**
	 * Marks the details of the given record as loaded.
	 * 
	 * @param record
	 */
	void setDetailLoaded(DataSourceRecord<?> record) 
	{
		this.loadedDetails.put(record, Boolean.TRUE);		
	}
}
