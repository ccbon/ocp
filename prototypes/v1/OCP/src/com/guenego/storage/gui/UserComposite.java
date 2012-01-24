package com.guenego.storage.gui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.User;

public class UserComposite extends Composite {
	private Text dirText;
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
		String defaultDir = user.getDefaultLocalDir();
		File defaultDirFile = new File(defaultDir);
		try {
			JLG.mkdir(defaultDir);
		} catch (Exception e) {
		}
		dirText.setText(defaultDirFile.getAbsolutePath());
		dirText.setBounds(10, 63, 267, 19);

		Button btnBrowse = new Button(this, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(
						getShell());
				directoryDialog.setFilterPath(dirText.getText());
				directoryDialog
						.setMessage("Please select a directory and click OK");

				String dir = directoryDialog.open();
				if (dir != null) {
					dirText.setText(dir);
				}

			}
		});
		btnBrowse.setBounds(283, 61, 68, 23);
		btnBrowse.setText("Browse");

		Button btnOpen = new Button(this, SWT.NONE);
		btnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File file = new File(dirText.getText());
				if (file.isDirectory()) {
					Program.launch(dirText.getText());
				}
			}
		});
		btnOpen.setBounds(357, 61, 68, 23);
		btnOpen.setText("Open");

		
		Button checkoutButton = new Button(this, SWT.NONE);
		checkoutButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					agent.getFileSystem(user).checkoutAll(dirText.getText());
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
				try {
					agent.getFileSystem(user).commitAll(dirText.getText());
				} catch (Exception e1) {
					QuickMessage.error(UserComposite.this.getShell(), "Error while commiting.");
					e1.printStackTrace();
				}

			}
		});
		commitButton.setBounds(209, 88, 68, 23);
		commitButton.setText("Commit");
		

	}
}
