package org.ocpteam.storage.gui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.ftp.FTPAgent;
import org.ocpteam.ftp.gui.FTPConfigWizard;
import org.ocpteam.misc.JLG;
import org.ocpteam.ocp.OCPAgent;
import org.ocpteam.ocp.UserInterface;
import org.ocpteam.ocp.gui.ConfigWizard;
import org.ocpteam.sftp.SFTPAgent;
import org.ocpteam.storage.Agent;

public class Main {
	public static void main(String[] args) {
		try {
			// Agent agent = new OCPAgent();
			// Agent agent = new FTPAgent();
			Agent agent = new SFTPAgent();
			UserInterface ui = new GraphicalUI(agent);

			if (agent.requiresConfigFile()) {
				if (!agent.isConfigFilePresent()) {
					start(agent);
				}
				if (!agent.isConfigFilePresent()) {
					// it should mean that the wizard was cancelled by the user.
					return;
				}
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
