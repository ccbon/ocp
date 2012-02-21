package org.ocpteam.ui.swt;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.ocpteam.layer.dsp.DSPAgent;
import org.ocpteam.layer.rsp.Agent;
import org.ocpteam.layer.rsp.User;
import org.ocpteam.misc.JLG;

public class AdminConsole extends ApplicationWindow {

	private static final String NOT_CONNECTED_STATUS = "No user connected.";
	private static final String CONNECTED_STATUS = "User connected: ";
	private static final String AGENT_OFF = "Agent OFF";
	private static final String AGENT_ON = "Agent ON";

	public Agent agent;
	private User user;

	private Display display;
	public CTabFolder tabFolder;
	CTabItem contactCTabItem;
	private ContactComposite contactComposite;

	private CTabItem userCTabItem;
	private CTabItem userExplorerCTabItem;
	public ExplorerComposite explorerComposite;

	private StartAgentAction startAgentAction;
	private StopAgentAction stopAgentAction;
	private NewUserAction newUserAction;
	private SignInAction signInAction;
	private SignOutAction signOutAction;
	private ExitAction exitAction;

	private SelectAllAction selectAllAction;
	private CopyAction copyAction;
	private PasteAction pasteAction;
	private CommitAction commitAction;
	private CheckOutAction checkoutAction;

	private ViewContactTabAction viewContactTabAction;
	private ViewUserSyncTabAction viewUserSyncTabAction;
	private ViewUserExplorerTabAction viewUserExplorerTabAction;

	private RemoveStorageAction removeStorageAction;

	private HelpAction helpAction;
	private AboutAction aboutAction;
	private DebugAction debugAction;
	private boolean bIsConnected;

	public Clipboard clipboard;

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
		clipboard = new Clipboard(display);
		
		SWTAgentAssistant a = (SWTAgentAssistant) agent.getAssistant(Main.SWT_ASSISTANT);
		a.display = display;
		
	}

	@Override
	public boolean close() {
		if (agent.isOnlyClient()) {
			agent.stop();
			display.dispose();
			return true;
		} else {
			// only hide it.
			getShell().setVisible(false);
			return true;
		}
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

	public void updateActions() {
		if (!agent.autoStarts()) {
			startAgentAction.setEnabled(!agent.isStarted());
			stopAgentAction.setEnabled(agent.isStarted());

		}

		// not connected actions
		signInAction.setEnabled(agent.isStarted() && !bIsConnected);
		if (agent.allowsUserCreation()) {
			newUserAction.setEnabled(agent.isStarted() && !bIsConnected);
		}

		// connected actions
		signOutAction.setEnabled(bIsConnected);
		viewUserSyncTabAction.setEnabled(bIsConnected);
		viewUserExplorerTabAction.setEnabled(bIsConnected);

		if (agent.isStarted()) {
			if (bIsConnected) {
				setStatus(AGENT_ON + " | " + CONNECTED_STATUS + user.getLogin());
			} else {
				setStatus(AGENT_ON + " | " + NOT_CONNECTED_STATUS);
				removeUserTab();
			}
		} else {
			setStatus(AGENT_OFF);
			removeUserTab();
		}

		// user explorer stuff
		copyAction.setEnabled(copyAction.canRun());
		pasteAction.setEnabled(pasteAction.canRun());
		commitAction.setEnabled(commitAction.canRun());
		checkoutAction.setEnabled(checkoutAction.canRun());

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		if (!agent.autoStarts()) {
			startAgentAction = new StartAgentAction(this);
			stopAgentAction = new StopAgentAction(this);
		}
		exitAction = new ExitAction(agent, display, this);
		if (agent.allowsUserCreation()) {
			newUserAction = new NewUserAction(agent, display, this);
		}
		signInAction = new SignInAction(this);
		signOutAction = new SignOutAction(this);
		if (DSPAgent.class.isInstance(agent)) {
			viewContactTabAction = new ViewContactTabAction(this);
		}
		if (DSPAgent.class.isInstance(agent)) {
			if (((DSPAgent) agent).hasStorage()) {
				removeStorageAction = new RemoveStorageAction(this);
			}
		}
		debugAction = new DebugAction();
		helpAction = new HelpAction(this);
		aboutAction = new AboutAction(this);
		viewUserSyncTabAction = new ViewUserSyncTabAction(this);
		viewUserExplorerTabAction = new ViewUserExplorerTabAction(this);

		selectAllAction = new SelectAllAction(this);
		copyAction = new CopyAction(this);
		pasteAction = new PasteAction(this);
		commitAction = new CommitAction(this);
		checkoutAction = new CheckOutAction(this);
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
		if (!agent.autoStarts()) {
			fileMenu.add(startAgentAction);
			fileMenu.add(stopAgentAction);
			fileMenu.add(new Separator());
		}
		fileMenu.add(signInAction);
		fileMenu.add(signOutAction);
		if (agent.allowsUserCreation()) {
			fileMenu.add(new Separator());
			fileMenu.add(newUserAction);
		}
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);

		MenuManager editMenu = new MenuManager("&Edit");
		editMenu.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager arg0) {
				AdminConsole.this.updateActions();
			}
		});
		menuBar.add(editMenu);
		editMenu.add(selectAllAction);
		editMenu.add(new Separator());
		editMenu.add(copyAction);
		editMenu.add(pasteAction);
		editMenu.add(new Separator());
		editMenu.add(commitAction);
		editMenu.add(checkoutAction);

		MenuManager viewMenu = new MenuManager("&View");
		menuBar.add(viewMenu);
		if (viewContactTabAction != null) {
			viewMenu.add(viewContactTabAction);
		}
		viewMenu.add(viewUserSyncTabAction);
		viewMenu.add(viewUserExplorerTabAction);

		MenuManager testMenu = new MenuManager("&Test");
		menuBar.add(testMenu);
		if (removeStorageAction != null) {
			testMenu.add(removeStorageAction);
		}
		testMenu.add(debugAction);

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
		if (!agent.autoStarts()) {
			toolBarManager.add(startAgentAction);
			toolBarManager.add(stopAgentAction);
			toolBarManager.add(new Separator());
		}
		if (agent.allowsUserCreation()) {
			toolBarManager.add(newUserAction);
			toolBarManager.add(new Separator());
		}
		toolBarManager.add(signInAction);
		toolBarManager.add(signOutAction);
		toolBarManager.add(new Separator());
		if (viewContactTabAction != null) {
			toolBarManager.add(viewContactTabAction);
		}
		toolBarManager.add(viewUserSyncTabAction);
		toolBarManager.add(viewUserExplorerTabAction);
		toolBarManager.add(new Separator());
		if (removeStorageAction != null) {
			toolBarManager.add(removeStorageAction);
			toolBarManager.add(new Separator());
		}
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
		newShell.setText(agent.getProtocolName() + " Agent Console - "
				+ agent.getName());
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

			explorerComposite = new ExplorerComposite(tabFolder,
					SWT.NONE, agent, agent.getFileSystem(user), this);
			userExplorerCTabItem.setControl(explorerComposite);

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
		if (userExplorerCTabItem != null
				&& (!userExplorerCTabItem.isDisposed())) {
			userExplorerCTabItem.dispose();
			userExplorerCTabItem = null;
		}
		explorerComposite = null;
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

		contactComposite = new ContactComposite(tabFolder, SWT.NONE,
				(DSPAgent) agent);
		contactCTabItem.setControl(contactComposite);
		tabFolder.setSelection(contactCTabItem);

	}

	public void setUser(User user) {
		this.user = user;
		bIsConnected = agent.isStarted() && (user != null);

		updateActions();
	}

	public User getUser() {
		return user;
	}

}
