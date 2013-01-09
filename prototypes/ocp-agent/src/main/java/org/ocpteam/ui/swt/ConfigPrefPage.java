package org.ocpteam.ui.swt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.Server;
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
		ps = (MyPreferenceStore) getPreferenceStore();
		if (ps.w.getDSEditMode() == DataSourceWindow.EDIT_MODE) {
			JLG.debug("Edit mode");
			text.setText(ps.w.ds.getProperty("server.port"));
		} else {
			int i = JLG.random(20000) + 20000;
			text.setText(Integer.toString(i));
		}
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
		ps.w.ds.setProperty("server.port", text.getText());
		syncServer();
		return super.performOk();
	}

	private void syncServer() {
		try {
			Server server = ps.w.ds.getComponent(Server.class);
			if (server == null) {
				return;
			}
			if (!server.isStarted()) {
				return;
			}
			int port = server.getListeners().get(0).getUrl().getPort();
			int newPort = Integer.parseInt(text.getText());
			if (port != newPort) {
				server.stop();
				DSPDataSource ds = (DSPDataSource) ps.w.ds;
				ds.configureServer();
				server.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
