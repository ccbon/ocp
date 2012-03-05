package org.ocpteam.ui.swt;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;

public class DefineDataSourceWizardPage extends WizardPage {
	public static final int FILE_MODE = 0;
	public static final int PROTOCOL_MODE = 1;
	public String selection;
	public int mode;
	public String filename;

	private Map<String, String> map;

	/**
	 * Create the wizard.
	 */
	public DefineDataSourceWizardPage() {
		super("DefineDataSourceWizardPage");
		setTitle("Please specify a data source.");
		setDescription("Please enter the requested information.");
		map = new HashMap<String, String>();
		for (Enumeration<String> e = DataSource.protocolResource.getKeys(); e
				.hasMoreElements();) {
			String key = (String) e.nextElement();
			map.put(key, DataSource.protocolResource.getString(key));
		}

	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);

		final Button btnBrowse = new Button(container, SWT.NONE);
		final Combo combo = new Combo(container, SWT.READ_ONLY);
		final Text filenameText = new Text(container, SWT.BORDER);
		filenameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filename = filenameText.getText();
				refresh();
			}
		});

		Button btnEnterAFilename = new Button(container, SWT.RADIO);
		btnEnterAFilename.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JLG.debug("select choose file");
				filenameText.setEnabled(true);
				btnBrowse.setEnabled(true);
				combo.setEnabled(false);
				mode = FILE_MODE;
				refresh();
			}
		});
		btnEnterAFilename.setBounds(10, 10, 354, 16);
		btnEnterAFilename
				.setText("Enter an existing datasource file (*.zip, *.uri, ...):");

		filenameText.setBounds(20, 32, 270, 19);

		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell());
				fileDialog.setFilterPath(System.getProperty("user.home"));
				fileDialog.setText("Please select a file and click OK");

				filename = fileDialog.open();
				if (filename != null) {
					filenameText.setText(filename);
				}
			}
		});
		btnBrowse.setBounds(296, 30, 68, 23);
		btnBrowse.setText("Browse");

		Button btnUseAProtocol = new Button(container, SWT.RADIO);
		btnUseAProtocol.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JLG.debug("select choose protocol");
				filenameText.setEnabled(false);
				btnBrowse.setEnabled(false);
				combo.setEnabled(true);
				mode = PROTOCOL_MODE;
				refresh();
			}

		});
		btnUseAProtocol.setBounds(10, 98, 280, 16);
		btnUseAProtocol.setText("Or choose a protocol");

		final String[] keys = (String[]) map.keySet().toArray(
				new String[map.size()]);
		Arrays.sort(keys);

		combo.setBounds(20, 120, 182, 21);
		combo.setItems(keys);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selection = keys[combo.getSelectionIndex()];
				JLG.debug("selection = " + selection);
			}
		});
		combo.select(0);
		selection = keys[0];
		mode = FILE_MODE;
	}

	private void refresh() {
		if (mode == FILE_MODE) {
			if (JLG.isNullOrEmpty(filename)) {
				setErrorMessage(null);
			} else {
				try {
					//new DataSource(new File(filename));
					setErrorMessage(null);
				} catch (Exception e1) {
					JLG.debug("error");
					// e1.printStackTrace();
					setErrorMessage("this file is not a valid datasource");
				}
			}
		} else {
			setErrorMessage(null);
		}
		getWizard().getContainer().updateButtons();
	}

	@Override
	public boolean isPageComplete() {
		JLG.debug("is page complete ?");
		if (mode == FILE_MODE) {
			if (JLG.isNullOrEmpty(filename)) {
				return false;
			}
		}
		if (getErrorMessage() != null) {
			return false;
		}
		return true;
	}

}
