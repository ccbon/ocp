package org.ocpteam.ui.swt;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.interfaces.IDataModel;
import org.ocpteam.interfaces.IMapDataModel;
import org.ocpteam.misc.JLG;

public class MapComposite extends Composite {
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
			public void menuDetected(MenuDetectEvent arg0) {
			}
		});
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.keyCode) {
				case (int) SWT.INSERT:
					(new SetKeyAction(dsw)).run();
					break;
				case (int) SWT.F5:
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
				JLG.debug("keypressed: keycode:" + e.keyCode
						+ " and character = '" + e.character + "'");
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
			String[] array = (String[]) set.toArray(new String[set.size()]);
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
			JLG.debug("removing key = " + key);
			mdm.remove(key);
		}
		refresh();
	}
}
