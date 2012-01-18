package com.guenego.ocp.gui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.guenego.ftp.FTPAgent;
import com.guenego.ftp.gui.install.FTPConfigWizard;
import com.guenego.misc.JLG;
import com.guenego.ocp.OCPAgent;
import com.guenego.ocp.UserInterface;
import com.guenego.ocp.gui.install.ConfigWizard;
import com.guenego.storage.Agent;

public class Main {
	public static void main(String[] args) {
		try {
			Agent agent = new OCPAgent();
			//Agent agent = new FTPAgent();
			UserInterface ui = new GraphicalUI(agent);

			if (!agent.isConfigFilePresent()) {
				start(agent);
			}
			if (!agent.isConfigFilePresent()) {
				// it should mean that the wizard was cancelled by the user.
				return;
			}

			agent.loadConfig();
			agent.start();
			(new Thread(ui)).start();
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	public static void start(Agent agent) throws Exception {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		IWizard wizard;
		if (agent.getClass() == OCPAgent.class) {
			wizard = new ConfigWizard(agent);
		} else if (agent.getClass() == FTPAgent.class) {
			wizard = new FTPConfigWizard(agent);
		} else {
			// no need wizard...
			throw new Exception("Cannot recognize the Agent class");
		}
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();

		JLG.debug("about to dispose");
		shell.dispose();
		display.dispose();
	}

}
