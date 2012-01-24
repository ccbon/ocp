package com.guenego.storage.gui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;

import com.guenego.misc.JLG;

public class HelpAction extends Action {

	private AdminConsole window;

	public HelpAction(AdminConsole adminConsole) {
		window = adminConsole;
		setText("&Help@F1");
		setToolTipText("Help");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(HelpAction.class.getResourceAsStream("linkto_help.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Starting help in OS default browser...");
			Program.launch(window.agent.getHelpURL());
		}

}