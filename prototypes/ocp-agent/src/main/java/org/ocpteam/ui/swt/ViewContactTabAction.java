package org.ocpteam.ui.swt;

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
import org.ocpteam.component.Agent;
import org.ocpteam.misc.JLG;

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
		JLG.debug("View Contact");
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
				JLG.debug("dispose");
			}
		});

		ContactComposite contactComposite = new ContactComposite(tabFolder,
				SWT.NONE, window.ds.getComponent(Agent.class));
		contactCTabItem.setControl(contactComposite);
		tabFolder.setSelection(contactCTabItem);

		window.addListener(DataSourceWindow.ON_DS_CLOSE, new Listener() {

			@Override
			public void handleEvent(Event event) {
				JLG.debug("closing contact list");
				contactCTabItem.dispose();
			}
		});

	}
}