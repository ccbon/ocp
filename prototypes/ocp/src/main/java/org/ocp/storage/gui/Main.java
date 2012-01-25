package org.ocp.storage.gui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.ocp.ftp.FTPAgent;
import org.ocp.ftp.gui.install.FTPConfigWizard;
import org.ocp.misc.JLG;
import org.ocp.ocp.OCPAgent;
import org.ocp.ocp.UserInterface;
import org.ocp.ocp.gui.install.ConfigWizard;
import org.ocp.storage.Agent;

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
			if (agent.autoStarts()) {
				agent.loadConfig();
				agent.start();
			}
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
