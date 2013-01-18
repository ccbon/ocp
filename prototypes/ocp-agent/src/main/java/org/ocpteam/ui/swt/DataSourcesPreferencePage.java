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
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;
import org.ocpteam.ui.swt.action.EditPreferencesAction;

public class DataSourcesPreferencePage extends PreferencePage {
	public static final String DS_PREFIX = "ds.";
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
		LOG.debug("ps=" + ps);
				
		for (DataSource ds : ps.w.dsf.getDataSourceOrderedList()) {
			Button btn = new Button(composite, SWT.CHECK);
			btn.setText(ds.getProtocolName());
			boolean b = this.ps.getBoolean(DS_PREFIX + ds.getProtocolName());
			btn.setSelection(b);
			LOG.debug(DS_PREFIX + ds.getProtocolName() + "=" + b);
			new Label(composite, SWT.NONE);
			list.add(btn);
		}

		return composite;
	}

	protected void performDefaults() {
		for (Button btn : list) {
			boolean b = false;
			String name = DS_PREFIX + btn.getText();
			if (EditPreferencesAction.defaultPreferences.containsKey(name)) {
				String value = EditPreferencesAction.defaultPreferences
						.getString(name);
				if (value != null && value.equalsIgnoreCase("true")) {
					b = true;
				}
			}
			btn.setSelection(b);
			LOG.debug(DS_PREFIX + btn.getText() + "=" + b);
		}
	}

	@Override
	protected void performApply() {
		for (Button btn : list) {
			ps.setValue(DS_PREFIX + btn.getText(), btn.getSelection());
			LOG.debug("ds." + btn.getText());
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
