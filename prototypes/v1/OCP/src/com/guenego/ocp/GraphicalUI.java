package com.guenego.ocp;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.guenego.misc.JLG;

public class GraphicalUI implements UserInterface {

	public GraphicalUI(Agent agent) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (SystemTray.isSupported()) {
			JLG.debug("System tray is supported.");

			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();
			// load an image
			Image image = Toolkit.getDefaultToolkit().getImage(
					"images/ocp_icon.png");
			// create a action listener to listen for default action executed on
			// the tray icon
			ActionListener listener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

				}
			};
			// create a popup menu
			PopupMenu popup = new PopupMenu();
			// create menu item for the default action
			MenuItem defaultItem = new MenuItem("OCP Start");
			defaultItem.addActionListener(listener);
			popup.add(defaultItem);
			popup.add(new MenuItem("OCP Stop"));
			popup.add(new MenuItem("OCP Console"));
			// / ... add other items
			// construct a TrayIcon
			TrayIcon trayIcon = new TrayIcon(image, "OCP Agent", popup);
			// set the TrayIcon properties
			trayIcon.addActionListener(listener);
			// ...
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				JLG.error(e);
			}

		}
	}
}
