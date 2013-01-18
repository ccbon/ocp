package org.ocpteam.ui.swt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.component.Server;
import org.ocpteam.component.TCPListener;
import org.ocpteam.component.UDPListener;
import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.interfaces.IUserCreation;
import org.ocpteam.interfaces.IUserManagement;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.action.AboutAction;
import org.ocpteam.ui.swt.action.CheckOutAction;
import org.ocpteam.ui.swt.action.CloseDataSourceAction;
import org.ocpteam.ui.swt.action.CommitAction;
import org.ocpteam.ui.swt.action.CopyAction;
import org.ocpteam.ui.swt.action.EditPreferencesAction;
import org.ocpteam.ui.swt.action.ExitAction;
import org.ocpteam.ui.swt.action.NewDataSourceAction;
import org.ocpteam.ui.swt.action.NewUserAction;
import org.ocpteam.ui.swt.action.OpenConsoleAction;
import org.ocpteam.ui.swt.action.OpenDataSourceAction;
import org.ocpteam.ui.swt.action.PasteAction;
import org.ocpteam.ui.swt.action.SaveAsDataSourceAction;
import org.ocpteam.ui.swt.action.SaveDataSourceAction;
import org.ocpteam.ui.swt.action.SelectAllAction;
import org.ocpteam.ui.swt.action.SignInAction;
import org.ocpteam.ui.swt.action.SignOutAction;
import org.ocpteam.ui.swt.action.ViewDataModelAction;

public class DataSourceWindow extends ApplicationWindow implements IComponent {

	public static final int ON_DS_CLOSE = 0;

	private static final String TITLE = "Generic Data Source Explorer";

	public static final int EDIT_MODE = 1;
	public static final int NEW_MODE = 0;

	public static final String GDSE_DIR = System.getProperty("user.home")
			+ "/.gdse";
	private static final String PROPERTIES_FILENAME = GDSE_DIR
			+ "/gdse.properties";

	OpenDataSourceAction openDataSourceAction;
	public CloseDataSourceAction closeDataSourceAction;
	SaveDataSourceAction saveDataSourceAction;
	public SaveAsDataSourceAction saveAsDataSourceAction;
	Map<String, NewDataSourceAction> newDataSourceActionMap;
	ExitAction exitAction;

	SignInAction signInAction;
	SignOutAction signOutAction;
	NewUserAction newUserAction;

	SelectAllAction selectAllAction;
	CopyAction copyAction;
	PasteAction pasteAction;
	CommitAction commitAction;
	CheckOutAction checkOutAction;
	EditPreferencesAction preferenceAction;

	public ViewDataModelAction viewExplorerAction;

	DebugAction debugAction;

	AboutAction aboutAction;
	HelpAction helpAction;

	public DataSource ds;
	public Context context;

	public CTabFolder tabFolder;
	private CTabItem explorerCTabItem;
	public Composite explorerComposite;

	public Clipboard clipboard;
	private DynamicMenuManager protocolMenu;
	private TrayItem item;
	private Map<Integer, List<Listener>> listenerMap;
	public IContainer app;
	public DataSourceFactory dsf;
	public MyPreferenceStore ps;

	MenuManager menuManager;

	private int editionMode;

	/**
	 * Create the application window.
	 */
	public DataSourceWindow() {
		super(null);
	}

	public void init() throws Exception {
		this.dsf = app.getComponent(DataSourceFactory.class);
		initPreferenceStore();

		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		refresh();
		listenerMap = new HashMap<Integer, List<Listener>>();
	}

	public class MyPreferenceStore extends PreferenceStore {
		public DataSourceWindow w;
		private String filename;

		MyPreferenceStore(String filename, DataSourceWindow w) {
			super(filename);
			this.w = w;
			this.filename = filename;
		}

		@Override
		public void save() throws IOException {
			String[] keys = preferenceNames();
			Arrays.sort(keys);
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename));
			try {
				for (String key : keys) {
					out.write(StringEscapeUtils.escapeJava(key + "=" + getString(key)) + JLG.NL);
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
			super.load();
		}

	}

	private void initPreferenceStore() throws Exception {
		File file = new File(PROPERTIES_FILENAME);
		if (!file.exists()) {

			InputStream is = this.getClass().getResourceAsStream(
					"preferences.properties");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				byte[] array = new byte[1024];
				int l = 0;
				while ((l = is.read(array)) != -1) {
					fos.write(array, 0, l);
				}
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
		ps = new MyPreferenceStore(PROPERTIES_FILENAME, this);
		ps.load();
	}

	public void refresh() {
		// action status
		boolean bAuth = ds != null && ds.usesComponent(IUserManagement.class);
		closeDataSourceAction.setEnabled(ds != null);
		saveDataSourceAction.setEnabled(ds != null);
		saveAsDataSourceAction.setEnabled(ds != null);
		signInAction.setEnabled(bAuth && context == null);
		signOutAction.setEnabled(bAuth && context != null);
		newUserAction.setEnabled(bAuth && context == null
				&& ds.usesComponent(IUserCreation.class));

		viewExplorerAction.setEnabled(context != null);

		// other
		if (context == null && explorerComposite != null) {
			removeExplorer();
		}

		// menu edit
		copyAction.setEnabled(copyAction.canRun());
		pasteAction.setEnabled(pasteAction.canRun());
		commitAction.setEnabled(commitAction.canRun());
		checkOutAction.setEnabled(checkOutAction.canRun());

		// status line
		String status = "";
		if (ds != null) {
			String username = "";
			if (ds.usesComponent(IUserManagement.class) && context != null) {
				IUserManagement um = ds.getComponent(IUserManagement.class);
				try {
					username = " | User: " + um.getUsername();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			status = "Protocol: " + ds.getProtocolName() + username;
		}
		setStatus(status);

		// Shell title
		String prefix = "";
		if (ds != null && !ds.isNew()) {
			prefix = ds.getFile().getName() + " - ";
		}
		String title = prefix + TITLE;
		Shell shell = getShell();
		if (shell != null) {
			shell.setText(title);
		}
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {

		clipboard = new Clipboard(parent.getDisplay());

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new CTabFolder(container, SWT.BORDER);
		// tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setSimple(false);

		CTabItem tbtmWelcome = new CTabItem(tabFolder, SWT.NONE);
		tbtmWelcome.setShowClose(true);
		tbtmWelcome.setText("Welcome");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		tbtmWelcome.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		Browser browser = new Browser(composite, SWT.NONE);
		String html = "You can press 'F1' to get help.";
		browser.setText(html);

		tabFolder.setSelection(tbtmWelcome);

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		openDataSourceAction = new OpenDataSourceAction(this);
		closeDataSourceAction = new CloseDataSourceAction(this);
		newDataSourceActionMap = new HashMap<String, NewDataSourceAction>();
		for (DataSource ds : dsf.getDataSourceList()) {
			String protocol = ds.getProtocolName();
			LOG.debug("ds=" + ds.getClass());
			newDataSourceActionMap.put(protocol, new NewDataSourceAction(this,
					protocol));
		}
		saveDataSourceAction = new SaveDataSourceAction(this);
		saveAsDataSourceAction = new SaveAsDataSourceAction(this);

		exitAction = new ExitAction(this);

		selectAllAction = new SelectAllAction(this);
		copyAction = new CopyAction(this);
		pasteAction = new PasteAction(this);
		commitAction = new CommitAction(this);
		checkOutAction = new CheckOutAction(this);
		preferenceAction = new EditPreferencesAction(this);

		viewExplorerAction = new ViewDataModelAction(this);

		debugAction = new DebugAction();

		signInAction = new SignInAction(this);
		signOutAction = new SignOutAction(this);
		newUserAction = new NewUserAction(this);

		helpAction = new HelpAction(this);
		aboutAction = new AboutAction(this);
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuBar = new MenuManager("menu");
		MenuManager fileMenu = new MenuManager("&File");
		menuBar.add(fileMenu);

		menuManager = new MenuManager("New");
		refreshNewMenuManager();

		fileMenu.add(menuManager);

		fileMenu.add(new Separator());
		fileMenu.add(openDataSourceAction);
		fileMenu.add(closeDataSourceAction);
		fileMenu.add(saveDataSourceAction);
		fileMenu.add(saveAsDataSourceAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager editMenu = new MenuManager("&Edit");
		editMenu.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager arg0) {
				DataSourceWindow.this.refresh();
			}
		});
		menuBar.add(editMenu);
		editMenu.add(selectAllAction);
		editMenu.add(new Separator());
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.add(new Separator());
		editMenu.add(commitAction);
		editMenu.add(checkOutAction);
		editMenu.add(new Separator());
		editMenu.add(preferenceAction);

		MenuManager userMenu = new MenuManager("&User");
		menuBar.add(userMenu);
		userMenu.add(signInAction);
		userMenu.add(signOutAction);
		editMenu.add(new Separator());
		userMenu.add(newUserAction);

		MenuManager viewMenu = new MenuManager("&View");
		menuBar.add(viewMenu);
		viewMenu.add(viewExplorerAction);

		MenuManager testMenu = new MenuManager("&Test");
		menuBar.add(testMenu);
		testMenu.add(debugAction);

		MenuManager helpMenu = new MenuManager("&Help", "&Help");
		menuBar.add(helpMenu);
		helpMenu.add(helpAction);
		helpMenu.add(aboutAction);

		return menuBar;
	}

	public void refreshNewMenuManager() {
		menuManager.removeAll();
		List<DataSource> list = dsf.getDataSourceOrderedList();
		for (DataSource ds : list) {
			String protocol = ds.getProtocolName();
			LOG.debug("ds=" + ds.getClass());
			if (ps.getBoolean("ds." + protocol)) {
				menuManager.add(newDataSourceActionMap.get(protocol));
			}
		}

	}

	/**
	 * Create the toolbar manager.
	 * 
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		toolBarManager.add(openDataSourceAction);
		toolBarManager.add(closeDataSourceAction);
		toolBarManager.add(saveDataSourceAction);
		toolBarManager.add(saveAsDataSourceAction);

		toolBarManager.add(new Separator());

		toolBarManager.add(viewExplorerAction);

		toolBarManager.add(new Separator());

		toolBarManager.add(signInAction);
		toolBarManager.add(signOutAction);
		toolBarManager.add(newUserAction);

		toolBarManager.add(new Separator());

		toolBarManager.add(helpAction);
		toolBarManager.add(aboutAction);

		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(new Point(600, 600));
		newShell.setImage(SWTResourceManager.getImage(DataSourceWindow.class,
				"ocp_icon.png"));
		super.configureShell(newShell);
		newShell.setText(TITLE);
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public void viewDataModel() throws Exception {
		LOG.debug("viewDataModel");
		if (context == null) {
			throw new Exception("missing context");
		}
		IDataModel dm = context.getDataModel();
		LOG.debug("dm=" + dm);

		if (explorerCTabItem == null) {
			explorerCTabItem = new CTabItem(tabFolder, SWT.NONE);
			explorerCTabItem.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent arg0) {
					LOG.debug("on dispose event");
					explorerCTabItem = null;
				}
			});

			explorerCTabItem.setShowClose(true);

			if (dm instanceof IFileSystem) {
				explorerCTabItem.setText("Explorer");
				explorerComposite = new ExplorerComposite(tabFolder, SWT.NONE,
						this);
			} else if (dm instanceof IMapDataModel) {
				explorerCTabItem.setText("Map");
				explorerComposite = new MapComposite(tabFolder, SWT.NONE, this);
			} else {
				throw new Exception("datamodel not understood");
			}

			explorerCTabItem.setControl(explorerComposite);

		}
		tabFolder.setSelection(explorerCTabItem);

		LOG.debug("viewExplorer done.");

	}

	private void removeExplorer() {
		if (explorerCTabItem != null && (!explorerCTabItem.isDisposed())) {
			explorerCTabItem.dispose();
			explorerCTabItem = null;
		}
		explorerComposite = null;
	}

	@Override
	public boolean close() {
		try {
			if (isDaemon()) {
				// only hide it.
				getShell().setVisible(false);
				return true;
			}
			if (exitAction.confirm()) {
				return exitAction.exit();
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.close();
	}

	private boolean isDaemon() {
		if (ps.getBoolean(GeneralPreferencePage.NEVER_STICKY)) {
			return false;
		}
		if (ds != null && ds.usesComponent(Server.class)) {
			return ds.getComponent(Server.class).isStarted();
		} else {
			return false;
		}
	}

	public boolean exit() {
		try {
			if (ds != null) {
				closeDataSource();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.close();
	}

	public void openDataSource(DataSource ds) throws Exception {
		try {
			LOG.debug("datasource=" + ds);
			this.ds = ds;
			manageNATTraversal();
			ds.readConfig();
			LOG.debug("About to connect");
			ds.connect();
			LOG.debug("Connected: " + ds.isConnected());
			refresh();
			addProtocolMenu();
			if (isDaemon()) {
				openTray();
			}
			context = ds.getContext();
			if (context != null) {
				viewExplorerAction.run();
			} else if (ds.usesComponent(IUserManagement.class)) {
				ds.getComponent(IUserManagement.class).initFromURI();
				if (ds.getComponent(IUserManagement.class)
						.canAutomaticallyLogin()) {
					signIn();
				} else {
					signInAction.run();
				}
			}
		} catch (Exception e) {
			throw QuickMessage.exception(getShell(),
					"Error when opening data source: ", e);
		}
	}

	private void manageNATTraversal() {
		if (ps.getBoolean(GeneralPreferencePage.NO_NAT_TRAVERSAL)) {
			LOG.debug("Removing NATTraversal");
			if (ds.usesComponent(TCPListener.class)) {
				ds.getComponent(TCPListener.class).removeComponent(
						NATTraversal.class);
			}
			if (ds.usesComponent(UDPListener.class)) {
				ds.getComponent(UDPListener.class).removeComponent(
						NATTraversal.class);
			}
		}
	}

	public void closeDataSource() throws Exception {
		LOG.debug("Close datasource");
		ds.disconnect();
		ds.close();
		if (isDaemon()) {
			closeTray();
		}
		ds = null;
		context = null;
		removeProtocolMenu();
		Event event = new Event();
		if (listenerMap.containsKey(ON_DS_CLOSE)) {
			Iterator<Listener> it = listenerMap.get(ON_DS_CLOSE).iterator();
			while (it.hasNext()) {
				it.next().handleEvent(event);
			}
		}
	}

	private void closeTray() {
		if (item != null) {
			if (!item.isDisposed()) {
				item.dispose();
			}
			item = null;
		}
	}

	private void openTray() {
		Display display = getShell().getDisplay();
		Shell shell = new Shell(display);
		Tray tray = display.getSystemTray();
		if (tray == null) {
			return;
		}
		item = new TrayItem(tray, SWT.NONE);
		Image image = new Image(display,
				DataSourceWindow.class.getResourceAsStream("ocp_icon.png"));
		item.setImage(image);
		item.setToolTipText("OCP Agent");
		final MenuManager myMenu = new MenuManager("xxx");
		final Menu menu = myMenu.createContextMenu(shell);
		myMenu.add(new ExitAction(this));
		myMenu.add(new OpenConsoleAction(this));
		menu.setEnabled(true);

		item.addListener(SWT.MenuDetect, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				LOG.debug("coucou");
				myMenu.setVisible(true);
				menu.setVisible(true);
			}
		});

		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				LOG.debug("item default selected");
				new OpenConsoleAction(DataSourceWindow.this).run();
			}
		});
	}

	public void addProtocolMenu() {
		protocolMenu = null;
		if (ds instanceof DSPDataSource) {
			protocolMenu = new DSPMenuManager(ds.getProtocolName()
					.toUpperCase(), "protocolMenu");
			protocolMenu.setParent(this);
			protocolMenu.init();
		}
		try {
			ResourceBundle rb = ds.getResource("swt");
			DynamicMenuManager menu = (DynamicMenuManager) rb.getObject("menu");
			menu.setParent(this);
			menu.init();
			protocolMenu = menu;
		} catch (Exception e) {
			// e.printStackTrace();
			LOG.debug("no specific menu for " + ds.getProtocolName());
		}
		if (protocolMenu != null) {
			MenuManager menuBar = getMenuBarManager();
			menuBar.insertBefore("&Help", protocolMenu);
			menuBar.updateAll(true);
		}
	}

	public void removeProtocolMenu() {
		if (protocolMenu != null) {
			MenuManager menuBar = getMenuBarManager();
			menuBar.remove(protocolMenu);
			menuBar.updateAll(true);
		}
		protocolMenu = null;
	}

	public void signIn() throws Exception {
		if (ds.usesComponent(IUserManagement.class)) {
			ds.getComponent(IUserManagement.class).login();
		}

		context = ds.getContext();
		if (context != null) {
			viewExplorerAction.run();
		}
	}

	public void signOut() throws Exception {
		context = null;
		if (ds.usesComponent(IUserManagement.class)) {
			ds.getComponent(IUserManagement.class).logout();
		}
		ds.setContext(null);
	}

	public String getHelpURL() {
		return "http://code.google.com/p/ocp/wiki/Help";
	}

	public void addListener(int eventType, Listener listener) {
		if (listenerMap.containsKey(eventType)) {
			listenerMap.get(eventType).add(listener);
		} else {
			List<Listener> list = new LinkedList<Listener>();
			list.add(listener);
			listenerMap.put(eventType, list);
		}
	}

	@Override
	public void setParent(IContainer parent) {
		this.app = parent;

	}

	@Override
	public IContainer getParent() {
		return this.app;
	}

	public void start() throws Exception {
		JLG.mkdir(GDSE_DIR);
		LOG.checkInit();
		init();
		refreshPreference();
		setBlockOnOpen(true);
		try {
			open();
		} catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null,
					"Cannot run the GUI: SWT version not compliant with JRE version. e = "
							+ e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		Display.getCurrent().dispose();
	}

	@Override
	public IContainer getRoot() {
		return this.app.getRoot();
	}

	public void setDSEditionMode(int mode) {
		this.editionMode = mode;
	}

	public int getDSEditMode() {
		return this.editionMode;
	}

	public void refreshPreference() {
		try {
			LOG.logInFile(ps.getString(GeneralPreferencePage.LOGFILE));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
