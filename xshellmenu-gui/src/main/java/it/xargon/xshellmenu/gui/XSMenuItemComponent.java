package it.xargon.xshellmenu.gui;

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

import it.xargon.xshellmenu.api.XSMenuItem;
import it.xargon.xshellmenu.api.XSPlatformResource;
import it.xargon.xshellmenu.api.base.InMemoryMenuItem;
import it.xargon.xshellmenu.res.Resources;

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
			itemListener.mouseActionClicked(menuItem, e.getButton());
		}
	};
	
	public XSMenuItemComponent() {
		InMemoryMenuItem iMenuItem = new InMemoryMenuItem("Menu item text", Resources.genericIcon);
		iMenuItem.addChild("Fake child", Resources.genericIcon);
		this.menuItem = iMenuItem;
		this.itemListener = null;
		initGui();
	}
	
	public XSMenuItemComponent(XSMenuItem menuItem, XSMenuItemListener itemListener) {
		this.menuItem = menuItem;
		this.itemListener = itemListener;
		initGui();
	}
		
	private void initGui() {
		addMouseListener(mouseInputHandler);
		
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		
		setBorder(normalBorder);
		
		boolean hasChildren = menuItem.countChildren(XSMenuItem.MenuType.PRIMARY) > 0; //also checks for permissions
		
		lblMenuItemText = new JLabel(menuItem.getLabel(), Resources.xsGuiPlatform.getPlatformResource(XSPlatformResource.GENERIC_ICON), SwingConstants.LEFT);
		
		Resources.xsGuiPlatform.getPlatformResource(XSPlatformResource.ICONFETCHER_SCHEDULER).execute(() -> {
			lblMenuItemText.setIcon(menuItem.getIcon());
			lblMenuItemText.repaint(); //will show real icon when it's ready
		});
		
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
