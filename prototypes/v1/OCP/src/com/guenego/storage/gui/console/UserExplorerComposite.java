package com.guenego.storage.gui.console;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.guenego.misc.JLG;
import com.guenego.storage.Agent;
import com.guenego.storage.FileInterface;
import com.guenego.storage.User;

public class UserExplorerComposite extends Composite {
	private static final String DIRECTORY_SIZE = "";
	private static final String DIRECTORY_TYPE = "Directory";
	private static final String DIRECTORY_PARENT = "..";
	private static final String DIRECTORY_NEW = "New Folder";
	private static final String FILE_TYPE = "File";
	private static final Image DIRECTORY_ICON = SWTResourceManager.getImage(
			UserExplorerComposite.class, "directory.png");
	private static final Image FILE_ICON = SWTResourceManager.getImage(
			UserExplorerComposite.class, "file.png");

	public Table localDirectoryTable;
	public Table remoteDirectoryTable;
	public File currentLocalDirectory;
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
		final Shell shell = parent.getShell();

		currentLocalDirectory = new File(user.getDefaultLocalDir());
		currentRemoteDirString = "/";

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
				| SWT.FULL_SELECTION | SWT.MULTI);
		localDirectoryTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.KEYPAD_CR:
				case (int) '\r':
					(new OpenFileAction(UserExplorerComposite.this)).run();
					break;
				case (int) SWT.F2:
					(new RenameFileAction(UserExplorerComposite.this)).run();
					break;
				case (int) SWT.F5:
					reloadLocalDirectoryTable();
					break;
				case SWT.DEL:
					(new RemoveFileAction(UserExplorerComposite.this)).run();
					break;
				case SWT.ESC:
					localDirectoryTable.deselectAll();
				default:
				}
				JLG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
			}
		});
		localDirectoryTable.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent arg0) {
				JLG.debug("opening context menu");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu.createContextMenu(shell);

				int sel = localDirectoryTable.getSelection().length;
				if (sel > 0) {
					OpenFileAction openFileAction = new OpenFileAction(
							UserExplorerComposite.this);
					myMenu.add(openFileAction);

					myMenu.add(new Separator());
					myMenu.add(new CommitAction(UserExplorerComposite.this));
					myMenu.add(new Separator());
					myMenu.add(new RemoveFileAction(UserExplorerComposite.this));
					RenameFileAction renameFileAction = new RenameFileAction(
							UserExplorerComposite.this);
					myMenu.add(renameFileAction);

					if (sel > 1) {
						openFileAction.setEnabled(false);
						renameFileAction.setEnabled(false);
					}
				}
				if (sel == 0) {
					myMenu.add(new CreateNewDirAction(
							UserExplorerComposite.this));
				}
				menu.setEnabled(true);
				myMenu.setVisible(true);
				menu.setVisible(true);

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

			@Override
			public void mouseDown(MouseEvent e) {
				JLG.debug("mouse down");
				Point pt = new Point(e.x, e.y);
				if (localDirectoryTable.getItem(pt) == null) {
					JLG.debug("cancel selection");
					localDirectoryTable.deselectAll();
				}
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

		DragSource dragLocalSource = new DragSource(localDirectoryTable,
				DND.DROP_MOVE | DND.DROP_COPY);
		dragLocalSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					// event.data = localDirectoryTable.getSelection();
					event.data = new String("this is my fake data");
				}
			}

			@Override
			public void dragStart(DragSourceEvent event) {
				if (localDirectoryTable.getSelection().length == 0) {
					event.doit = false;
				}
			}
		});
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		dragLocalSource.setTransfer(types);

		DropTarget dropLocalTarget = new DropTarget(localDirectoryTable,
				DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		dropLocalTarget.setTransfer(types);
		dropLocalTarget.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(DropTargetEvent event) {
				JLG.debug("drop");
				if (TextTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					String text = (String) event.data;
					JLG.debug("received from transfer: " + text);
					(new CheckOutAction(UserExplorerComposite.this)).run();
				}
			}
		});

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
				| SWT.FULL_SELECTION | SWT.MULTI);
		remoteDirectoryTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.KEYPAD_CR:
				case (int) '\r':
					(new OpenRemoteFileAction(UserExplorerComposite.this))
							.run();
					break;
				case SWT.F2:
					(new RenameRemoteFileAction(UserExplorerComposite.this))
							.run();
					break;
				case SWT.F5:
					reloadRemoteDirectoryTable();
					break;
				case SWT.DEL:
					(new RemoveRemoteFileAction(UserExplorerComposite.this))
							.run();
				case SWT.ESC:
					remoteDirectoryTable.deselectAll();
				default:
					break;
				}

				JLG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");

			}
		});
		remoteDirectoryTable.addMenuDetectListener(new MenuDetectListener() {
			public void menuDetected(MenuDetectEvent e) {
				JLG.debug("opening context menu");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu.createContextMenu(shell);

				int sel = remoteDirectoryTable.getSelection().length;
				if (sel > 0) {
					OpenRemoteFileAction openRemoteFileAction = new OpenRemoteFileAction(
							UserExplorerComposite.this);
					myMenu.add(openRemoteFileAction);

					myMenu.add(new Separator());
					myMenu.add(new CheckOutAction(UserExplorerComposite.this));
					myMenu.add(new Separator());
					myMenu.add(new RemoveRemoteFileAction(
							UserExplorerComposite.this));
					RenameRemoteFileAction renameremoteFileAction = new RenameRemoteFileAction(
							UserExplorerComposite.this);
					myMenu.add(renameremoteFileAction);

					if (sel > 1) {
						openRemoteFileAction.setEnabled(false);
						renameremoteFileAction.setEnabled(false);
					}
				}
				if (sel == 0) {
					myMenu.add(new CreateNewRemoteDirAction(
							UserExplorerComposite.this));
				}
				menu.setEnabled(true);
				myMenu.setVisible(true);
				menu.setVisible(true);
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

			@Override
			public void mouseDown(MouseEvent e) {
				JLG.debug("mouse down");
				Point pt = new Point(e.x, e.y);
				if (remoteDirectoryTable.getItem(pt) == null) {
					JLG.debug("cancel selection");
					remoteDirectoryTable.deselectAll();
				}
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

		DragSource dragRemoteSource = new DragSource(remoteDirectoryTable,
				DND.DROP_MOVE | DND.DROP_COPY);
		dragRemoteSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					// event.data = localDirectoryTable.getSelection();
					event.data = new String("this is my fake data");
				}
			}

			@Override
			public void dragStart(DragSourceEvent event) {
				if (remoteDirectoryTable.getSelection().length == 0) {
					event.doit = false;
				}
			}
		});
		dragRemoteSource.setTransfer(types);

		DropTarget dropRemoteTarget = new DropTarget(remoteDirectoryTable,
				DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		dropRemoteTarget.setTransfer(types);
		dropRemoteTarget.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(DropTargetEvent event) {
				JLG.debug("drop");
				if (TextTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					String text = (String) event.data;
					JLG.debug("received from transfer: " + text);
					(new CommitAction(UserExplorerComposite.this)).run();
				}
			}
		});

	}

	protected void openRemoteFile(TableItem item) {
		try {
			String name = item.getText(0);
			if (name.equals(DIRECTORY_PARENT)) {
				currentRemoteDirString = currentRemoteDirString.substring(0,
						currentRemoteDirString.lastIndexOf("/"));
				if (currentRemoteDirString.equals("")) {
					currentRemoteDirString = "/";
				}
				reloadRemoteDirectoryTable();
			} else {
				if (item.getText(1).equals(DIRECTORY_TYPE)) {
					if (!currentRemoteDirString.endsWith("/")) {
						currentRemoteDirString += "/";
					}
					currentRemoteDirString += name;
				}
				reloadRemoteDirectoryTable();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void reloadRemoteDirectoryTable() {
		try {
			remoteDirectoryLabel.setText(currentRemoteDirString);
			// first make sure the table content is empty.
			remoteDirectoryTable.removeAll();

			TableItem parentTreetableItem = new TableItem(remoteDirectoryTable,
					SWT.NONE);
			parentTreetableItem.setText(new String[] { DIRECTORY_PARENT,
					DIRECTORY_TYPE, DIRECTORY_SIZE });
			parentTreetableItem.setImage(DIRECTORY_ICON);

			FileInterface currentDir = agent.getDir(user, currentRemoteDirString);
			if (currentDir == null) {
				return;
			}
			Collection<? extends FileInterface> set = currentDir.listFiles();
			// Create an array containing the elements in a set
			FileInterface[] array = (FileInterface[]) set.toArray(new FileInterface[set
					.size()]);
			// Order
			Arrays.sort(array, new Comparator<FileInterface>() {
				public int compare(FileInterface f1, FileInterface f2) {
					if (f1.isFile() && f2.isDirectory()) {
						return 1;
					} else if (f2.isFile() && f1.isDirectory()) {
						return -1;
					} else {
						return f1.getName().compareTo(f2.getName());
					}
				}
			});

			for (FileInterface te : array) {

				TableItem tableItem = new TableItem(remoteDirectoryTable,
						SWT.NONE);
				String type = null;
				String size = null;
				Image image = null;
				if (te.isDirectory()) {
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
		} catch (Exception e) {
			// TODO: handle exception
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

	public void deleteLocalFile(TableItem item) {
		String name = item.getText(0);
		String type = item.getText(1);
		if (name.equals(DIRECTORY_PARENT)) {
			QuickMessage.error(this.getShell(),
					"Cannot delete the parent directory.");
			return;
		}
		JLG.debug("type = " + type);
		JLG.rm(new File(currentLocalDirectory, name));
	}

	public void deleteRemoteFile(TableItem item) {

		String name = item.getText(0);
		try {
			// must work both for dir and file
			agent.rm(user, currentRemoteDirString, name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void renameLocalFile(String name, String text) {
		new File(currentLocalDirectory, name).renameTo(new File(
				currentLocalDirectory, text));

	}

	public void renameRemoteFile(String oldName, String newName) {
		try {
			agent.rename(user, currentRemoteDirString, oldName, newName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createNewLocalDir() {
		TableItem newDirItem = new TableItem(localDirectoryTable, SWT.NONE);
		newDirItem.setText(new String[] { DIRECTORY_NEW, DIRECTORY_TYPE,
				DIRECTORY_SIZE });
		newDirItem.setImage(DIRECTORY_ICON);
		try {
			JLG.mkdir(new File(currentLocalDirectory, DIRECTORY_NEW));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localDirectoryTable.setSelection(newDirItem);
		(new RenameFileAction(this)).run();

	}

	public void createNewRemoteDir() {
		TableItem newDirItem = new TableItem(remoteDirectoryTable, SWT.NONE);
		newDirItem.setText(new String[] { DIRECTORY_NEW, DIRECTORY_TYPE,
				DIRECTORY_SIZE });
		newDirItem.setImage(DIRECTORY_ICON);
		try {
			agent.mkdir(user, currentRemoteDirString, DIRECTORY_NEW);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		remoteDirectoryTable.setSelection(newDirItem);
		(new RenameRemoteFileAction(this)).run();

	}

}
