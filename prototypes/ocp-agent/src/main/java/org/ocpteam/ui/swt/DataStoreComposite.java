package org.ocpteam.ui.swt;

import java.io.File;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.serializable.Address;
import org.eclipse.wb.swt.SWTResourceManager;

public class DataStoreComposite extends Composite {

	public class FileReaderDialog extends Dialog {
		private Text text;
		private String title;
		private String content;

		/**
		 * Create the dialog.
		 * 
		 * @param parentShell
		 * @param content
		 */
		public FileReaderDialog(Shell parentShell, String title, String content) {
			super(parentShell);
			setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.CLOSE);
			this.title = title;
			this.content = content;
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(title);
		}

		/**
		 * Create contents of the dialog.
		 * 
		 * @param parent
		 */
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			container.setLayout(new FillLayout(SWT.HORIZONTAL));

			text = new Text(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.CANCEL | SWT.READ_ONLY | SWT.MULTI);
			text.setText(content);
			text.setFont(SWTResourceManager.getFont("Courier New", 9,
					SWT.NORMAL));
			return container;
		}

		/**
		 * Create contents of the button bar.
		 * 
		 * @param parent
		 */
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID,
					IDialogConstants.OK_LABEL, true);
		}

		/**
		 * Return the initial size of the dialog.
		 */
		@Override
		protected Point getInitialSize() {
			return new Point(450, 300);
		}

	}

	public class RefreshAction extends Action {
		public RefreshAction() {
			setText("&Refresh@F5");
			setToolTipText("Refresh");
		}

		@Override
		public void run() {
			refresh();
		}
	}

	public class InternalViewAction extends Action {
		public InternalViewAction() {
			setText("View in internal viewer");
			setToolTipText("View in internal viewer");
		}

		@Override
		public void run() {
			try {
				for (Item item : table.getSelection()) {
					Address address = new Address(item.getText());
					LOG.debug("name=" + address);
					byte[] content = mdm.get(address);
					LOG.debug("content=" + new String(content));
					Display display = dsw.getShell().getDisplay();
					FileReaderDialog dialog = new FileReaderDialog(new Shell(
							display), address.toString(), new String(content));
					dialog.open();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class ExternalViewAction extends Action {
		public ExternalViewAction() {
			setText("View in external viewer");
			setToolTipText("View in external viewer");
		}

		@Override
		public void run() {
			try {
				for (Item item : table.getSelection()) {
					Address address = new Address(item.getText());
					LOG.debug("name=" + address);
					byte[] content = mdm.get(address);
					LOG.debug("content=" + new String(content));
					File file = new File(dsw.getTempDir() + "/" + address
							+ ".txt");
					JLG.setBinaryFile(file, content);
					Program.launch(file.getAbsolutePath());
				}
			} catch (Exception e) {
				QuickMessage.exception(getShell(), "error", e);
			}
		}
	}

	Table table;
	private DataSourceWindow dsw;
	private IDataStore mdm;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public DataStoreComposite(Composite parent, int style,
			final DataSourceWindow dsw) {
		super(parent, style);
		this.dsw = dsw;
		mdm = (IDataStore) dsw.ds.getComponent(IDataStore.class);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent arg0) {
				LOG.debug("opening context menu");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu
						.createContextMenu(DataStoreComposite.this.dsw
								.getShell());

				int sel = table.getSelection().length;
				if (sel > 0) {
					InternalViewAction internalViewAction = new InternalViewAction();
					ExternalViewAction externalViewAction = new ExternalViewAction();
					myMenu.add(internalViewAction);
					myMenu.add(externalViewAction);
					myMenu.add(new Separator());
				}
				myMenu.add(new RefreshAction());
				menu.setEnabled(true);
				myMenu.setVisible(true);
				menu.setVisible(true);

			}
		});
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case SWT.F5:
					refresh();
					break;
				case SWT.ESC:
					table.deselectAll();
					break;
				default:
				}
				LOG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
				DataStoreComposite.this.dsw.refresh();
			}

		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				LOG.debug("double click");
				new InternalViewAction().run();
				LOG.debug("table item:" + e.widget.getClass());
			}

			@Override
			public void mouseDown(MouseEvent e) {
				LOG.debug("mouse down");
				Point pt = new Point(e.x, e.y);
				if (table.getItem(pt) == null) {
					LOG.debug("cancel selection");
					table.deselectAll();
				}
				DataStoreComposite.this.dsw.refresh();
			}
		});
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnKey = new TableColumn(table, SWT.NONE);
		tblclmnKey.setWidth(100);
		tblclmnKey.setText("Key");

		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("Value");

		refresh();

	}

	public void refresh() {
		try {
			table.removeAll();
			Set<Address> set = mdm.keySet();
			// Create an array containing the elements in a set
			for (Address key : set) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				String value = new String(mdm.get(key));
				tableItem.setText(new String[] { key.toString(), value });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
