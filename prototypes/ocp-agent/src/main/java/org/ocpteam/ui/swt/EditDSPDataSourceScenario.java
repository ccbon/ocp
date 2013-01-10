package org.ocpteam.ui.swt;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.interfaces.IPersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class EditDSPDataSourceScenario implements IScenario {

	private DataSourceWindow w;
	private boolean bSucceeded;

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
		PreferenceNode tcpServer = new PreferenceNode("General", "General",
				null, ConfigPrefPage.class.getName());
		PreferenceNode network = new PreferenceNode("Network", "Network", null,
				NetworkPrefPage.class.getName());

		// Add the nodes
		prefManager.addToRoot(tcpServer);
		prefManager.addToRoot(network);
		if (w.getDSEditMode() == DataSourceWindow.EDIT_MODE) {
			boolean bViewDataStore = (w.ds.usesComponent(IPersistentMap.class) && (w.ds
					.getComponent(IPersistentMap.class) instanceof PersistentFileMap));
			if (bViewDataStore) {
				PreferenceNode dataStore = new PreferenceNode("Datastore",
						"Datastore", null,
						ViewDataStorePrefPage.class.getName());
				prefManager.addToRoot(dataStore);
			}
		}

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
		if (prefDialog.open() != 0) {
			bSucceeded = false;
		} else {
			bSucceeded = true;
		}
	}

	@Override
	public boolean succeeded() {
		return bSucceeded;
	}
}
