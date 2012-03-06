package org.ocpteam.ui.swt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.UserInterface;
import org.ocpteam.misc.JLG;

public class Main {
	public static final String SWT_ASSISTANT = "swt";
	public static final File DEFAULT_FILE = new File("default.uri");

	public static void main(String[] args) {
		try {
			JLG.debug_on();
			JLG.debug("args.length=" + args.length);
			DataSource ds = null;
			if (args.length > 0) {
				ds = DataSource.getInstance(new File(args[0]));
			} else {
				if (DEFAULT_FILE.exists() && DEFAULT_FILE.isFile()) {
					ds = DataSource.getInstance(DEFAULT_FILE);
				} else {
					ds = getDataSourceFromWizard();
				}
			}

			Agent agent = ds.getAgent();
			agent.setAssistant(SWT_ASSISTANT,
					SWTAgentAssistant.getInstance(agent));
			UserInterface ui = new GraphicalUI(agent);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static DataSource getDataSourceFromWizard() throws Exception {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		DataSource ds = DataSource.getInstance(null);
		IWizard wizard = new DefineDataSourceWizard(ds);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();

		JLG.debug("about to dispose");
		shell.dispose();
		display.dispose();
		return ds;
	}

	public static void main2(String[] args) {
		try {
			String agentClass = getAgentClass();
			Agent agent = (Agent) Class.forName(agentClass).newInstance();
			agent.setAssistant(SWT_ASSISTANT,
					SWTAgentAssistant.getInstance(agent));
			UserInterface ui = new GraphicalUI(agent);
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

		String result = null;
		Properties p = new Properties();
		File file = new File("protocol.properties");
		if (!file.exists()) {
			chooseProtocolWizard();
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

	private static void chooseProtocolWizard() {
		JLG.debug_on();
		JLG.debug("starting wizard");
		Display display = Display.getDefault();

		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		IWizard wizard = new ChooseProtocolWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();

		JLG.debug("about to dispose");
		shell.dispose();
		display.dispose();
	}


}
