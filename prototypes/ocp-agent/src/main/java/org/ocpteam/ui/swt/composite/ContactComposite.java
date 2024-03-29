package org.ocpteam.ui.swt.composite;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.ocpteam.component.ContactMap;
import org.ocpteam.serializable.Contact;
import org.ocpteam.ui.swt.DataSourceWindow;

public class ContactComposite extends Composite {

	private DataSourceWindow w;
	private Tree tree;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ContactComposite(Composite parent, int style, DataSourceWindow w) {
		super(parent, style);
		this.w = w;
		setLayout(new GridLayout(1, false));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		composite.pack();

		Button btnRefresh = new Button(composite, SWT.NONE);
		tree = new Tree(this, SWT.BORDER);
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		btnRefresh.setBounds(10, 10, 68, 23);
		btnRefresh.setText("Refresh");

		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		TreeColumn trclmnContact = new TreeColumn(tree, SWT.NONE);
		trclmnContact.setWidth(343);
		trclmnContact.setText("Contact");

		refresh();
	}

	public void refresh() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ContactMap contactMap = w.ds.getComponent(
							ContactMap.class);
					contactMap.refreshContactList();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		tree.removeAll();

		ContactMap contactMap = w.ds.getComponent(ContactMap.class);
		Iterator<Contact> it = contactMap.getContactSnapshotList().iterator();
		while (it.hasNext()) {
			Contact contact = it.next();
			try {
				TreeItem contactTreeItem = new TreeItem(tree, SWT.NONE);
				String text = contact.getName();
				contactTreeItem.setText(text);
				if (contact.isMyself()) {
					TreeItem urlTreeItem = new TreeItem(contactTreeItem,
							SWT.NONE);
					int port = Integer.parseInt(w.ds.getProperty("server.port", "22222"));
					urlTreeItem.setText("<myself> (Port: " + port + ")");
					contactTreeItem.setExpanded(true);
				} else if (contact.getTcpPort() > 0) {
					TreeItem urlTreeItem = new TreeItem(contactTreeItem,
							SWT.NONE);
					urlTreeItem.setText("tcp://" + contact.getHost() + ":"
							+ contact.getTcpPort());
					contactTreeItem.setExpanded(true);
				}
			} catch (Exception e) {
				TreeItem contactTreeItem = new TreeItem(tree, SWT.NONE);
				String text = "error while printing contact: e = " + e;
				contactTreeItem.setText(text);			
			}

		}

	}
}
