package org.ocpteam.ui.swt;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.ocpteam.functionality.DataModel;
import org.ocpteam.functionality.MapDataModel;

public class MapComposite extends Composite {
	private Table table;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapComposite(Composite parent, int style, DataSourceWindow dsw) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnKey = new TableColumn(table, SWT.NONE);
		tblclmnKey.setWidth(100);
		tblclmnKey.setText("Key");
		
		TableColumn tblclmnValue = new TableColumn(table, SWT.NONE);
		tblclmnValue.setWidth(100);
		tblclmnValue.setText("Value");
		
		MapDataModel mdm = (MapDataModel) dsw.ds.getDesigner().get(DataModel.class);
		Map<byte[], byte[]> map = mdm.getMap();
		Set<byte[]> set = map.keySet();
		// Create an array containing the elements in a set
		byte[][] array = (byte[][]) set
				.toArray(new byte[set.size()][]);
		for (byte[] key : array) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String s = new String(key);
			String value = new String(map.get(key));
			tableItem.setText(new String[] { s , value });
		}


	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
