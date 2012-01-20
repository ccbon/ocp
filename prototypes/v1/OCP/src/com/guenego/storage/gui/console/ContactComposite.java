package com.guenego.storage.gui.console;

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

import com.guenego.misc.URL;
import com.guenego.ocp.Contact;
import com.guenego.storage.Agent;

public class ContactComposite extends Composite {

	private Agent agent;
	private Tree tree;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ContactComposite(Composite parent, int style, Agent agent) {
		super(parent, style);
		this.agent = agent;
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
					agent.refreshContactList();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		tree.removeAll();

		Iterator<Contact> it = agent.getContactIterator();
		while (it.hasNext()) {
			Contact contact = (Contact) it.next();
			TreeItem contactTreeItem = new TreeItem(tree, SWT.NONE);
			String text = contact.id + " - " + contact.getName();
			contactTreeItem.setText(text);
			Iterator<URL> itu = contact.urlList.iterator();
			while (itu.hasNext()) {
				URL url = (URL) itu.next();
				TreeItem urlTreeItem = new TreeItem(contactTreeItem, SWT.NONE);
				urlTreeItem.setText(url.toString());
				contactTreeItem.setExpanded(true);
			}
		}

	}
}
