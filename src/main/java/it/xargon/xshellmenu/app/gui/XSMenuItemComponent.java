package it.xargon.xshellmenu.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import it.xargon.xshellmenu.XSMenuItem;
import it.xargon.xshellmenu.app.model.base.InMemoryMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

public class XSMenuItemComponent extends JPanel {
	private static final long serialVersionUID = 2267459892158068804L;
		
	private JLabel lblMenuItemText;
	private JLabel lblExpandIcon;
	
	private XSMenuItem menuItem;
	
	private Color hoverBorderColor = UIManager.getColor("MenuItem.selectionBackground");
	private Color itemDisabledColor = UIManager.getColor("MenuItem.disabledForeground");
	
	private Border normalBorder = new EmptyBorder(5, 5, 5, 5);
	private Border hoverBorder = new XSRoundBorder(hoverBorderColor, 1, 5);
		
	private XSMenuItemListener itemListener = null;
	
	private MouseInputAdapter mouseInputHandler = new MouseInputAdapter() {
		public void mouseEntered(MouseEvent e) {
			setBorder(hoverBorder);
			itemListener.mouseEntered(menuItem);
		}
		
		public void mouseExited(MouseEvent e) {
			setBorder(normalBorder);
			itemListener.mouseExited(menuItem);
		}
		
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1)
				itemListener.mouseActionClicked(menuItem);
			else
				itemListener.mouseAuxClicked(menuItem, e.getButton());
		}
	};
	
	public XSMenuItemComponent() {
		InMemoryMenuItem iMenuItem = new InMemoryMenuItem("Menu item text", Resources.genericIcon);
		iMenuItem.addChild("Fake child");
		this.menuItem = iMenuItem;
		this.itemListener = null;
		initGui();
	}
	
	public XSMenuItemComponent(XSMenuItem menuItem, XSMenuItemListener itemListener) {
		this.menuItem = menuItem;
		this.itemListener = itemListener;
		initGui();
	}
	
	private void iconLoadedEvent() {
		lblMenuItemText.setIcon(menuItem.getIcon(null));
		lblMenuItemText.repaint();
	}
	
	private void initGui() {
		addMouseListener(mouseInputHandler);
		
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		
		setBorder(normalBorder);
		
		boolean hasChildren = menuItem.countChildren(XSMenuItem.PRIMARY_MENU) > 0; //also checks for permissions
		
		lblMenuItemText = new JLabel(menuItem.getLabel(), menuItem.getIcon(this::iconLoadedEvent), SwingConstants.LEFT);
		lblMenuItemText.setIconTextGap(10);
		lblMenuItemText.setOpaque(false);
		if (!menuItem.isEnabled()) lblMenuItemText.setForeground(itemDisabledColor);
		add(lblMenuItemText, BorderLayout.CENTER);
		
		lblExpandIcon = new JLabel(Resources.expandIcon, SwingConstants.RIGHT);
		lblMenuItemText.setOpaque(false);
		add(lblExpandIcon, BorderLayout.EAST);
		
		setToolTipText(menuItem.getTooltip());
		
		if (hasChildren) lblExpandIcon.setVisible(true); else lblExpandIcon.setVisible(false);
	}
}
