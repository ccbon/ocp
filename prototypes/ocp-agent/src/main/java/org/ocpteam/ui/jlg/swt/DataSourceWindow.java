package org.ocpteam.ui.jlg.swt;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.Context;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.layer.rsp.FileSystem;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.GraphicalUI;

public class DataSourceWindow extends ApplicationWindow {

	OpenDataSourceAction openDataSourceAction;
	CloseDataSourceAction closeDataSourceAction;
	Map<String, NewDataSourceAction> newDataSourceActionMap;
	ExitAction exitAction;

	SignInAction signInAction;
	SignOutAction signOutAction;

	public ViewExplorerAction viewExplorerAction;

	public DataSource ds;
	public Agent agent;
	public Context context;

	public CTabFolder tabFolder;
	private CTabItem explorerCTabItem;
	public ExplorerComposite explorerComposite;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		JLG.debug_on();
		try {
			DataSourceWindow window = new DataSourceWindow();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application window.
	 */
	public DataSourceWindow() {
		super(null);
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		refresh();
	}

	void refresh() {
		// action status
		closeDataSourceAction.setEnabled(ds != null);
		signInAction.setEnabled(ds != null && agent != null
				&& agent.usesAuthentication() && context == null);
		signOutAction.setEnabled(ds != null && agent != null
				&& agent.usesAuthentication() && context != null);
		viewExplorerAction.setEnabled(context != null);

		// other
		if (context == null && explorerComposite != null) {
			removeExplorer();
		}
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
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
		for (Enumeration<String> e = DataSource.protocolResource.getKeys(); e
				.hasMoreElements();) {
			String protocol = e.nextElement();
			newDataSourceActionMap.put(protocol, new NewDataSourceAction(this,
					protocol));
		}
		exitAction = new ExitAction(this);

		viewExplorerAction = new ViewExplorerAction(this);

		signInAction = new SignInAction(this);
		signOutAction = new SignOutAction(this);
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

		{
			MenuManager menuManager = new MenuManager("New");
			String[] protocols = (String[]) newDataSourceActionMap.keySet()
					.toArray(new String[newDataSourceActionMap.size()]);
			Arrays.sort(protocols);
			for (String protocol : protocols) {
				menuManager.add(newDataSourceActionMap.get(protocol));
			}

			fileMenu.add(menuManager);
		}
		fileMenu.add(new Separator());
		fileMenu.add(openDataSourceAction);
		fileMenu.add(closeDataSourceAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager userMenu = new MenuManager("&User");
		menuBar.add(userMenu);
		userMenu.add(signInAction);
		userMenu.add(signOutAction);

		MenuManager viewMenu = new MenuManager("&View");
		menuBar.add(viewMenu);
		viewMenu.add(viewExplorerAction);

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

		toolBarManager.add(new Separator());

		toolBarManager.add(viewExplorerAction);

		toolBarManager.add(new Separator());

		toolBarManager.add(signInAction);
		toolBarManager.add(signOutAction);

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
		newShell.setImage(SWTResourceManager.getImage(GraphicalUI.class,
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
		if (!(context.dataModel instanceof FileSystem)) {
			throw new Exception("not filesystem");
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
			explorerCTabItem.setText("Explorer");

			explorerComposite = new ExplorerComposite(tabFolder, SWT.NONE, this);
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
			if (agent != null && (!agent.isOnlyClient())) {
				// only hide it.
				getShell().setVisible(false);
				return true;
			}
			if (exitAction.isFirstRun) {
				exitAction.run();
				if (exitAction.wantToExit == false) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.close();
	}

	public void openDataSource() throws Exception {
		try {
			agent = ds.getAgent();
			agent.connect();
			context = agent.getInitialContext();
			if (context != null) {
				viewExplorerAction.run();
			} else if (agent.usesAuthentication()) {
				try {
					String[] array = ds.getURI().getUserInfo().split(":");
					String username = array[0];
					String password = array[1];
					signIn(username, password);
				} catch (Exception e) {
					signInAction.run();
				}

			}
		} catch (Exception e) {
			throw QuickMessage.exception(getShell(), "Error when opening data source: ", e);
		}
	}

	public void signIn(String login, Object challenge) throws Exception {
		agent.login(login, challenge);
		context = agent.getInitialContext();
		if (context != null) {
			viewExplorerAction.run();
		}

	}

	public void signOut() throws Exception {
		context = null;
		agent.logout(null);
	}
}
