package org.ocpteam.ui.swt;

import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class NetworkPrefPage extends PreferencePage {

	private static final String DEFAULT_SPONSOR_URL = "tcp://localhost:22222";
	private Text text;
	private Label lblPleaseEnterAt;
	private Button btnJoinAnExisting;
	private Button btnDelete;
	private Table table;
	private Properties p;
	private Composite composite_1;
	private Button btnSet;
	private Group grpExistingNetwork;
	private Group grpNewNetwork;
	private String oldSponsor;

	public class InsertKeyDialog extends Dialog {
		private Text keyText;
		private Text valueText;

		public InsertKeyDialog(Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);

			Label lblKey = new Label(container, SWT.NONE);
			lblKey.setText("Key:");

			keyText = new Text(container, SWT.BORDER);
			keyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));

			Label lblValue = new Label(container, SWT.NONE);
			lblValue.setText("Value:");

			valueText = new Text(container, SWT.BORDER);
			valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false, 1, 1));

			return container;
		}

		@Override
		protected void okPressed() {
			p.put(keyText.getText(), valueText.getText());
			super.okPressed();
		}

		/**
		 * Create contents of the button bar.
		 * 
		 * @param parent
		 */
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID,
					IDialogConstants.OK_LABEL, true);
			createButton(parent, IDialogConstants.CANCEL_ID,
					IDialogConstants.CANCEL_LABEL, false);
		}

		@Override
		protected Point getInitialSize() {
			return new Point(450, 300);
		}
	}

	public NetworkPrefPage() {
		p = new Properties();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		btnJoinAnExisting = new Button(composite, SWT.CHECK);
		btnJoinAnExisting.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				true, false, 1, 1));
		btnJoinAnExisting.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		btnJoinAnExisting.setText("Join an existing network");
		MyPreferenceStore ps = (MyPreferenceStore) getPreferenceStore();
		if (ps.w.getDSEditMode() == DataSourceWindow.EDIT_MODE) {
			boolean b = ps.w.ds.getProperty("agent.isFirst", "yes").equals(
					"yes");
			btnJoinAnExisting.setSelection(!b);
			btnJoinAnExisting.setEnabled(false);
		}
		grpExistingNetwork = new Group(composite, SWT.NONE);
		grpExistingNetwork.setLayout(new FormLayout());
		GridData gd_grpExistingNetwork = new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1);
		gd_grpExistingNetwork.heightHint = 72;
		gd_grpExistingNetwork.widthHint = 321;
		grpExistingNetwork.setLayoutData(gd_grpExistingNetwork);
		grpExistingNetwork.setText("Existing Network");

		lblPleaseEnterAt = new Label(grpExistingNetwork, SWT.NONE);
		FormData fd_lblPleaseEnterAt = new FormData();
		fd_lblPleaseEnterAt.top = new FormAttachment(0, 6);
		fd_lblPleaseEnterAt.left = new FormAttachment(0, 7);
		lblPleaseEnterAt.setLayoutData(fd_lblPleaseEnterAt);
		lblPleaseEnterAt.setText("Please enter a sponsor");

		text = new Text(grpExistingNetwork, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.top = new FormAttachment(lblPleaseEnterAt, 6);
		fd_text.left = new FormAttachment(lblPleaseEnterAt, 0, SWT.LEFT);
		text.setLayoutData(fd_text);
		if (ps.w.getDSEditMode() == DataSourceWindow.EDIT_MODE) {
			oldSponsor = ps.w.ds.getProperty("sponsor.1", "");
			text.setText(oldSponsor);
		} else {
			text.setText(DEFAULT_SPONSOR_URL);
		}

		grpNewNetwork = new Group(composite, SWT.NONE);
		grpNewNetwork.setLayout(new GridLayout(2, false));
		GridData gd_grpNewNetwork = new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1);
		gd_grpNewNetwork.heightHint = 213;
		gd_grpNewNetwork.widthHint = 331;
		grpNewNetwork.setLayoutData(gd_grpNewNetwork);
		grpNewNetwork.setText("New Network");

		table = new Table(grpNewNetwork, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (table.getSelection().length > 0) {
					btnDelete.setEnabled(true);
				} else {
					btnDelete.setEnabled(false);
				}
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnKey = new TableColumn(table, SWT.NONE);
		tblclmnKey.setWidth(96);
		tblclmnKey.setText("Key");

		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(182);
		tblclmnValue.setText("Value");

		btnSet = new Button(grpNewNetwork, SWT.NONE);
		btnSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		btnSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LOG.debug("Add entry");
				InsertKeyDialog dialog = new InsertKeyDialog(getShell());
				dialog.open();
				refreshTable();
			}
		});
		btnSet.setText("Set");

		btnDelete = new Button(grpNewNetwork, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,
				1, 1));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (TableItem item : table.getSelection()) {
					p.remove(item.getText(0));
				}
				btnDelete.setEnabled(false);
				refreshTable();
			}
		});
		btnDelete.setText("Delete");
		btnDelete.setEnabled(false);

		composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new FormLayout());

		refresh();
		return composite;
	}

	protected void refresh() {
		boolean b = btnJoinAnExisting.getSelection();
		grpExistingNetwork.setEnabled(b);
		for (Control c : grpExistingNetwork.getChildren()) {
			c.setEnabled(b);
		}

		grpNewNetwork.setEnabled(!b);
		for (Control c : grpNewNetwork.getChildren()) {
			c.setEnabled(!b);
		}
		refreshTable();
		btnDelete.setEnabled(false);
	}

	@Override
	protected void performDefaults() {
		btnJoinAnExisting.setSelection(false);
		text.setText(DEFAULT_SPONSOR_URL);
		p.clear();
		refresh();
	}

	private void refreshTable() {
		table.removeAll();
		for (Object key : p.keySet()) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String value = (String) p.get(key);
			tableItem.setText(new String[] { (String) key, value });
		}
	}

	@Override
	public boolean performOk() {
		LOG.debug("Network performApply");
		MyPreferenceStore ps = (MyPreferenceStore) getPreferenceStore();
		if (btnJoinAnExisting.getSelection()) {
			ps.w.ds.setProperty("sponsor.1", text.getText());
			ps.w.ds.setProperty("agent.isFirst", "no");
			if (ps.w.getDSEditMode() == DataSourceWindow.EDIT_MODE) {
				if (!oldSponsor.equals(text.getText())) {
					try {
						ps.w.ds.disconnect();
						ps.w.ds.connect();
					} catch (Exception e) {
						e.printStackTrace();
					}
					oldSponsor = text.getText();
				}
			}
		} else {
			for (Object key : p.keySet()) {
				ps.w.ds.setProperty("network." + key, (String) p.get(key));
			}
		}
		return super.performOk();
	}
}
