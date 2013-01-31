package org.ocpteam.ui.swt.editprefpage;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class MonitorPreferencePage extends PreferencePage {
	public static final String MONITOR_PREFIX = "monitor.";
	public static final String REFRESHRATE = MONITOR_PREFIX + "refresh_rate";
	public static final String MAXTHREAD = MONITOR_PREFIX + "max_thread";

	private MyPreferenceStore ps;
	private Label lblMonitorRefreshRate;
	private Text refreshRate;
	private Label lblMaxThread;
	private Text maxThread;

	public MonitorPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		ps = (MyPreferenceStore) getPreferenceStore();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		lblMonitorRefreshRate = new Label(composite, SWT.NONE);
		lblMonitorRefreshRate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		lblMonitorRefreshRate.setText("Monitor refresh rate (ms)");

		refreshRate = new Text(composite, SWT.BORDER);
		refreshRate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		int rate = ps.getInt(REFRESHRATE);
		refreshRate.setText("" + rate);

		lblMaxThread = new Label(composite, SWT.NONE);
		lblMaxThread.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
				false, 1, 1));
		lblMaxThread.setText("Max thread");

		maxThread = new Text(composite, SWT.BORDER);
		maxThread.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		int thread = ps.getInt(MAXTHREAD);
		maxThread.setText("" + thread);

		return composite;
	}

	@Override
	protected void performDefaults() {
		refreshRate.setText(ps.getDefaultInt(REFRESHRATE) + "");
		maxThread.setText(ps.getDefaultInt(MAXTHREAD) + "");
		super.performDefaults();
	}

	@Override
	protected void performApply() {
		int rate = 0;
		int thread = 0;
		try {
			rate = Integer.parseInt(refreshRate.getText());
			thread = Integer.parseInt(maxThread.getText());
		} catch (Exception e) {
			QuickMessage.error(getShell(),
					"Refresh rate and max thread must be numbers.");
			return;
		}
		if (rate < 1 || thread < 1) {
			QuickMessage.error(getShell(),
					"Refresh rate and max thread must be superior than 1.");
			return;
		}
		if (thread != ps.getInt(MAXTHREAD)) {
			ThreadPoolExecutor pool = ps.w.getMonitor().getPool();
			pool.setCorePoolSize(thread);
			pool.setMaximumPoolSize(thread);
		}
		ps.setValue(REFRESHRATE, rate);
		ps.setValue(MAXTHREAD, thread);
		try {
			ps.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
