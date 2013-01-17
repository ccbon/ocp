package org.ocpteam.ui.swt;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.misc.LOG;
import org.ocpteam.serializable.Address;

public class DataStoreComposite extends Composite {
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
