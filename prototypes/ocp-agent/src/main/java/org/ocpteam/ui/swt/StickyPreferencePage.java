package org.ocpteam.ui.swt;

import java.io.IOException;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class StickyPreferencePage extends PreferencePage {
	public static final String NEVER_STICKY = "never_sticky";
	private MyPreferenceStore ps;
	private Button btnDatasourceNeverSticky;

	public StickyPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		ps = (MyPreferenceStore) getPreferenceStore();
		
		btnDatasourceNeverSticky = new Button(composite, SWT.CHECK);
		btnDatasourceNeverSticky.setText("Datasource never sticky");
		btnDatasourceNeverSticky.setSelection(ps.getBoolean(NEVER_STICKY));
		return composite;
	}
	
	@Override
	protected void performDefaults() {
		btnDatasourceNeverSticky.setSelection(false);
	}
	
	@Override
	protected void performApply() {
		ps.setValue(NEVER_STICKY, btnDatasourceNeverSticky.getSelection());
		try {
			ps.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
