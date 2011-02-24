package br.com.sysmap.crux.widgets.client.stackmenu;

import br.com.sysmap.crux.widgets.client.util.TextSelectionUtils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The clickable area of the menu item
 * @author Gesse Dafe
 */
class StackMenuItemCaption extends Composite
{
	private FocusPanel widget;
	private HorizontalPanel canvas;
	private SimplePanel hasSubItemsIndicator;
	private final StackMenuItem stackMenuItem;
	
	/**
	 * Package-protected constructor 
	 * @param label
	 * @param stackMenuItem
	 */
	StackMenuItemCaption(String label, StackMenuItem stackMenuItem)
	{
		this.stackMenuItem = stackMenuItem;
		canvas = new HorizontalPanel();
		canvas.setStyleName("item");
		
		createLeftBorder();
		createBody(label);
		createRightBorder();		
		
		widget = new FocusPanel(canvas);
		widget.addClickHandler(createBaseClickHandler());
		widget.addMouseOverHandler(createMouseOverHandler());
		widget.addMouseOutHandler(createMouseOutHandler());
		widget.addKeyUpHandler(createKeyUpHandler());
	
		initWidget(widget);
	}

	/**
	 * Creates the label of the item
	 * @param label
	 * @return
	 */
	private Label createBody(String label)
	{
		Label menuItemLabel = new Label(label);
		menuItemLabel.setStyleName("itemLabel");
		TextSelectionUtils.makeUnselectable(menuItemLabel.getElement());
		canvas.add(menuItemLabel);
		canvas.setCellVerticalAlignment(menuItemLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		return menuItemLabel;
	}

	/**
	 * Creates the left border of the item
	 */
	private void createLeftBorder()
	{
		Label emptyLabel = new Label(" ");
		emptyLabel.getElement().getStyle().setProperty("fontSize", "0px");
		canvas.add(emptyLabel);
		emptyLabel.getElement().getParentElement().setClassName("left-border");
	}
	
	/**
	 * Creates the right border of the item
	 */
	private void createRightBorder()
	{
		Label emptyLabel = new Label(" ");
		emptyLabel.getElement().getStyle().setProperty("fontSize", "0px");
		
		this.hasSubItemsIndicator = new SimplePanel();
		this.hasSubItemsIndicator.setStyleName("hasSubItems");
		this.hasSubItemsIndicator.add(emptyLabel);
		this.hasSubItemsIndicator.setVisible(false);
		
		canvas.add(this.hasSubItemsIndicator);
		canvas.setCellHorizontalAlignment(hasSubItemsIndicator, HasHorizontalAlignment.ALIGN_RIGHT);
		canvas.setCellVerticalAlignment(hasSubItemsIndicator, HasVerticalAlignment.ALIGN_MIDDLE);

		this.hasSubItemsIndicator.getElement().getParentElement().setClassName("right-border");
	}

	/**
	 * Changes the appearance of the item depending on its status.
	 * @param open
	 */
	void setOpen(boolean open)
	{
		if(open)
		{
			canvas.addStyleDependentName("open");
		}
		else
		{
			canvas.removeStyleDependentName("open");
		}		
	}
	
	/**
	 * Sets the text of the item
	 * @param label the label to set
	 */
	void setLabel(String label)
	{
		((Label) this.canvas.getWidget(0)).setText(label);		
	}
	
	/**
	 * Shows or hides the icon indicating child items
	 * @param show
	 */
	void showSubItensIndicator(boolean show)
	{
		hasSubItemsIndicator.setVisible(show);		
	}

	/**
	 * Changes the item's appearance when mouse is out of it  
	 * @return
	 */
	private MouseOutHandler createMouseOutHandler()
	{
		return new MouseOutHandler()
		{
			public void onMouseOut(MouseOutEvent event)
			{
				canvas.removeStyleDependentName("over");
				event.stopPropagation();
			}			
		};
	}

	/**
	 * Changes the item's appearance when mouse is over it
	 * @return
	 */
	private MouseOverHandler createMouseOverHandler()
	{
		return new MouseOverHandler()
		{
			public void onMouseOver(MouseOverEvent event)
			{
				canvas.addStyleDependentName("over");
				event.stopPropagation();
			}
		};
	}

	/**
	 * Fires the action associated with the selection of the item when user clicks it.
	 * @return
	 */
	private ClickHandler createBaseClickHandler()
	{
		return new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				fireAction(event);
			}
		};
	}
	
	/**
	 * Fires the action associated with the selection of the item when user presses enter.
	 * @return
	 */
	private KeyUpHandler createKeyUpHandler()
	{
		return new KeyUpHandler()
		{
			public void onKeyUp(KeyUpEvent event)
			{
				int keyCode = event.getNativeEvent().getKeyCode();
				if(keyCode == KeyCodes.KEY_ENTER || keyCode == ' ')
				{
					fireAction(event);					
				}
			}
		};
	}
	
	/**
	 * Cancels the native event and fires the action associated with the selection of the item
	 * @param event
	 */
	private void fireAction(DomEvent<?> event)
	{
		event.preventDefault();
		event.stopPropagation();		
		stackMenuItem.select();
	}
}