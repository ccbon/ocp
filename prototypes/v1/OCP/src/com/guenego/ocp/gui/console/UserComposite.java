package com.guenego.ocp.gui.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.FileSystem;
import com.guenego.ocp.User;

public class UserComposite extends Composite {
	private Text dirText;
	private String selectedDir;
	private Agent agent;
	private User user;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 * @param agent
	 */
	public UserComposite(Composite parent, int style, Agent a, User u) {
		super(parent, style);
		this.agent = a;
		this.user = u;
		setLayout(null);

		Label lblLocalDirectory = new Label(this, SWT.NONE);
		lblLocalDirectory.setBounds(10, 44, 109, 13);
		lblLocalDirectory.setText("Local Directory");

		dirText = new Text(this, SWT.BORDER);
		dirText.setBounds(10, 63, 267, 19);

		Button btnBrowse = new Button(this, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(
						getShell());
				directoryDialog.setFilterPath(selectedDir);
				directoryDialog
						.setMessage("Please select a directory and click OK");

				String dir = directoryDialog.open();
				if (dir != null) {
					dirText.setText(dir);
					selectedDir = dir;
				}

			}
		});
		btnBrowse.setBounds(283, 61, 68, 23);
		btnBrowse.setText("Browse");

		Button checkoutButton = new Button(this, SWT.NONE);
		checkoutButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileSystem fs = new FileSystem(user, agent, selectedDir);
				try {
					fs.checkout();
				} catch (Exception e1) {
					JLG.error(e1);
				}
			}
		});
		checkoutButton.setBounds(10, 88, 68, 23);
		checkoutButton.setText("Check out");

		Button commitButton = new Button(this, SWT.NONE);
		commitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileSystem fs = new FileSystem(user, agent, selectedDir);
				try {
					fs.commit();
				} catch (Exception e1) {
					JLG.error(e1);
				}

			}
		});
		commitButton.setBounds(209, 88, 68, 23);
		commitButton.setText("Commit");

	}
}
