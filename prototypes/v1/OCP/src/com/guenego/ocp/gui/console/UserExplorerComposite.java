package com.guenego.ocp.gui.console;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

import com.guenego.ocp.Agent;
import com.guenego.ocp.User;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class UserExplorerComposite extends Composite {
	private Table leftTable;
	private Table table;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public UserExplorerComposite(Composite parent, int style, Agent agent,
			User user) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new GridLayout(1, false));
		
		Label lblCtrucbidule = new Label(leftComposite, SWT.NONE);
		lblCtrucbidule.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lblCtrucbidule.setText("C:/truc/bidule");
		
		leftTable = new Table(leftComposite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_leftTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_leftTable.heightHint = 275;
		leftTable.setLayoutData(gd_leftTable);
		leftTable.setHeaderVisible(true);
		leftTable.setLinesVisible(true);
		
		TableColumn tblclmnName = new TableColumn(leftTable, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnType = new TableColumn(leftTable, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnSize = new TableColumn(leftTable, SWT.NONE);
		tblclmnSize.setWidth(100);
		tblclmnSize.setText("Size");
		
		TableItem tableItem = new TableItem(leftTable, SWT.NONE);
		tableItem.setImage(SWTResourceManager.getImage(UserExplorerComposite.class, "directory.png"));
		tableItem.setText("Directory");
		
		TableItem tableItem_1 = new TableItem(leftTable, SWT.NONE);
		tableItem_1.setImage(SWTResourceManager.getImage(UserExplorerComposite.class, "file.png"));
		tableItem_1.setText("File");
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		label.setText("C:/truc/bidule");
		
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 275;
		table.setLayoutData(gd_table);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(100);
		tableColumn.setText("Name");
		
		TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("Type");
		
		TableColumn tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("Size");
		sashForm.setWeights(new int[] {1, 1});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
