package org.ocpteamx.protocol.dht6.swt;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.IScenario;

public class ConfigPreferenceScenario implements IScenario {
	public static final String URI = "uri";
	private DataSourceWindow w;
	private boolean bSucceeded;

	public ConfigPreferenceScenario() {
	}

	@Override
	public void setWindow(DataSourceWindow w) {
		this.w = w;
	}

	@Override
	public void run() throws Exception {
		JLG.debug("Starting DHT6 new DataSource senario");
		Shell shell = new Shell(w.getShell().getDisplay());
		// Create the preference manager
		PreferenceManager prefManager = new PreferenceManager();

		// Create the nodes
		PreferenceNode one = new PreferenceNode("uri", "URI", null,
				URIPrefPage.class.getName());

		// Add the nodes
		prefManager.addToRoot(one);

		// Create the preferences dialog
		PreferenceDialog prefDialog = new PreferenceDialog(shell, prefManager);

		// Set the preference store
		PreferenceStore ps = new PreferenceStore();
		ps.setDefault(URI, "ftp://k:k@localhost:21/datastore");
		try {
			ps.load();
		} catch (IOException e) {
			// Ignore
		}
		prefDialog.setPreferenceStore(ps);

		// Open the dialog
		bSucceeded = (prefDialog.open() == 0);

		w.ds.setProperty("datastore.uri", ps.getString(URI));
	}

	@Override
	public boolean succeeded() {
		return bSucceeded;
	}
}
