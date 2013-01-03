package org.ocpteam.ui.swt;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.ocpteam.misc.JLG;

public class PreferencesAction extends Action {
	public static ResourceBundle defaultPreferences = ResourceBundle
			.getBundle(PreferencesAction.class.getPackage().getName()
					+ ".preferences");

	private DataSourceWindow w;

	public PreferencesAction(DataSourceWindow w) {
		this.w = w;
		setText("&Preferences");
		setToolTipText("Open settings dialog");
	}

	@Override
	public void run() {
		JLG.debug("Start preferences");
		try {
			// Create the preference manager
			PreferenceManager mgr = new PreferenceManager();

			// Create the nodes
			PreferenceNode generalPrefNode = new PreferenceNode("General",
					"General", null,
					GeneralPreferencePage.class.getName());
			PreferenceNode dsPrefNode = new PreferenceNode("Datasources",
					"Datasources", null,
					DataSourcesPreferencePage.class.getName());

			// Add the nodes
			mgr.addToRoot(generalPrefNode);
			mgr.addToRoot(dsPrefNode);

			// Create the preferences dialog
			PreferenceDialog dlg = new PreferenceDialog(null, mgr);
			dlg.setPreferenceStore(w.ps);

			// Open the dialog
			dlg.open();
			// Save the preferences
			w.ps.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
