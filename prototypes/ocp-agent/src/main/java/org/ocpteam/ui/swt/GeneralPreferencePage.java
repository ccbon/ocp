package org.ocpteam.ui.swt;

import java.io.IOException;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class GeneralPreferencePage extends PreferencePage {
	public static final String GENERAL_PREFIX = "general.";
	public static final String CONFIRM_ON_EXIT = "confirm_on_exit";
	public static final String NEVER_STICKY = "never_sticky";
	private MyPreferenceStore ps;
	private Button btnConfirmOnExit;
	private Button btnDatasourceNeverSticky;

	public GeneralPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		ps = (MyPreferenceStore) getPreferenceStore();

		btnConfirmOnExit = new Button(composite, SWT.CHECK);
		btnConfirmOnExit.setText("Confirm on exit");
		btnConfirmOnExit.setSelection(ps.getBoolean(GENERAL_PREFIX
				+ CONFIRM_ON_EXIT));

		btnDatasourceNeverSticky = new Button(composite, SWT.CHECK);
		btnDatasourceNeverSticky.setText("Datasource never sticky");
		btnDatasourceNeverSticky.setSelection(ps.getBoolean(GENERAL_PREFIX
				+ NEVER_STICKY));

		return composite;
	}

	@Override
	protected void performDefaults() {
		btnConfirmOnExit.setSelection(true);
		btnDatasourceNeverSticky.setSelection(false);
		super.performDefaults();
	}

	@Override
	protected void performApply() {
		ps.setValue(GENERAL_PREFIX + CONFIRM_ON_EXIT,
				btnConfirmOnExit.getSelection());
		ps.setValue(GENERAL_PREFIX + NEVER_STICKY,
				btnDatasourceNeverSticky.getSelection());
		try {
			ps.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
