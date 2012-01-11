package com.guenego.ocp.gui.console;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.FileSystem;
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

	public Table localDirectoryTable;
	public Table remoteDirectoryTable;
	public File currentLocalDirectory;
	private LinkedList<Tree> treeList;
	public Tree currentTree;
	public String currentRemoteDirString;

	public Agent agent;
	public User user;
	private Label localDirectoryLabel;
	private Label remoteDirectoryLabel;

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
		final Display display = parent.getDisplay();
		final Shell shell = parent.getShell();

		currentLocalDirectory = new File(user.getDefaultLocalDir());

		currentRemoteDirString = "/";
		synchronizeRemote();

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
		localDirectoryTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.KEYPAD_CR:
				case (int) '\r':
					(new OpenFileAction(UserExplorerComposite.this)).run();
					break;
				case (int) SWT.F5:
					reloadLocalDirectoryTable();
					break;
				case SWT.DEL:
					(new RemoveFileAction(UserExplorerComposite.this)).run();
					break;
				default:
				}
				JLG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
			}
		});
		localDirectoryTable.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				if (localDirectoryTable.getSelection().length > 0) {
					JLG.debug("opening context menu");
					final MenuManager myMenu = new MenuManager("xxx");
					final Menu menu = myMenu.createContextMenu(shell);
					myMenu.add(new OpenFileAction(UserExplorerComposite.this));
					myMenu.add(new Separator());
					myMenu.add(new CommitAction(UserExplorerComposite.this));
					myMenu.add(new Separator());
					myMenu.add(new RemoveFileAction(UserExplorerComposite.this));
					menu.setEnabled(true);
					myMenu.setVisible(true);
					menu.setVisible(true);
				}
			}
		});
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

		remoteDirectoryLabel = new Label(rightComposite, SWT.NONE);
		remoteDirectoryLabel.setToolTipText("Remote Directory");
		remoteDirectoryLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				true, false, 1, 1));

		remoteDirectoryTable = new Table(rightComposite, SWT.BORDER
				| SWT.FULL_SELECTION);
		remoteDirectoryTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.KEYPAD_CR:
				case (int) '\r':
					(new OpenRemoteFileAction(UserExplorerComposite.this))
							.run();
					break;
				case SWT.F5:
					synchronizeRemote();
					reloadRemoteDirectoryTable();
					break;
				case SWT.DEL:
					(new RemoveRemoteFileAction(UserExplorerComposite.this))
					.run();
				default:
					break;
				}

				JLG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");

			}
		});
		remoteDirectoryTable.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				if (remoteDirectoryTable.getSelection().length > 0) {
					JLG.debug("opening context menu just closed to the selection");
					// TODO : replace the context menu at the good position if
					// needed.
					final MenuManager myMenu = new MenuManager("xxx");
					final Menu menu = myMenu.createContextMenu(shell);
					myMenu.add(new OpenRemoteFileAction(
							UserExplorerComposite.this));
					myMenu.add(new Separator());
					myMenu.add(new CheckOutAction(UserExplorerComposite.this));
					myMenu.add(new Separator());
					myMenu.add(new RemoveRemoteFileAction(
							UserExplorerComposite.this));
					menu.setEnabled(true);
					myMenu.setVisible(true);
					menu.setVisible(true);
				}

			}
		});
		remoteDirectoryTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				JLG.debug("double click");
				TableItem[] items = remoteDirectoryTable.getSelection();
				JLG.debug("length=" + items.length);
				if (items.length == 1) {
					JLG.debug("length=1");
					TableItem item = items[0];
					openRemoteFile(item);
				}
				JLG.debug("table item:" + e.widget.getClass());

			}
		});
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

		reloadRemoteDirectoryTable();
		sashForm.setWeights(new int[] { 1, 1 });

	}

	protected void openRemoteFile(TableItem item) {
		try {
			String name = item.getText(0);
			if (name.equals(DIRECTORY_PARENT)) {
				currentTree = treeList.removeLast();
				currentRemoteDirString = currentRemoteDirString.substring(0,
						currentRemoteDirString.lastIndexOf("/") + 1);
				if (currentLocalDirectory.equals("")) {
					currentRemoteDirString = "/";
				}
				reloadRemoteDirectoryTable();
			} else {
				TreeEntry te = (TreeEntry) item.getData();
				JLG.debug("Try to open " + te.getName());
				if (te.isTree()) {
					Pointer p = te.getPointer();
					treeList.addLast(currentTree);
					currentTree = (Tree) agent.get(user, p);
					if (!currentRemoteDirString.endsWith("/")) {
						currentRemoteDirString += "/";
					}
					currentRemoteDirString += te.getName();
				}
				reloadRemoteDirectoryTable();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void reloadRemoteDirectoryTable() {
		remoteDirectoryLabel.setText(currentRemoteDirString);
		// first make sure the table content is empty.
		remoteDirectoryTable.removeAll();

		if (!treeList.isEmpty()) {
			TableItem parentTreetableItem = new TableItem(remoteDirectoryTable,
					SWT.NONE);
			parentTreetableItem.setText(new String[] { DIRECTORY_PARENT,
					DIRECTORY_TYPE, DIRECTORY_SIZE });
			parentTreetableItem.setImage(DIRECTORY_ICON);
		}

		Collection<TreeEntry> set = currentTree.getEntries();
		// Create an array containing the elements in a set
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
			tableItem.setData(te);
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

	}

	protected void openLocalFile(TableItem item) {
		String name = item.getText(0);
		String type = item.getText(1);
		JLG.debug("type = " + type);
		if (type.equals(DIRECTORY_TYPE)) {

			if (name.equals(DIRECTORY_PARENT)) {
				currentLocalDirectory = currentLocalDirectory.getParentFile();
			} else {
				currentLocalDirectory = new File(currentLocalDirectory, name);
			}
			reloadLocalDirectoryTable();
		} else {
			Program.launch(new File(currentLocalDirectory, name)
					.getAbsolutePath());
		}
	}

	public void reloadLocalDirectoryTable() {
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
				if (f.length() > 1024) {
					size = (f.length() / 1024) + " KB";
				} else {
					size = f.length() + " B";
				}
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

	public void synchronizeRemote() {
		treeList = new LinkedList<Tree>();
		Pointer p = null;
		try {
			p = user.getRootPointer(agent);
			currentTree = (Tree) agent.get(user, p);
		} catch (Exception e) {
			currentTree = new Tree();
		}
		if (currentRemoteDirString.equals("/")) {
			return;
		}
		String[] dirnames = currentRemoteDirString.substring(1).split("/");
		for (int i = 0; i < dirnames.length; i++) {
			String dirname = dirnames[i];
			p = currentTree.getEntry(dirname).getPointer();
			Tree subTree;
			try {
				subTree = (Tree) agent.get(user, p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			treeList.addLast(currentTree);

			currentTree = subTree;
		}

	}

	public void deleteLocalFile(TableItem item) {
		String name = item.getText(0);
		String type = item.getText(1);
		if (name.equals(DIRECTORY_PARENT)) {
			QuickMessage.error(this.getShell(),
					"Cannot delete the parent directory.");
			return;
		}
		if (!QuickMessage.confirm(getShell(),
				"Are you sure you want to delete the file " + name + "?")) {
			return;
		}
		JLG.debug("type = " + type);
		JLG.rm(new File(currentLocalDirectory, name));
		reloadLocalDirectoryTable();
	}

	public void deleteRemoteFile(TableItem item) {
		
		String name = item.getText(0);

		FileSystem fs = new FileSystem(user, agent, null);
		try {
			fs.deleteFile(currentRemoteDirString, name);
			synchronizeRemote();
			reloadRemoteDirectoryTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
