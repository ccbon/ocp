package org.ocpteam.ui.swt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;
import org.ocpteam.ui.swt.action.OpenDataStoreFolderAction;

public class ViewDataStorePrefPage extends PreferencePage {
	public ViewDataStorePrefPage() {
	}

	private MyPreferenceStore ps;

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));
		ps = (MyPreferenceStore) getPreferenceStore();

		Button btn = new Button(composite, SWT.BUTTON1);
		btn.setText("View Datastore");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new OpenDataStoreFolderAction(ps.w).run();
			}
			
		});
		return composite;
	}

}
