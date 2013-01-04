package org.ocpteam.ui.swt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class ConfigPrefPage extends PreferencePage {
	private Text text;
	private MyPreferenceStore ps;

	public ConfigPrefPage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		Label lblPort = new Label(composite, SWT.NONE);
		lblPort.setText("Port:");

		text = new Text(composite, SWT.BORDER);
		int i = JLG.random(20000) + 20000;
		text.setText(Integer.toString(i));
//		text.setText("22222");
		return composite;
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		text.setText("22222");
	}
	
	@Override
	public boolean performOk() {
		JLG.debug("Config performApply");
		ps = (MyPreferenceStore) getPreferenceStore();
		ps.w.ds.setProperty("server.port", text.getText());
		return super.performOk();
	}
}
