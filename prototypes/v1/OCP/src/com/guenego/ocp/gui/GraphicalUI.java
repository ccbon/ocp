package com.guenego.ocp.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.UserInterface;

public class GraphicalUI implements UserInterface {

	private Agent agent;

	public GraphicalUI(Agent agent) {
		this.agent = agent;
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

//			MouseListener mouseListener = new MouseListener() {
//
//				public void mouseClicked(MouseEvent e) {
//					JLG.debug("Tray Icon - Mouse clicked!");
//				}
//
//				public void mouseEntered(MouseEvent e) {
//					JLG.debug("Tray Icon - Mouse entered!");
//				}
//
//				public void mouseExited(MouseEvent e) {
//					JLG.debug("Tray Icon - Mouse exited!");
//				}
//
//				public void mousePressed(MouseEvent e) {
//					JLG.debug("Tray Icon - Mouse pressed!");
//				}
//
//				public void mouseReleased(MouseEvent e) {
//					JLG.debug("Tray Icon - Mouse released!");
//				}
//			};


			PopupMenu popup = new PopupMenu();

			MenuItem exitItem = new MenuItem("Exit");
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JLG.debug("Exiting...");
					agent.stop();
					System.exit(0);
				}
			};
			exitItem.addActionListener(exitListener);
			popup.add(exitItem);

			MenuItem configItem = new MenuItem("Config");
			ActionListener configListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JLG.debug("Configuring");
					//ConfigFrame configFrame = new ConfigFrame(agent);
				}
			};
			configItem.addActionListener(configListener);
			popup.add(configItem);

			final TrayIcon trayIcon = new TrayIcon(image, "OCP Agent", popup);

//			ActionListener actionListener = new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					JLG.debug("try to display a message");
//					trayIcon.displayMessage("Action Event",
//							"An Action Event Has Been Performed!",
//							TrayIcon.MessageType.INFO);
//				}
//			};

			trayIcon.setImageAutoSize(true);
			//trayIcon.addActionListener(actionListener);
			//trayIcon.addMouseListener(mouseListener);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				JLG.error(e);
			}

		}
	}
}
