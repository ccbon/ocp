package com.guenego.ocp.gui.console;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.guenego.ocp.Agent;
import com.guenego.ocp.User;

import org.eclipse.wb.swt.SWTResourceManager;

public class AdminConsole extends ApplicationWindow {

	public Agent agent;
	private ExitAction exitAction;
	private Display display;
	private NewUserAction newUserAction;
	private SignInAction signInAction;
	private CTabFolder tabFolder;

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
		//return super.close();
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
		setStatus("\u00A9 JLG Consulting - 2011 - Jean-Louis GUENEGO");
		Composite container = new Composite(parent, SWT.BORDER);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new CTabFolder(container, SWT.BORDER);
		//tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.setSimple(false);
		
		CTabItem tbtmWelcome = new CTabItem(tabFolder, SWT.NONE);
		tbtmWelcome.setShowClose(true);
		tbtmWelcome.setText("Welcome");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		tbtmWelcome.setControl(composite);
		

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
		MenuManager helpMenu = new MenuManager("&Help");
		menuBar.add(fileMenu);
		
		MenuManager editMenu = new MenuManager("&Edit");
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
		fileMenu.add(exitAction);
		fileMenu.add(newUserAction);
		fileMenu.add(signInAction);
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
		toolBarManager.add(exitAction);
		toolBarManager.add(newUserAction);
		toolBarManager.add(signInAction);
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
		newShell.setImage(SWTResourceManager.getImage("images/ocp_icon.png"));
		super.configureShell(newShell);
		newShell.setText("OCP Agent Console");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(652, 514);
	}

	public void addUserTab(User user) {
		CTabItem userCTabItem = new CTabItem(tabFolder, SWT.NONE);
		userCTabItem.setShowClose(true);
		userCTabItem.setText("User: " + user.getLogin());
		
		Composite composite = new UserComposite(tabFolder, SWT.NONE, agent, user);
		userCTabItem.setControl(composite);
		tabFolder.setSelection(userCTabItem);
	}
}
