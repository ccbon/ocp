package org.ocpteam.ui.swt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
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
import org.ocpteam.component.Agent;
import org.ocpteam.component.Authentication;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.component.MapDataModel;
import org.ocpteam.component.Server;
import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;

public class DataSourceWindow extends ApplicationWindow implements
		IComponent {

	public static final int ON_DS_CLOSE = 0;
	OpenDataSourceAction openDataSourceAction;
	CloseDataSourceAction closeDataSourceAction;
	SaveDataSourceAction saveDataSourceAction;
	SaveAsDataSourceAction saveAsDataSourceAction;
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

	public ViewExplorerAction viewExplorerAction;

	DebugAction debugAction;

	AboutAction aboutAction;
	HelpAction helpAction;

	public DataSource ds;
	public Agent agent;
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

	/**
	 * Create the application window.
	 */
	public DataSourceWindow() {
		super(null);
	}

	public void init() {
		this.dsf = app.getDesigner().get(DataSourceFactory.class);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		refresh();
		listenerMap = new HashMap<Integer, List<Listener>>();
	}

	void refresh() {
		// action status
		closeDataSourceAction.setEnabled(ds != null);
		saveDataSourceAction.setEnabled(ds != null);
		saveAsDataSourceAction.setEnabled(ds != null);
		signInAction.setEnabled(ds != null
				&& ds.getDesigner().uses(Authentication.class)
				&& context == null);
		signOutAction.setEnabled(ds != null
				&& ds.getDesigner().uses(Authentication.class)
				&& context != null);
		newUserAction.setEnabled(ds != null
				&& ds.getDesigner().uses(Authentication.class)
				&& context == null
				&& ds.getDesigner().get(Authentication.class)
						.allowsUserCreation());

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
			status = "Protocol: " + ds.getProtocol();
		}
		setStatus(status);
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
		Iterator<DataSource> it = dsf.getDataSourceIterator();
		while (it.hasNext()) {
			DataSource ds = it.next();
			String protocol = ds.getProtocol();
			JLG.debug("ds=" + ds.getClass());
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

		viewExplorerAction = new ViewExplorerAction(this);

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

		MenuManager menuManager = new MenuManager("New");
		String[] protocols = (String[]) newDataSourceActionMap.keySet()
				.toArray(new String[newDataSourceActionMap.size()]);
		Arrays.sort(protocols);
		for (String protocol : protocols) {
			menuManager.add(newDataSourceActionMap.get(protocol));
		}

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
		newShell.setText("Generic Data Source Explorer");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public void viewExplorer() throws Exception {
		JLG.debug("viewExplorer");
		if (context == null) {
			throw new Exception("missing context");
		}
		

		if (explorerCTabItem == null) {
			explorerCTabItem = new CTabItem(tabFolder, SWT.NONE);
			explorerCTabItem.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					JLG.debug("on dispose event");
					explorerCTabItem = null;
				}
			});

			explorerCTabItem.setShowClose(true);
			
			if (context.dataModel instanceof IFileSystem) {
				explorerCTabItem.setText("Explorer");
				explorerComposite = new ExplorerComposite(tabFolder, SWT.NONE, this);
			} else if (context.dataModel instanceof MapDataModel) {
				explorerCTabItem.setText("Map");
				explorerComposite = new MapComposite(tabFolder, SWT.NONE, this);
			} else {
				throw new Exception("datamodel not understood");
			}
			
			explorerCTabItem.setControl(explorerComposite);

		}
		tabFolder.setSelection(explorerCTabItem);

		JLG.debug("viewExplorer done.");

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
		if (ds != null && ds.getDesigner().uses(Server.class)) {
			return ds.getDesigner().get(Server.class).isStarted();
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
			JLG.debug("datasource=" + ds);
			this.ds = ds;
			ds.connect();
			addProtocolMenu();
			agent = ds.getDesigner().get(Agent.class);
			if (isDaemon()) {
				openTray();
			}
			context = ds.getContext();
			if (context != null) {
				viewExplorerAction.run();
			} else if (ds.getDesigner().uses(Authentication.class)) {
				ds.getDesigner().get(Authentication.class).initFromURI();
				if (ds.getDesigner().get(Authentication.class).canLogin()) {
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

	public void closeDataSource() throws Exception {
		ds.disconnect();
		ds.close();
		if (isDaemon()) {
			closeTray();
		}
		ds = null;
		agent = null;
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
		if (tray != null) {
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
					JLG.debug("coucou");
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
					JLG.debug("item default selected");
					new OpenConsoleAction(DataSourceWindow.this).run();
				}
			});

		}
	}

	public void addProtocolMenu() {
		protocolMenu = null;
		try {
			ResourceBundle rb = ds.getResource("swt");
			protocolMenu = (DynamicMenuManager) rb.getObject("menu");
			protocolMenu.setParent(this);
			protocolMenu.init();
		} catch (Exception e) {
			// e.printStackTrace();
			JLG.debug("no specific menu for " + ds.getProtocol());
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
		ds.getDesigner().get(Authentication.class).login();
		context = ds.getContext();
		if (context != null) {
			viewExplorerAction.run();
		}
	}

	public void signOut() throws Exception {
		context = null;
		ds.getDesigner().get(Authentication.class).logout();
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

	public void start() {
		init();
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	@Override
	public IContainer getRoot() {
		return this.app.getRoot();
	}



}
