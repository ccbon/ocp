package org.ocpteam.ui.swt;

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
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.AgentConfig;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ftp.FTPAgent;
import org.ocpteam.protocol.ocp.OCPAgent;
import org.ocpteam.protocol.ocp.UserInterface;
import org.ocpteam.protocol.sftp.SFTPAgent;
import org.ocpteam.protocol.zip.ZipAgent;

public class Main {
	public static final String SWT_ASSISTANT = "swt";

	public static void main(String[] args) {
		try {
			String agentClass = getAgentClass();
			Agent agent = (Agent) Class.forName(agentClass).newInstance();
			agent.setAssistant(SWT_ASSISTANT, SWTAgentAssistant.getInstance(agent));
			UserInterface ui = new GraphicalUI(agent);
			AgentConfig cfg = AgentConfig.newInstance(agent);
			agent.setConfig(cfg);
			if (cfg.requiresConfigFile()) {
				if (!cfg.isConfigFilePresent()) {
					startWizard(cfg);
				}
				if (!cfg.isConfigFilePresent()) {
					// it should mean that the wizard was cancelled by the user.
					return;
				}
				cfg.loadConfigFile();
			}
			agent.readConfig();
			
			if (agent.autoConnect()) {
				agent.connect();
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
		map.put("ZIP", ZipAgent.class.getName());
		
		String result = null;
		Properties p = new Properties();
		File file = new File("protocol.properties");
		if (!file.exists()) {
			chooseProtocolWizard(map);
		}
		if (!file.exists()) {
			System.exit(0);
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

	public static void startWizard(AgentConfig cfg) throws Exception {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		SWTAgentAssistant a = (SWTAgentAssistant) cfg.agent.getAssistant(Main.SWT_ASSISTANT);
		IWizard wizard = a.getConfigWizardInstance();
		a.startWizard(display, wizard);

		JLG.debug("about to dispose");
		display.dispose();
	}

}
