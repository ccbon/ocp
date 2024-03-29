package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.composite.ContactComposite;

public class ViewContactTabAction extends Action {
	private DataSourceWindow window;

	public ViewContactTabAction(DataSourceWindow w) {
		window = w;
		setText("View Cont&act@Ctrl+T");
		setToolTipText("View Contact Tab");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(DataSourceWindow.class
							.getResourceAsStream("view_contact.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.info("View Contact");
		addContactTab();
	}

	public void addContactTab() {
		// if one contact tab is already present, then just select it.
		CTabFolder tabFolder = window.tabFolder;
		CTabItem[] items = tabFolder.getItems();
		for (CTabItem item : items) {
			if (item.getControl().getClass() == ContactComposite.class) {
				ContactComposite contactComposite = (ContactComposite) item
						.getControl();
				tabFolder.setSelection(item);
				contactComposite.refresh();
				return;
			}
		}

		final CTabItem contactCTabItem = new CTabItem(tabFolder, SWT.NONE);
		contactCTabItem.setShowClose(true);
		contactCTabItem.setText("Contacts");
		contactCTabItem.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				LOG.info("dispose");
			}
		});

		ContactComposite contactComposite = new ContactComposite(tabFolder,
				SWT.NONE, window);
		contactCTabItem.setControl(contactComposite);
		tabFolder.setSelection(contactCTabItem);

		window.addListener(DataSourceWindow.ON_DS_CLOSE, new Listener() {

			@Override
			public void handleEvent(Event event) {
				LOG.info("closing contact list");
				contactCTabItem.dispose();
			}
		});

	}
}