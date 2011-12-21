package com.guenego.ocp.gui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.UserInterface;
import com.guenego.ocp.gui.console.AdminConsole;
import com.guenego.ocp.gui.console.ExitAction;
import com.guenego.ocp.gui.console.OpenConsoleAction;

public class GraphicalUI implements UserInterface {

	private Agent agent;

	public GraphicalUI(Agent agent) {
		this.agent = agent;
	}

	@Override
	public void run() {
		JLG.debug("starting GUI");
		final Display display = Display.getDefault();
		Shell shell = new Shell(display);
		final AdminConsole window = new AdminConsole(agent, display);
		window.setBlockOnOpen(true);

		Tray tray = display.getSystemTray();
		if (tray != null) {
			TrayItem item = new TrayItem(tray, SWT.NONE);
			Image image = new Image(display, "images/ocp_icon.png");
			item.setImage(image);
			item.setToolTipText("OCP Agent");
			final MenuManager myMenu = new MenuManager("xxx");
			final Menu menu = myMenu.createContextMenu(shell);
			myMenu.add(new ExitAction(agent, display));
			myMenu.add(new OpenConsoleAction(window, display));
			menu.setEnabled(true);
			

			item.addListener(SWT.MenuDetect, new Listener() {

				@Override
				public void handleEvent(Event arg0) {
					JLG.debug("coucou");
					myMenu.setVisible(true);
					menu.setVisible(true);
				}
			});


		}
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
				//JLG.debug("sleep");
			}
		}
		display.dispose();

	}
}
