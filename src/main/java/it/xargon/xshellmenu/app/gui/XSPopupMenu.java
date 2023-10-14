package it.xargon.xshellmenu.app.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import it.xargon.xshellmenu.app.model.XSMenuItem;
import it.xargon.xshellmenu.app.res.Resources;

public class XSPopupMenu extends JFrame {
	public static enum Propagation {
		NONE,
		FORWARD,
		BACKWARD;
	}
	
	private static final long serialVersionUID = 8748457023451298892L;
	
	private static final int BORDER_RADIUS = 8;
	
	private static final long SUBMENU_OPEN_DELAY = 500;
	
	private ScheduledFuture<?> subMenuFuture;
	private Object subMenuFutureSync = new Object();
		
	private JScrollPane menuScrollPane;

	private XSPopupMenu parentMenu;
	
	private XSPopupMenu currentSubMenu;
	
	private XSMenuItem menuSource;
	
	private boolean ignoreLostFocus = false;
	
	private boolean hoveringOnItem = false;
	
	public WindowAdapter subMenuWindowEventsHandler = new WindowAdapter() {
		@Override
		public void windowClosed(WindowEvent e) {
			if (currentSubMenu != null && e.getSource() == currentSubMenu) {
				currentSubMenu = null;
			}
		}
	};

	private ComponentAdapter componentResizeHandler = new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			RoundRectangle2D.Double frameShape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);
			setShape(frameShape);
		}
	};
	
	private WindowAdapter thisMenuWindowEventsHandler = new WindowAdapter() {
		@Override
		public void windowLostFocus(WindowEvent e) {
			//this handler serves only to hide this popup and all its parents
			//if it loses focus
			//it must be ignored if this popoup have been closed externally
			//or it has the mouse "hovering" on it
			if (ignoreLostFocus)  return;
			if (currentSubMenu == null) close(Propagation.BACKWARD);
		}
	};
	
	private XSMenuItemListener menuItemListener = new XSMenuItemListener() {
		@Override
		public void mouseEntered(XSMenuItem item) {
			hoveringOnItem = true;
			if (currentSubMenu != null) {
				currentSubMenu.close(Propagation.FORWARD);
			}
			
			if (item.countChildren(XSMenuItem.PRIMARY_MENU) > 0) {
				//mustOpenSubMenu(item, XSMenuItem.PRIMARY_MENU);
				Point menuLocation = MouseInfo.getPointerInfo().getLocation();				
				scheduleDelayedOpenSubMenu(item, menuLocation);
			} else {
				cancelDelayedOpenSubMenu();
				requireAttention();
			}
		}

		@Override
		public void mouseExited(XSMenuItem item) {
			hoveringOnItem = false;
		}

		@Override
		public void mouseActionClicked(XSMenuItem item) {
			ignoreLostFocus = true;
			hoveringOnItem = false;
			Runnable action = item.getAction();
			if (action!=null) {
				close(Propagation.BACKWARD);
				action.run();
			}
		}

		@Override
		public void mouseAuxClicked(XSMenuItem item, int buttonIndex) {
			ignoreLostFocus=true;
			hoveringOnItem = false;
			if (currentSubMenu != null) {
				currentSubMenu.close(Propagation.FORWARD);
			}
			
			if (item.countChildren(buttonIndex) > 0) {
				Point menuLocation = MouseInfo.getPointerInfo().getLocation();
				mustOpenSubMenu(item, buttonIndex, menuLocation);
			}
		}		
	};
	
	private void scheduleDelayedOpenSubMenu(final XSMenuItem menuItem, final Point menuLocation) {
		synchronized (subMenuFutureSync) {
			cancelDelayedOpenSubMenu();
			subMenuFuture = Resources.internalTaskScheduler.schedule(() -> {
				SwingUtilities.invokeLater(() -> {
					mustOpenSubMenu(menuItem, XSMenuItem.PRIMARY_MENU, menuLocation);
				});
			}, SUBMENU_OPEN_DELAY, TimeUnit.MILLISECONDS);			
		}
	}
	
	private void cancelDelayedOpenSubMenu() {
		synchronized (subMenuFutureSync) {
			if (subMenuFuture != null) subMenuFuture.cancel(true);
			subMenuFuture = null;
		}		
	}
	
	private void mustOpenSubMenu(XSMenuItem menuItem, int menuId, Point menuLocation) {
		cancelDelayedOpenSubMenu();
		currentSubMenu = new XSPopupMenu(this, menuItem, menuId);
		currentSubMenu.addWindowListener(subMenuWindowEventsHandler);
		currentSubMenu.show(menuLocation.x, menuLocation.y);
	}
	
	public XSPopupMenu() {
		this(null, null, XSMenuItem.PRIMARY_MENU);
	}
	
	public XSPopupMenu(XSPopupMenu parentMenu, XSMenuItem menuSource, int menuId) {
		this.parentMenu = parentMenu;
		this.menuSource = menuSource;
		setUndecorated(true);
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		
		addComponentListener(componentResizeHandler);
		addWindowFocusListener(thisMenuWindowEventsHandler);
				
		menuScrollPane = new JScrollPane();
		menuScrollPane.setBorder(new XSRoundBorder(UIManager.getColor("Button.shadow"), 3, BORDER_RADIUS));
		getContentPane().add(menuScrollPane, BorderLayout.CENTER);
		menuScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		menuScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		XSMenuContainer menuContainer = new XSMenuContainer();
		
		int itemCount = menuSource.countChildren(menuId);
		
		for(int i=0; i<itemCount; i++) {
			XSMenuItem menuItem = menuSource.getChild(menuId, i);
			
			switch (menuItem.getType()) {
				case ITEM:
					XSMenuItemComponent menuItemComponent = new XSMenuItemComponent(menuItem, menuItemListener);
					menuContainer.addMenuComponent(menuItemComponent, (i == (itemCount-1)));
					break;
				case SEPARATOR:
					menuContainer.addSeparator();
					break;
			}
		}
		
		menuScrollPane.setViewportView(menuContainer);
	}
	
	public XSMenuItem getMenuSource() {
		return menuSource;
	}
	
	public void show(int x, int y) {
		pack();
		
		//avoid covering more than 2/3 display height, switching on vertical scrollbar if needed
		Rectangle display = getGraphicsConfiguration().getBounds();
		int maxMenuHeight = display.height / 3 * 2; //we don't need double precision
		
		Dimension menuDims = getSize();
		
		if (menuDims.height > maxMenuHeight) {		
			menuDims.setSize(menuDims.width + menuScrollPane.getVerticalScrollBar().getPreferredSize().width, maxMenuHeight);
			setPreferredSize(menuDims);
			pack();
		}		
		
		//force the menu into visible display area
		int xMenu = x;
		int yMenu = y;

		if (xMenu + menuDims.width > display.width) xMenu = xMenu - menuDims.width;
		if (yMenu + menuDims.height > display.height) {yMenu = yMenu - menuDims.height; yMenu = (yMenu < 0) ? 0 : yMenu;}
		
		setLocation(xMenu, yMenu);
		setVisible(true);
		requireAttention();
	}
	
	public void requireAttention() {
		toFront();
		setAlwaysOnTop(true);
	}

	public void close(Propagation propagate) {
		if (hoveringOnItem) return; //this submenu still needs attention
		
		ignoreLostFocus=true;
		setVisible(false);
		dispose();
		switch (propagate) {
			case BACKWARD:
				if (parentMenu != null) parentMenu.close(propagate);
				break;
			case FORWARD:
				if (currentSubMenu != null) currentSubMenu.close(propagate);
				break;
			case NONE:
				break;
		}
	}
}
