package org.ocpteam.ui.swt;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.misc.JLG;
import org.ocpteam.ocp.UserInterface;


public class GraphicalUI implements UserInterface {

	private Agent agent;
	private Display display;
	

	public GraphicalUI(Agent agent) {
		this.agent = agent;
		
	}

	@Override
	public void run() {
		try {
			JLG.debug("starting GUI");
			final Display display = new Display();
			this.display = display;
			Shell shell = new Shell(display);
			final AdminConsole window = new AdminConsole(agent, display);
			window.setBlockOnOpen(true);

			Tray tray = display.getSystemTray();
			if (tray != null) {
				TrayItem item = new TrayItem(tray, SWT.NONE);
				Image image = new Image(display,
						GraphicalUI.class.getResourceAsStream("ocp_icon.png"));
				item.setImage(image);
				item.setToolTipText("OCP Agent");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu.createContextMenu(shell);
				myMenu.add(new ExitAction(agent, display, window));
				myMenu.add(new OpenConsoleAction(window));
				menu.setEnabled(true);

				item.addListener(SWT.MenuDetect, new Listener() {

					@Override
					public void handleEvent(Event arg0) {
						JLG.debug("coucou");
						myMenu.setVisible(true);
						menu.setVisible(true);
					}
				});
				
				item.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent arg0) {
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						JLG.debug("item default selected");
						new OpenConsoleAction(window).run();
					}
				});

			}
			// if you want to open the window when starting...
			window.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
					// JLG.debug("sleep");
				}
			}
			display.dispose();
		} catch (Exception e) {
			JLG.error(e);
			System.exit(1);
		}
	}

	@Override
	public void stop() {
		if (!display.isDisposed()) {
			display.dispose();
		}
	}
}
