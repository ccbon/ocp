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
	private Table localDirectoryTable;
	private Table remoteDirectoryTable;

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
		GridLayout gl_leftComposite = new GridLayout(1, false);
		gl_leftComposite.marginLeft = 5;
		gl_leftComposite.marginWidth = 0;
		gl_leftComposite.horizontalSpacing = 0;
		leftComposite.setLayout(gl_leftComposite);
		
		Label localDirectoryLabel = new Label(leftComposite, SWT.NONE);
		localDirectoryLabel.setToolTipText("Local Directory");
		localDirectoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		localDirectoryLabel.setText("C:/truc/bidule");
		
		localDirectoryTable = new Table(leftComposite, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gd_localDirectoryTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_localDirectoryTable.heightHint = 275;
		localDirectoryTable.setLayoutData(gd_localDirectoryTable);
		localDirectoryTable.setHeaderVisible(true);
		
		TableColumn localNameColumn = new TableColumn(localDirectoryTable, SWT.NONE);
		localNameColumn.setWidth(100);
		localNameColumn.setText("Name");
		
		TableColumn localTypeColumn = new TableColumn(localDirectoryTable, SWT.NONE);
		localTypeColumn.setWidth(100);
		localTypeColumn.setText("Type");
		
		TableColumn localSizeColumn = new TableColumn(localDirectoryTable, SWT.NONE);
		localSizeColumn.setWidth(100);
		localSizeColumn.setText("Size");
		
		TableItem tableItem = new TableItem(localDirectoryTable, SWT.NONE);
		tableItem.setText(new String[] {"Directory", "hello", "12"});
		tableItem.setImage(SWTResourceManager.getImage(UserExplorerComposite.class, "directory.png"));
		
		TableItem tableItem_1 = new TableItem(localDirectoryTable, SWT.NONE);
		tableItem_1.setImage(SWTResourceManager.getImage(UserExplorerComposite.class, "file.png"));
		tableItem_1.setText("File");
		
		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_rightComposite = new GridLayout(1, false);
		gl_rightComposite.marginRight = 5;
		gl_rightComposite.marginWidth = 0;
		rightComposite.setLayout(gl_rightComposite);
		
		Label remoteDirectoryLabel = new Label(rightComposite, SWT.NONE);
		remoteDirectoryLabel.setToolTipText("Remote Directory");
		remoteDirectoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		remoteDirectoryLabel.setText("/bidule");
		
		remoteDirectoryTable = new Table(rightComposite, SWT.BORDER | SWT.FULL_SELECTION);
		remoteDirectoryTable.setHeaderVisible(true);
		GridData gd_remoteDirectoryTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_remoteDirectoryTable.heightHint = 275;
		remoteDirectoryTable.setLayoutData(gd_remoteDirectoryTable);
		
		TableColumn nameColumn = new TableColumn(remoteDirectoryTable, SWT.NONE);
		nameColumn.setWidth(100);
		nameColumn.setText("Name");
		
		TableColumn typeColumn = new TableColumn(remoteDirectoryTable, SWT.NONE);
		typeColumn.setWidth(100);
		typeColumn.setText("Type");
		
		TableColumn sizeColumn = new TableColumn(remoteDirectoryTable, SWT.NONE);
		sizeColumn.setWidth(100);
		sizeColumn.setText("Size");
		
		TableItem tableItem_2 = new TableItem(remoteDirectoryTable, 0);
		tableItem_2.setText(new String[] {"Directory", "hello", "12"});
		tableItem_2.setImage(SWTResourceManager.getImage(UserExplorerComposite.class, "directory.png"));
		sashForm.setWeights(new int[] {1, 1});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}