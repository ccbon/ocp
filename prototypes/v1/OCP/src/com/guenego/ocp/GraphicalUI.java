package com.guenego.ocp;

import java.awt.SystemTray;

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
		}
	}

}
