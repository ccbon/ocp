package org.ocpteam.ui.swt.composite;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

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
import org.eclipse.swt.dnd.FileTransfer;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.action.CancelAction;
import org.ocpteam.ui.swt.action.CheckOutAction;
import org.ocpteam.ui.swt.action.CommitAction;
import org.ocpteam.ui.swt.action.CreateNewDirAction;
import org.ocpteam.ui.swt.action.CreateNewRemoteDirAction;
import org.ocpteam.ui.swt.action.OpenFileAction;
import org.ocpteam.ui.swt.action.OpenRemoteFileAction;
import org.ocpteam.ui.swt.action.RemoveFileAction;
import org.ocpteam.ui.swt.action.RemoveRemoteFileAction;
import org.ocpteam.ui.swt.action.RenameFileAction;
import org.ocpteam.ui.swt.action.RenameRemoteFileAction;
import org.ocpteam.win32.WindowsKernel32;

import com.sun.jna.Platform;

public class ExplorerComposite extends Composite {
	private static final String DIRECTORY_SIZE = "";
	private static final String DIRECTORY_TYPE = "Directory";
	private static final String DIRECTORY_PARENT = "..";
	private static final String DIRECTORY_NEW = "New Folder";
	private static final String FILE_TYPE = "File";
	private static final Image DIRECTORY_ICON = SWTResourceManager.getImage(
			DataSourceWindow.class, "directory.png");
	private static final Image FILE_ICON = SWTResourceManager.getImage(
			DataSourceWindow.class, "file.png");

	public Table localDirectoryTable;
	public Table remoteDirectoryTable;
	public File currentLocalDirectory;
	public String currentRemoteDirString;

	public IFileSystem fs;
	private Label localDirectoryLabel;
	private Label remoteDirectoryLabel;
	private DataSourceWindow w;
	public Table table;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ExplorerComposite(Composite parent, int style, DataSourceWindow w) {
		super(parent, style);
		this.w = w;
		this.fs = (IFileSystem) w.context.getDataModel();

		currentLocalDirectory = new File(fs.getDefaultLocalDir());
		currentRemoteDirString = "/";

		setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm verticalSashForm = new SashForm(this, SWT.VERTICAL);

		SashForm sashForm = new SashForm(verticalSashForm, SWT.NONE);

		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_leftComposite = new GridLayout(1, false);
		gl_leftComposite.marginTop = 5;
		gl_leftComposite.marginHeight = 0;
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
				case '\r':
					(new OpenFileAction(ExplorerComposite.this)).run();
					break;
				case SWT.F2:
					(new RenameFileAction(ExplorerComposite.this)).run();
					break;
				case SWT.F5:
					reloadLocalDirectoryTable();
					break;
				case SWT.DEL:
					(new RemoveFileAction(ExplorerComposite.this)).run();
					break;
				case SWT.ESC:
					localDirectoryTable.deselectAll();
					break;
				default:
				}
				LOG.info("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
				ExplorerComposite.this.w.refresh();
			}
		});
		localDirectoryTable.addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent arg0) {
				LOG.info("opening context menu");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu
						.createContextMenu(ExplorerComposite.this.w.getShell());

				int sel = localDirectoryTable.getSelection().length;
				if (sel > 0) {
					OpenFileAction openFileAction = new OpenFileAction(
							ExplorerComposite.this);
					myMenu.add(openFileAction);

					myMenu.add(new Separator());
					CommitAction commitAction = new CommitAction(
							ExplorerComposite.this.w);
					myMenu.add(commitAction);
					myMenu.add(new Separator());
					RemoveFileAction removeAction = new RemoveFileAction(
							ExplorerComposite.this);
					myMenu.add(removeAction);
					RenameFileAction renameFileAction = new RenameFileAction(
							ExplorerComposite.this);
					myMenu.add(renameFileAction);
					LOG.info("array.lenght=" + sel);
					if (sel > 1) {
						openFileAction.setEnabled(false);
						renameFileAction.setEnabled(false);
					}
					if (sel == 1) {
						String name = localDirectoryTable.getSelection()[0]
								.getText(0);
						if (name.equals(DIRECTORY_PARENT)) {
							renameFileAction.setEnabled(false);
							commitAction.setEnabled(false);
							removeAction.setEnabled(false);
						}
					}
				}
				if (sel == 0) {
					myMenu.add(new CreateNewDirAction(ExplorerComposite.this));
				}
				menu.setEnabled(true);
				myMenu.setVisible(true);
				menu.setVisible(true);

			}
		});
		localDirectoryTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				LOG.info("double click");
				TableItem[] items = localDirectoryTable.getSelection();
				if (items.length == 1) {
					TableItem item = items[0];
					openLocalFile(item);
				}
				LOG.info("table item:" + e.widget.getClass());
			}

			@Override
			public void mouseDown(MouseEvent e) {
				LOG.info("mouse down");
				Point pt = new Point(e.x, e.y);
				if (localDirectoryTable.getItem(pt) == null) {
					LOG.info("cancel selection");
					localDirectoryTable.deselectAll();
				}
				ExplorerComposite.this.w.refresh();
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

		localDND();

		reloadLocalDirectoryTable();

		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		GridLayout gl_rightComposite = new GridLayout(1, false);
		gl_rightComposite.marginHeight = 0;
		gl_rightComposite.marginTop = 5;
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
				case '\r':
					(new OpenRemoteFileAction(ExplorerComposite.this)).run();
					break;
				case SWT.F2:
					(new RenameRemoteFileAction(ExplorerComposite.this)).run();
					break;
				case SWT.F5:
					reloadRemoteDirectoryTable();
					break;
				case SWT.DEL:
					(new RemoveRemoteFileAction(ExplorerComposite.this)).run();
				case SWT.ESC:
					remoteDirectoryTable.deselectAll();
				default:
					break;
				}

				LOG.info("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
				ExplorerComposite.this.w.refresh();
			}
		});
		remoteDirectoryTable.addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				LOG.info("opening context menu");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu
						.createContextMenu(ExplorerComposite.this.w.getShell());

				int sel = remoteDirectoryTable.getSelection().length;
				if (sel > 0) {
					OpenRemoteFileAction openRemoteFileAction = new OpenRemoteFileAction(
							ExplorerComposite.this);
					myMenu.add(openRemoteFileAction);

					myMenu.add(new Separator());
					CheckOutAction checkoutAction = new CheckOutAction(
							ExplorerComposite.this.w);
					myMenu.add(checkoutAction);
					myMenu.add(new Separator());
					RemoveRemoteFileAction removeAction = new RemoveRemoteFileAction(
							ExplorerComposite.this);
					myMenu.add(removeAction);
					RenameRemoteFileAction renameremoteFileAction = new RenameRemoteFileAction(
							ExplorerComposite.this);
					myMenu.add(renameremoteFileAction);

					if (sel > 1) {
						openRemoteFileAction.setEnabled(false);
						renameremoteFileAction.setEnabled(false);
					}
					if (sel == 1) {
						String name = remoteDirectoryTable.getSelection()[0]
								.getText(0);
						if (name.equals(DIRECTORY_PARENT)) {
							renameremoteFileAction.setEnabled(false);
							checkoutAction.setEnabled(false);
							removeAction.setEnabled(false);
						}
					}
				}
				if (sel == 0) {
					myMenu.add(new CreateNewRemoteDirAction(
							ExplorerComposite.this));
				}
				menu.setEnabled(true);
				myMenu.setVisible(true);
				menu.setVisible(true);
			}
		});
		remoteDirectoryTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				LOG.info("double click");
				TableItem[] items = remoteDirectoryTable.getSelection();
				LOG.info("length=" + items.length);
				if (items.length == 1) {
					LOG.info("length=1");
					TableItem item = items[0];
					openRemoteFile(item);
				}
				LOG.info("table item:" + e.widget.getClass());

			}

			@Override
			public void mouseDown(MouseEvent e) {
				LOG.info("mouse down");
				Point pt = new Point(e.x, e.y);
				if (remoteDirectoryTable.getItem(pt) == null) {
					LOG.info("cancel selection");
					remoteDirectoryTable.deselectAll();
				}
				ExplorerComposite.this.w.refresh();
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

		remoteDND();
		reloadRemoteDirectoryTable();
		sashForm.setWeights(new int[] { 1, 1 });

		Composite composite = new Composite(verticalSashForm, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		TableColumn tblclmnSize = new TableColumn(table, SWT.NONE);
		tblclmnSize.setWidth(33);
		tblclmnSize.setText("Size");

		TableColumn tblclmnUd = new TableColumn(table, SWT.NONE);
		tblclmnUd.setWidth(33);
		tblclmnUd.setText("U/D");

		TableColumn tblclmnProgress = new TableColumn(table, SWT.NONE);
		tblclmnProgress.setWidth(135);
		tblclmnProgress.setText("Progress");

		TableColumn tblclmnSpeed = new TableColumn(table, SWT.NONE);
		tblclmnSpeed.setWidth(50);
		tblclmnSpeed.setText("Speed");

		TableColumn tblclmnElapsed = new TableColumn(table, SWT.NONE);
		tblclmnElapsed.setWidth(58);
		tblclmnElapsed.setText("Elapsed");

		TableColumn tblclmnStarted = new TableColumn(table, SWT.NONE);
		tblclmnStarted.setWidth(58);
		tblclmnStarted.setText("Started");

		TableColumn tblclmnEstimated = new TableColumn(table, SWT.NONE);
		tblclmnEstimated.setWidth(65);
		tblclmnEstimated.setText("Estimated");

		// TableItem item1 = new TableItem(table, SWT.NONE);
		// item1.setText(new String[] { "toto.txt", "10B", "U", "", "12ko/s",
		// "1s", "~1s" });
		// TableEditor editor = new TableEditor(table);
		// ProgressBar bar = new ProgressBar(table, SWT.SMOOTH);
		// bar.setMaximum(100);
		// bar.setSelection(50);
		// editor.grabHorizontal = true;
		// editor.horizontalAlignment = SWT.LEFT;
		// editor.setEditor(bar, item1, 3);

		verticalSashForm.setWeights(new int[] { 231, 66 });

		table.addMenuDetectListener(new MenuDetectListener() {

			@Override
			public void menuDetected(MenuDetectEvent e) {
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu
						.createContextMenu(ExplorerComposite.this.w.getShell());

				int sel = table.getSelection().length;
				if (sel > 0) {
					CancelAction cancelAction = new CancelAction(
							ExplorerComposite.this.w);
					myMenu.add(cancelAction);
					myMenu.add(new Separator());
				}
				menu.setEnabled(true);
				myMenu.setVisible(true);
				menu.setVisible(true);
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				LOG.info("mouse down");
				Point pt = new Point(e.x, e.y);
				if (table.getItem(pt) == null) {
					LOG.info("cancel selection");
					table.deselectAll();
				}
			}
		});
	}

	private void remoteDND() {
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
		Transfer[] types = new Transfer[] { TextTransfer.getInstance(),
				FileTransfer.getInstance() };

		dragRemoteSource.setTransfer(types);

		DropTarget dropRemoteTarget = new DropTarget(remoteDirectoryTable,
				DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		dropRemoteTarget.setTransfer(types);
		dropRemoteTarget.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(DropTargetEvent event) {
				LOG.info("drop");
				if (TextTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					String text = (String) event.data;
					LOG.info("received from transfer: " + text);
					(new CommitAction(ExplorerComposite.this.w)).run();
				} else if (FileTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					commitFiles((String[]) event.data);
				}
			}
		});

	}

	private void localDND() {
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
		Transfer[] types = new Transfer[] { TextTransfer.getInstance(),
				FileTransfer.getInstance() };

		dragLocalSource.setTransfer(types);

		DropTarget dropLocalTarget = new DropTarget(localDirectoryTable,
				DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		dropLocalTarget.setTransfer(types);
		dropLocalTarget.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(DropTargetEvent event) {
				LOG.info("drop");
				if (TextTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					String text = (String) event.data;
					LOG.info("received from transfer: " + text);
					(new CheckOutAction(ExplorerComposite.this.w)).run();
				} else if (FileTransfer.getInstance().isSupportedType(
						event.currentDataType)) {
					copyFiles((String[]) event.data);
				}
			}
		});

	}

	public void openRemoteFile(TableItem item) {
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
					reloadRemoteDirectoryTable();
				}
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

			IFile currentDir = fs.getFile(currentRemoteDirString);
			if (currentDir == null) {
				return;
			}
			Collection<? extends IFile> set = currentDir.listFiles();
			// Create an array containing the elements in a set
			IFile[] array = set.toArray(new IFile[set.size()]);
			// Order
			Arrays.sort(array, new Comparator<IFile>() {
				@Override
				public int compare(IFile f1, IFile f2) {
					if (f1.isFile() && f2.isDirectory()) {
						return 1;
					} else if (f2.isFile() && f1.isDirectory()) {
						return -1;
					} else {
						return f1.getName().compareTo(f2.getName());
					}
				}
			});

			for (IFile ifile : array) {

				TableItem tableItem = new TableItem(remoteDirectoryTable,
						SWT.NONE);
				String type = null;
				String size = null;
				Image image = null;
				if (ifile.isDirectory()) {
					type = DIRECTORY_TYPE;
					size = "";
					image = DIRECTORY_ICON;
				} else {
					type = FILE_TYPE;
					size = formatSize(ifile.getSize());
					image = FILE_ICON;
				}
				tableItem.setText(new String[] { ifile.getName(), type, size });
				tableItem.setImage(image);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void openLocalFile(TableItem item) {
		String name = item.getText(0);
		String type = item.getText(1);
		LOG.info("type = " + type);
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
			parentDirtableItem.setImage(DIRECTORY_ICON);
		}

		File[] children = null;
		LOG.info("currentLocalDirectory=" + currentLocalDirectory);
		if (currentLocalDirectory != null) {
			children = currentLocalDirectory.listFiles();
			if (Platform.isWindows()) {
				children = windowsFilter(children);
			}
		} else {
			children = File.listRoots();
		}

		Arrays.sort(children, new Comparator<File>() {
			@Override
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
			LOG.info("filename: " + f.getAbsolutePath());
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
				size = formatSize(f.length());
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

	private String formatSize(long size) {
		String result = null;
		if (size < 0) {
			return "N/A";
		}
		if (size > 1024 * 1024) {
			result = (size / (1024 * 1024)) + " MB";
		} else if (size > 1024) {
			result = (size / 1024) + " KB";
		} else {
			result = size + " B";
		}
		return result;
	}

	private File[] windowsFilter(File[] children) {
		try {
			Collection<File> c = new HashSet<File>();
			for (File f : children) {
				Path path = f.toPath();
				DosFileAttributes attr = Files.readAttributes(path,
						DosFileAttributes.class);
				if (attr.isHidden() || attr.isSystem()) {
					continue;
				}
				if (WindowsKernel32.isJunctionOrSymlink(f)) {
					continue;
				}
				c.add(f);
			}
			File[] result = c.toArray(new File[c.size()]);
			return result;
		} catch (Exception e) {
			return children;
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
		LOG.info("type = " + type);
		JLG.rm(new File(currentLocalDirectory, name));
	}

	public void deleteRemoteFile(TableItem item) {

		String name = item.getText(0);
		try {
			// must work both for dir and file
			fs.rm(currentRemoteDirString, name);
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
			fs.rename(currentRemoteDirString, oldName, newName);
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
			fs.mkdir(currentRemoteDirString, DIRECTORY_NEW);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		remoteDirectoryTable.setSelection(newDirItem);
		(new RenameRemoteFileAction(this)).run();

	}

	public void copyFiles(String[] data) {
		for (String path : data) {
			File origFile = new File(path);
			File destFile = new File(currentLocalDirectory, origFile.getName());
			Path origPath = FileSystems.getDefault().getPath(
					origFile.getAbsolutePath());
			Path destPath = FileSystems.getDefault().getPath(
					destFile.getAbsolutePath());

			try {
				Files.copy(origPath, destPath,
						StandardCopyOption.REPLACE_EXISTING);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reloadLocalDirectoryTable();
	}

	public void commitFiles(String[] data) {
		for (String path : data) {
			File file = new File(path);
			try {
				fs.commit(currentRemoteDirString, file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reloadRemoteDirectoryTable();
	}

	public void checkout(String[] data) {
		for (String path : data) {
			try {
				fs.checkout(currentRemoteDirString, path, currentLocalDirectory);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reloadLocalDirectoryTable();

	}
}
