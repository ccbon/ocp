package org.ocpteam.ui.swt.composite;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.action.RemoveKeyAction;
import org.ocpteam.ui.swt.action.SetKeyAction;

public class MapComposite extends Composite {
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

	Table table;
	private DataSourceWindow dsw;
	private IMapDataModel mdm;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MapComposite(Composite parent, int style, final DataSourceWindow dsw) {
		super(parent, style);
		this.dsw = dsw;
		mdm = (IMapDataModel) dsw.ds.getComponent(IDataModel.class);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent arg0) {
				LOG.info("opening context menu");
				final MenuManager myMenu = new MenuManager("xxx");
				final Menu menu = myMenu
						.createContextMenu(MapComposite.this.dsw.getShell());

				int sel = table.getSelection().length;
				if (sel > 0) {
					RemoveKeyAction removeKeyAction = new RemoveKeyAction(
							MapComposite.this.dsw);
					myMenu.add(removeKeyAction);
				}
				if (sel == 0) {
					myMenu.add(new SetKeyAction(MapComposite.this.dsw));
				}
				myMenu.add(new Separator());
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
				case SWT.INSERT:
					(new SetKeyAction(dsw)).run();
					break;
				case SWT.F5:
					refresh();
					break;
				case SWT.DEL:
					(new RemoveKeyAction(dsw)).run();
					break;
				case SWT.ESC:
					table.deselectAll();
					break;
				default:
				}
				LOG.info("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
				MapComposite.this.dsw.refresh();
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
				MapComposite.this.dsw.refresh();
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

	private void refresh() {
		try {
			table.removeAll();
			Set<String> set = mdm.keySet();
			// Create an array containing the elements in a set
			String[] array = set.toArray(new String[set.size()]);
			for (String key : array) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				String s = key;
				String value = new String(mdm.get(key));
				tableItem.setText(new String[] { s, value });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void set(String key, String value) throws Exception {
		mdm.set(key, value);
		refresh();
	}

	public void remove() throws Exception {
		for (TableItem item : table.getSelection()) {
			String key = item.getText(0);
			LOG.info("removing key = " + key);
			mdm.remove(key);
		}
		refresh();
	}
}
