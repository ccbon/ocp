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
	public static final String CONFIRM_ON_EXIT = GENERAL_PREFIX
			+ "confirm_on_exit";
	public static final String NEVER_STICKY = GENERAL_PREFIX + "never_sticky";
	public static final String NO_NAT_TRAVERSAL = GENERAL_PREFIX
			+ "nat_traversal";

	private MyPreferenceStore ps;
	private Button btnConfirmOnExit;
	private Button btnDatasourceNeverSticky;
	private Button btnDoNotUse;

	public GeneralPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		ps = (MyPreferenceStore) getPreferenceStore();

		btnConfirmOnExit = new Button(composite, SWT.CHECK);
		btnConfirmOnExit.setText("Confirm on exit");
		btnConfirmOnExit.setSelection(ps.getBoolean(CONFIRM_ON_EXIT));

		btnDatasourceNeverSticky = new Button(composite, SWT.CHECK);
		btnDatasourceNeverSticky.setText("Datasource never sticky");
		btnDatasourceNeverSticky.setSelection(ps.getBoolean(NEVER_STICKY));

		btnDoNotUse = new Button(composite, SWT.CHECK);
		btnDoNotUse.setText("Do not use NATTraversal");
		btnDoNotUse.setSelection(ps.getBoolean(NO_NAT_TRAVERSAL));

		return composite;
	}

	@Override
	protected void performDefaults() {
		btnConfirmOnExit.setSelection(true);
		btnDatasourceNeverSticky.setSelection(false);
		btnDoNotUse.setSelection(false);
		super.performDefaults();
	}

	@Override
	protected void performApply() {
		ps.setValue(CONFIRM_ON_EXIT, btnConfirmOnExit.getSelection());
		ps.setValue(NEVER_STICKY, btnDatasourceNeverSticky.getSelection());
		ps.setValue(NO_NAT_TRAVERSAL, btnDoNotUse.getSelection());
		try {
			ps.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
