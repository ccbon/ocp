package org.ocpteamx.protocol.dht6.swt;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class URIPrefPage extends PreferencePage {
	public URIPrefPage() {
	}

	private Text uriText;

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		// Get the preference store
		IPreferenceStore preferenceStore = getPreferenceStore();

		Label lblUri = new Label(composite, SWT.NONE);
		lblUri.setLayoutData(new RowData(35, SWT.DEFAULT));
		lblUri.setText("URI");

		uriText = new Text(composite, SWT.BORDER);
		uriText.setLayoutData(new RowData(337, SWT.DEFAULT));
		if (preferenceStore.getDefaultString(ConfigPreferenceScenario.URI).equals("")) {
			performDefaults();
		} else {
			uriText.setText(preferenceStore
					.getDefaultString(ConfigPreferenceScenario.URI));
		}
		return composite;
	}

	@Override
	protected void performDefaults() {
		// Reset the fields to the defaults
		uriText.setText(getPreferenceStore().getDefaultString(
				ConfigPreferenceScenario.URI));
	}
	
	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(ConfigPreferenceScenario.URI, uriText.getText());
		return super.performOk();
	}

}
