package org.ocpteam.ui.swt.editprefpage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class GeneralPreferencePage extends PreferencePage {
	public static final String GENERAL_PREFIX = "general.";
	public static final String CONFIRM_ON_EXIT = GENERAL_PREFIX
			+ "confirm_on_exit";
	public static final String NEVER_STICKY = GENERAL_PREFIX + "never_sticky";
	public static final String NO_NAT_TRAVERSAL = GENERAL_PREFIX
			+ "nat_traversal";
	public static final String LOGFILE = GENERAL_PREFIX + "logfile";
	public static final String LOG_LEVEL = GENERAL_PREFIX + "loglevel";

	private MyPreferenceStore ps;
	private Button btnConfirmOnExit;
	private Button btnDatasourceNeverSticky;
	private Button btnDoNotUse;
	private Button btnSaveLogsIn;
	private Text text;
	private Link link;
	private Label lblLogLevel;
	private Combo combo;

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
		new Label(composite, SWT.NONE);

		btnSaveLogsIn = new Button(composite, SWT.CHECK);
		btnSaveLogsIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.setEnabled(btnSaveLogsIn.getSelection());
			}
		});
		btnSaveLogsIn.setText("Append logs to a file");
		boolean b = !ps.getString(LOGFILE).equals("");
		btnSaveLogsIn.setSelection(b);

		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		String logfile = b ? ps.getString(LOGFILE) : DataSourceWindow.GDSE_DIR
				+ "/log/gdse_%u.log";
		text.setText(new File(logfile).getAbsolutePath());
		text.setEnabled(btnSaveLogsIn.getSelection());

		link = new Link(composite, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://docs.oracle.com/javase/6/docs/api/java/util/logging/FileHandler.html");
			}
		});
		link.setText("See the <a>Javadoc</a> on the syntax pattern.");

		lblLogLevel = new Label(composite, SWT.NONE);
		GridData gd_lblLogLevel = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_lblLogLevel.widthHint = 76;
		lblLogLevel.setLayoutData(gd_lblLogLevel);
		lblLogLevel.setText("Log level");

		combo = new Combo(composite, SWT.NONE);
		combo.setItems(new String[] { Level.OFF.getName(),
				Level.SEVERE.getName(), Level.WARNING.getName(),
				Level.INFO.getName(), Level.CONFIG.getName(),
				Level.FINE.getName(), Level.FINER.getName(),
				Level.FINEST.getName(), Level.ALL.getName() });
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
				1));
		int lvl = lvl2case(ps.getString(LOG_LEVEL));
		combo.select(lvl);

		return composite;
	}

	private int lvl2case(String lvl) {
		int result = 0;
		if (lvl.equals(Level.SEVERE.getName())) {
			result = 1;
		} else if (lvl.equals(Level.WARNING.getName())) {
			result = 2;
		} else if (lvl.equals(Level.INFO.getName())) {
			result = 3;
		} else if (lvl.equals(Level.CONFIG.getName())) {
			result = 4;
		} else if (lvl.equals(Level.FINE.getName())) {
			result = 5;
		} else if (lvl.equals(Level.FINER.getName())) {
			result = 6;
		} else if (lvl.equals(Level.FINEST.getName())) {
			result = 7;
		} else if (lvl.equals(Level.ALL.getName())) {
			result = 8;
		}
		return result;
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
		ps.setValue(LOG_LEVEL, combo.getText());
		LOG.setLevel(Level.parse(combo.getText()));
		if (btnSaveLogsIn.getSelection()) {
			ps.setValue(LOGFILE, text.getText());
		} else {
			ps.setToDefault(LOGFILE);
		}
		try {
			ps.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
