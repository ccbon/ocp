package org.ocpteam.ui.swt;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class NewDSPDataSourceScenario implements IScenario {

	private DataSourceWindow w;

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;
	}

	@Override
	public void run() throws Exception {
		JLG.debug("Starting new DataSource senario");
		Shell shell = new Shell(w.getShell().getDisplay());
		// Create the preference manager
		PreferenceManager prefManager = new PreferenceManager();

		// Create the nodes
		PreferenceNode tcpServer = new PreferenceNode("TCP Server",
				"TCP Server", null, ConfigPrefPage.class.getName());
		PreferenceNode network = new PreferenceNode("Network",
				"Network", null, NetworkPrefPage.class.getName());

		// Add the nodes
		prefManager.addToRoot(tcpServer);
		prefManager.addToRoot(network);

		// Create the preferences dialog
		PreferenceDialog prefDialog = new PreferenceDialog(shell, prefManager);

		// Set the preference store
		MyPreferenceStore ps = w.ps;
		try {
			ps.load();
		} catch (IOException e) {
			// Ignore
		}
		prefDialog.setPreferenceStore(ps);

		// Open the dialog
		prefDialog.open();

//		w.ds.setProperty("datastore.uri", ps.getString(URI));
	}
}
