package com.guenego.ocp.gui.console;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.Pointer;
import com.guenego.ocp.Tree;
import com.guenego.ocp.TreeEntry;
import com.guenego.ocp.User;

public class UserExplorerComposite extends Composite {
	private static final String DIRECTORY_SIZE = "";
	private static final String DIRECTORY_TYPE = "Directory";
	private static final String DIRECTORY_PARENT = "..";
	private static final String FILE_TYPE = "File";
	private static final Image DIRECTORY_ICON = SWTResourceManager.getImage(
			UserExplorerComposite.class, "directory.png");
	private static final Image FILE_ICON = SWTResourceManager.getImage(
			UserExplorerComposite.class, "file.png");

	private Table localDirectoryTable;
	private Table remoteDirectoryTable;
	private File currentLocalDirectory;
	private String currentRemoteDirString;
	private Tree currentTree;

	private Agent agent;
	private User user;
	private Label localDirectoryLabel;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public UserExplorerComposite(Composite parent, int style, Agent agent,
			User user) {
		super(parent, style);
		this.agent = agent;
		this.user = user;

		currentLocalDirectory = new File(user.getDefaultLocalDir());
		currentRemoteDirString = "/";
		Pointer p = null;
		try {
			p = user.getRootPointer(agent);
			currentTree = (Tree) agent.get(user, p);
		} catch (Exception e) {
			currentTree = new Tree();
		}

		setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(this, SWT.NONE);

		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_leftComposite = new GridLayout(1, false);
		gl_leftComposite.marginLeft = 5;
		gl_leftComposite.marginWidth = 0;
		gl_leftComposite.horizontalSpacing = 0;
		leftComposite.setLayout(gl_leftComposite);

		localDirectoryLabel = new Label(leftComposite, SWT.NONE);
		localDirectoryLabel.setToolTipText("Local Directory");
		localDirectoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));

		localDirectoryTable = new Table(leftComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		localDirectoryTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				JLG.debug("double click");
				TableItem[] items = localDirectoryTable.getSelection();
				if (items.length == 1) {
					TableItem item = items[0];
					openLocalFile(item);
				}
				JLG.debug("table item:" + e.widget.getClass());
			}
		});
		GridData gd_localDirectoryTable = new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1);
		gd_localDirectoryTable.heightHint = 275;
		localDirectoryTable.setLayoutData(gd_localDirectoryTable);
		localDirectoryTable.setHeaderVisible(true);

		TableColumn localNameColumn = new TableColumn(localDirectoryTable,
				SWT.NONE);
		localNameColumn.setWidth(100);
		localNameColumn.setText("Name");

		TableColumn localTypeColumn = new TableColumn(localDirectoryTable,
				SWT.NONE);
		localTypeColumn.setWidth(100);
		localTypeColumn.setText("Type");

		TableColumn localSizeColumn = new TableColumn(localDirectoryTable,
				SWT.NONE);
		localSizeColumn.setWidth(100);
		localSizeColumn.setText("Size");

		reloadLocalDirectoryTable();

		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_rightComposite = new GridLayout(1, false);
		gl_rightComposite.marginRight = 5;
		gl_rightComposite.marginWidth = 0;
		rightComposite.setLayout(gl_rightComposite);

		Label remoteDirectoryLabel = new Label(rightComposite, SWT.NONE);
		remoteDirectoryLabel.setToolTipText("Remote Directory");
		remoteDirectoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));
		remoteDirectoryLabel.setText(currentRemoteDirString);

		remoteDirectoryTable = new Table(rightComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		remoteDirectoryTable.setHeaderVisible(true);
		GridData gd_remoteDirectoryTable = new GridData(SWT.FILL, SWT.FILL,
				true, true, 1, 1);
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

		TableItem parentTreetableItem = new TableItem(remoteDirectoryTable,
				SWT.NONE);
		parentTreetableItem.setText(new String[] { DIRECTORY_PARENT,
				DIRECTORY_TYPE, DIRECTORY_SIZE });
		parentTreetableItem.setImage(SWTResourceManager.getImage(
				UserExplorerComposite.class, "directory.png"));

		Set<TreeEntry> set = currentTree.getEntries();
		// Create an array containing the elements in a set
		Object[] objectArray = set.toArray();
		TreeEntry[] array = (TreeEntry[]) set
				.toArray(new TreeEntry[set.size()]);
		// Order
		Arrays.sort(array, new Comparator<TreeEntry>() {
			public int compare(TreeEntry f1, TreeEntry f2) {
				if (f1.isFile() && f2.isTree()) {
					return 1;
				} else if (f2.isFile() && f1.isTree()) {
					return -1;
				} else {
					return f1.getName().compareTo(f2.getName());
				}
			}
		});

		for (TreeEntry te : array) {

			TableItem tableItem = new TableItem(remoteDirectoryTable, SWT.NONE);
			String type = null;
			String size = null;
			Image image = null;
			if (te.isTree()) {
				type = DIRECTORY_TYPE;
				size = "";
				image = DIRECTORY_ICON;
			} else {
				type = FILE_TYPE;
				size = ""; // not implemented yet
				image = FILE_ICON;
			}
			tableItem.setText(new String[] { te.getName(), type, size });
			tableItem.setImage(image);

		}

		sashForm.setWeights(new int[] { 1, 1 });

	}

	protected void openLocalFile(TableItem item) {
		String name = item.getText(0);
		String type = item.getText(1);
		JLG.debug("type = " + type);
		if (type.equals(DIRECTORY_TYPE)) {
			
			if (name.equals(DIRECTORY_PARENT)) {
				currentLocalDirectory = currentLocalDirectory
						.getParentFile();
			} else {
				currentLocalDirectory = new File(currentLocalDirectory, name);
			}
			reloadLocalDirectoryTable();
		} else {
			Program.launch(new File(currentLocalDirectory, name).getAbsolutePath());
		}
	}

	protected void reloadLocalDirectoryTable() {
		String path = "";
		if (currentLocalDirectory != null) {
			path = currentLocalDirectory.getAbsolutePath();
		}
		localDirectoryLabel.setText(path);
		// first make sure the table content is empty.
		localDirectoryTable.removeAll();

		if (currentLocalDirectory != null) {
			TableItem parentDirtableItem = new TableItem(localDirectoryTable,
					SWT.NONE);
			parentDirtableItem.setText(new String[] { DIRECTORY_PARENT,
					DIRECTORY_TYPE, DIRECTORY_SIZE });
			parentDirtableItem.setImage(SWTResourceManager.getImage(
					UserExplorerComposite.class, "directory.png"));
		}

		File[] children = null;
		if (currentLocalDirectory != null) {
			children = currentLocalDirectory.listFiles();
		} else {
			children = File.listRoots();
		}

		Arrays.sort(children, new Comparator<File>() {
			public int compare(File f1, File f2) {
				if (f1.isFile() && f2.isDirectory()) {
					return 1;
				} else if (f2.isFile() && f1.isDirectory()) {
					return -1;
				} else {
					return f1.getName().compareTo(f2.getName());
				}
			}
		});

		for (File f : children) {
			JLG.debug("filename: " + f.getAbsolutePath());
			TableItem tableItem = new TableItem(localDirectoryTable, SWT.NONE);
			String type = null;
			String size = null;
			Image image = null;
			if (f.isDirectory()) {
				type = DIRECTORY_TYPE;
				size = "";
				image = DIRECTORY_ICON;
			} else {
				type = FILE_TYPE;
				size = (f.length() / 1024) + " KB";
				image = FILE_ICON;
			}
			String name = null;
			if (currentLocalDirectory == null) {
				name = f.getAbsolutePath();
			} else {
				name = f.getName();
			}
			tableItem.setText(new String[] { name, type, size });
			tableItem.setImage(image);
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
