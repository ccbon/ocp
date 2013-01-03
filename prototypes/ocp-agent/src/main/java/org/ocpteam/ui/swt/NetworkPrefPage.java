package org.ocpteam.ui.swt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow.MyPreferenceStore;

public class NetworkPrefPage extends PreferencePage {
	private Text text;
	private Label lblPleaseEnterAt;
	private Button btnJoinAnExisting;

	public NetworkPrefPage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.VERTICAL));

		btnJoinAnExisting = new Button(composite, SWT.CHECK);
		btnJoinAnExisting.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnJoinAnExisting.getSelection()) {
					text.setEnabled(true);
					lblPleaseEnterAt.setEnabled(true);
				} else {
					text.setEnabled(false);
					lblPleaseEnterAt.setEnabled(false);
				}
			}
		});
		btnJoinAnExisting.setText("Join an existing network");

		lblPleaseEnterAt = new Label(composite, SWT.NONE);
		lblPleaseEnterAt.setText("Please enter a sponsor");
		lblPleaseEnterAt.setEnabled(false);

		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new RowData(340, SWT.DEFAULT));
		text.setText("tcp://localhost:22222");
		text.setEnabled(false);

		return composite;
	}

	@Override
	protected void performApply() {
		JLG.debug("Network performApply");
		if (btnJoinAnExisting.getSelection()) {
			MyPreferenceStore ps = (MyPreferenceStore) getPreferenceStore();
			ps.w.ds.setProperty("sponsor.url", text.getText());
			ps.w.ds.setProperty("agent.isFirst", "no");
		}
	}
}
