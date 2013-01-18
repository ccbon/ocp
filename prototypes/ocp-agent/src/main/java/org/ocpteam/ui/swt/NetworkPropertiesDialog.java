package org.ocpteam.ui.swt;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.component.DSPDataSource;

public class NetworkPropertiesDialog extends Dialog {
	private Table table;
	private DataSourceWindow w;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param window 
	 */
	public NetworkPropertiesDialog(Shell parentShell, DataSourceWindow window) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
		this.w = window;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnKey = new TableColumn(table, SWT.NONE);
		tblclmnKey.setWidth(180);
		tblclmnKey.setText("Key");
		
		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(200);
		tblclmnValue.setText("Value");
		DSPDataSource ds = (DSPDataSource) w.ds;
		Set<Object> set = ds.network.keySet();
		String[] array = set.toArray(new String[set.size()]);
		Arrays.sort(array);
		for (Object key : array) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String value = (String) ds.network.get(key);
			tableItem.setText(new String[] { (String) key, value });
		}
		return container;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Network Properties");
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
//		createButton(parent, IDialogConstants.CANCEL_ID,
//				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
