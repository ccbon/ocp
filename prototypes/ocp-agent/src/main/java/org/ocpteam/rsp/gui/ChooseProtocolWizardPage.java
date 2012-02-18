package org.ocpteam.rsp.gui;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.ocpteam.misc.JLG;

public class ChooseProtocolWizardPage extends WizardPage {

	public Combo combo;
	private Map<String, String> map;
	public String selection;
	
	/**
	 * Create the wizard.
	 */
	public ChooseProtocolWizardPage(Map<String, String> map) {
		super("wizardPage");
		setTitle("Choose a protocol to test");
		setDescription("Please choose the protocol you wish to use.");
		this.map = map;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblProtocol = new Label(container, SWT.NONE);
		lblProtocol.setBounds(86, 101, 157, 13);
		lblProtocol.setText("Protocol");
		
		final String[] keys = (String[]) map.keySet().toArray(new String[map.size()]);
		
		combo = new Combo(container, SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selection = map.get(keys[combo.getSelectionIndex()]);
				JLG.debug("selection = " + selection);
			}
		});
		
		combo.setItems(keys);
		combo.setBounds(84, 120, 159, 21);
		combo.select(0);
		selection = map.get(keys[0]);
	}
}
