package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.DataStoreComposite;

public class ViewDataStoreAction extends Action {
	private DataSourceWindow window;

	public ViewDataStoreAction(DataSourceWindow w) {
		window = w;
		setText("View Datastore");
		setToolTipText("View Datastore Tab");
	}

	@Override
	public void run() {
		LOG.debug("View Datastore");
		addDataStoreTab();
	}

	public void addDataStoreTab() {
		// if one contact tab is already present, then just select it.
		CTabFolder tabFolder = window.tabFolder;
		CTabItem[] items = tabFolder.getItems();
		for (CTabItem item : items) {
			if (item.getControl().getClass() == DataStoreComposite.class) {
				DataStoreComposite datastoreComposite = (DataStoreComposite) item
						.getControl();
				tabFolder.setSelection(item);
				datastoreComposite.refresh();
				return;
			}
		}

		final CTabItem datastoreCTabItem = new CTabItem(tabFolder, SWT.NONE);
		datastoreCTabItem.setShowClose(true);
		datastoreCTabItem.setText("Datastore");
		datastoreCTabItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				LOG.debug("dispose");
			}
		});

		DataStoreComposite datastoreComposite = new DataStoreComposite(tabFolder,
				SWT.NONE, window);
		datastoreCTabItem.setControl(datastoreComposite);
		tabFolder.setSelection(datastoreCTabItem);

		window.addListener(DataSourceWindow.ON_DS_CLOSE, new Listener() {

			@Override
			public void handleEvent(Event event) {
				LOG.debug("closing contact list");
				datastoreCTabItem.dispose();
			}
		});

	}
}
