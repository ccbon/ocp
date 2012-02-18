package org.ocpteam.rsp.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import org.ocpteam.rsp.Agent;
import org.ocpteam.sftp.SFTPAgent;

public class Main {
	public static void main(String[] args) {
		try {
			String agentClass = getAgentClass();
			Agent agent = (Agent) Class.forName(agentClass).newInstance();
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
				if (agent.requiresConfigFile()) {
					agent.loadConfig();
				}
				agent.start();
			}
			(new Thread(ui)).start();
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	private static String getAgentClass() throws Exception {
		// if protocol.properties file exists,
		// load it and read agent class
		// else starts a wizard.

		Map<String, String> map = new HashMap<String, String>();
		map.put("OCP", OCPAgent.class.getName());
		map.put("FTP", FTPAgent.class.getName());
		map.put("SFTP", SFTPAgent.class.getName());
		
		String result = null;
		Properties p = new Properties();
		File file = new File("protocol.properties");
		if (!file.exists()) {
			chooseProtocolWizard(map);
		}
		InputStream fis = new FileInputStream(file);
		p.load(fis);
		fis.close();
		result = p.getProperty("agent.class");
		return result;
	}

	private static void chooseProtocolWizard(Map<String, String> map) {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		IWizard wizard = new ChooseProtocolWizard(map);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();

		JLG.debug("about to dispose");
		shell.dispose();
		display.dispose();
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
