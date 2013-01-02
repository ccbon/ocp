package org.ocpteam.ui.swt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.ocpteam.component.DataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class DataSourcesPreferencePage extends PreferencePage {
	private MyPreferenceStore ps;
	private List<Button> list;

	public DataSourcesPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		new Label(composite, SWT.NONE);

		Label lblListOfThe = new Label(composite, SWT.NONE);
		lblListOfThe.setText("List of the datasource that can be manipulated:");
		new Label(composite, SWT.NONE);

		list = new ArrayList<Button>();
		this.ps = (MyPreferenceStore) getPreferenceStore();
		JLG.debug("ps=" + ps);
		for (DataSource ds : ps.w.dsf.getDataSourceOrderedList()) {
			Button btn = new Button(composite, SWT.CHECK);
			btn.setText(ds.getProtocolName());
			boolean b = this.ps.getBoolean("ds." + ds.getProtocolName());
			btn.setSelection(b);
			JLG.debug("ds." + ds.getProtocolName() + "=" + b);
			new Label(composite, SWT.NONE);
			list.add(btn);
		}

		return composite;
	}

	protected void performDefaults() {
		for (Button btn : list) {
			boolean b = false;
			String name = "ds." + btn.getText();
			if (PreferencesAction.defaultPreferences.containsKey(name)) {
				String value = PreferencesAction.defaultPreferences
						.getString(name);
				if (value != null && value.equalsIgnoreCase("true")) {
					b = true;
				}
			}
			btn.setSelection(b);
			JLG.debug("ds." + btn.getText() + "=" + b);
		}
	}

	@Override
	protected void performApply() {
		for (Button btn : list) {
			ps.setValue("ds." + btn.getText(), btn.getSelection());
			JLG.debug("ds." + btn.getText());
		}
		try {
			ps.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ps.w.refreshNewMenuManager();
	}

	/**
	 * Called when user clicks Apply or OK
	 * 
	 * @return boolean
	 */
	public boolean performOk() {
		return true;
	}

}
