package com.guenego.ocp.gui.console;

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

import com.guenego.misc.JLG;
import com.guenego.ocp.Agent;
import com.guenego.ocp.User;
import com.guenego.ocp.gui.GraphicalUI;

public class AdminConsole extends ApplicationWindow {

	private static final String NOT_CONNECTED_STATUS = "No user connected.";
	private static final String CONNECTED_STATUS = "User connected: ";

	public Agent agent;
	private User user;

	private Display display;
	public CTabFolder tabFolder;
	CTabItem contactCTabItem;
	private ContactComposite contactComposite;

	private CTabItem userCTabItem;
	private CTabItem userExplorerCTabItem;
		
	
	private NewUserAction newUserAction;
	private SignInAction signInAction;
	private SignOutAction signOutAction;
	private ExitAction exitAction;
	
	private ViewContactTabAction viewAdminTabAction;
	private ViewUserSyncTabAction viewUserSyncTabAction;
	private ViewUserExplorerTabAction viewUserExplorerTabAction;
	
	private RemoveStorageAction removeStorageAction;
	
	private HelpAction helpAction;
	private AboutAction aboutAction;
	
	
	/**
	 * Create the application window.
	 */
	public AdminConsole(Agent agent, Display display) {
		super(null);
		this.agent = agent;
		this.display = display;
		createActions();
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		// return super.close();
		getShell().setVisible(false);
		return true;
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);
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

		setUser(null);

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		exitAction = new ExitAction(agent, display);
		newUserAction = new NewUserAction(agent, display, this);
		signInAction = new SignInAction(agent, display, this);
		signOutAction = new SignOutAction(this);
		viewAdminTabAction = new ViewContactTabAction(this);
		removeStorageAction = new RemoveStorageAction(this);
		helpAction = new HelpAction();
		aboutAction = new AboutAction(display);
		viewUserSyncTabAction = new ViewUserSyncTabAction(this);
		viewUserExplorerTabAction = new ViewUserExplorerTabAction(this);
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
		fileMenu.add(signInAction);
		fileMenu.add(signOutAction);
		fileMenu.add(new Separator());
		fileMenu.add(newUserAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager viewMenu = new MenuManager("&View");
		menuBar.add(viewMenu);
		viewMenu.add(viewAdminTabAction);
		viewMenu.add(viewUserSyncTabAction);
		viewMenu.add(viewUserExplorerTabAction);

		MenuManager testMenu = new MenuManager("&Test");
		menuBar.add(testMenu);
		testMenu.add(removeStorageAction);

		MenuManager helpMenu = new MenuManager("&Help");
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
		toolBarManager.add(newUserAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(signInAction);
		toolBarManager.add(signOutAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(viewAdminTabAction);
		toolBarManager.add(viewUserSyncTabAction);
		toolBarManager.add(viewUserExplorerTabAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(removeStorageAction);
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
		newShell.setSize(new Point(500, 400));
		newShell.setImage(SWTResourceManager.getImage(GraphicalUI.class,
				"ocp_icon.png"));
		super.configureShell(newShell);
		newShell.setText("OCP Agent Console - " + agent.getName());
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(652, 514);
	}

	public void addUserExplorerTab() {
		if (userExplorerCTabItem == null) {
			userExplorerCTabItem = new CTabItem(tabFolder, SWT.NONE);
			userExplorerCTabItem.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					JLG.debug("on dispose event");
					userExplorerCTabItem = null;
				}
			});

			userExplorerCTabItem.setShowClose(true);
			userExplorerCTabItem.setText("Explorer");

			Composite composite = new UserExplorerComposite(tabFolder, SWT.NONE, agent,
					user);
			userExplorerCTabItem.setControl(composite);
			
		}
		tabFolder.setSelection(userExplorerCTabItem);
		
	}

	
	public void addUserSyncTab() {
		if (userCTabItem == null) {
			userCTabItem = new CTabItem(tabFolder, SWT.NONE);
			userCTabItem.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent arg0) {
					JLG.debug("on dispose event");
					userCTabItem = null;
				}
			});

			userCTabItem.setShowClose(true);
			userCTabItem.setText("Synchronize");

			Composite composite = new UserComposite(tabFolder, SWT.NONE, agent,
					user);
			userCTabItem.setControl(composite);
			
		}
		tabFolder.setSelection(userCTabItem);
	}

	public void removeUserTab() {
		if (userCTabItem != null && (!userCTabItem.isDisposed())) {
			userCTabItem.dispose();
			userCTabItem = null;
		}
		if (userExplorerCTabItem != null && (!userExplorerCTabItem.isDisposed())) {
			userExplorerCTabItem.dispose();
			userExplorerCTabItem = null;
		}
	}

	public void addContactTab() {
		// if one contact tab is already present, then just select it.
		if (contactCTabItem != null && contactCTabItem.isShowing()) {
			tabFolder.setSelection(contactCTabItem);
			contactComposite.refresh();
			return;
		}
		contactCTabItem = new CTabItem(tabFolder, SWT.NONE);
		contactCTabItem.setShowClose(true);
		contactCTabItem.setText("Contacts");
		contactCTabItem.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				JLG.debug("dispose");
				contactCTabItem = null;
			}
		});

		contactComposite = new ContactComposite(tabFolder, SWT.NONE, agent);
		contactCTabItem.setControl(contactComposite);
		tabFolder.setSelection(contactCTabItem);

	}

	public void setUser(User user) {
		this.user = user;
		boolean bIsConnected = true;
		if (user == null) {
			setStatus(NOT_CONNECTED_STATUS);
			removeUserTab();
			
			bIsConnected = false;
		} else {
			setStatus(CONNECTED_STATUS + user.getLogin());
		}
		// not connected actions
		signInAction.setEnabled(!bIsConnected);
		newUserAction.setEnabled(!bIsConnected);
		

		// connected actions
		signOutAction.setEnabled(bIsConnected);
		viewUserSyncTabAction.setEnabled(bIsConnected);
		viewUserExplorerTabAction.setEnabled(bIsConnected);
	}

}
