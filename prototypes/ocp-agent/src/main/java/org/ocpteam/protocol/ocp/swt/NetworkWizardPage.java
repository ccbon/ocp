package org.ocpteam.protocol.ocp.swt;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NetworkWizardPage extends WizardPage {

	public Text backupNbrText;
	public Combo symmetricAlgoCombo;
	public Combo asymmetricAlgoCombo;
	public Combo messageDigestCombo;

	/**
	 * Create the wizard.
	 */
	public NetworkWizardPage() {
		super("networkPage");
		setTitle("Network configuration");
		setDescription("Setup the OCP network properties.");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		Label lblNetworkBackupNumber = new Label(container, SWT.NONE);
		lblNetworkBackupNumber.setBounds(10, 24, 176, 13);
		lblNetworkBackupNumber.setText("Network backup number :");
		
		backupNbrText = new Text(container, SWT.BORDER);
		backupNbrText.setText("5");
		backupNbrText.setBounds(196, 21, 93, 19);
		
		Group grpAlgorithms = new Group(container, SWT.NONE);
		grpAlgorithms.setText("Algorithms");
		grpAlgorithms.setBounds(10, 79, 367, 185);
		
		Label lblHashFunction = new Label(grpAlgorithms, SWT.NONE);
		lblHashFunction.setBounds(10, 29, 176, 13);
		lblHashFunction.setText("Message digest algorithm :");
		
		messageDigestCombo = new Combo(grpAlgorithms, SWT.NONE);
		messageDigestCombo.setBounds(192, 26, 93, 21);
		messageDigestCombo.setItems(new String[] {"MD2", "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512"});
		messageDigestCombo.select(2);
		
		asymmetricAlgoCombo = new Combo(grpAlgorithms, SWT.NONE);
		asymmetricAlgoCombo.setBounds(192, 64, 93, 21);
		asymmetricAlgoCombo.setItems(new String[] {"DiffieHellman", "DSA", "RSA", "EC"});
		asymmetricAlgoCombo.select(1);
		
		Label lblAsymmetricEncryptionAlgorithm = new Label(grpAlgorithms, SWT.NONE);
		lblAsymmetricEncryptionAlgorithm.setBounds(10, 67, 176, 13);
		lblAsymmetricEncryptionAlgorithm.setText("Asymmetric encryption algorithm :");
		
		Label lblAgentSignatureAlgorithm = new Label(grpAlgorithms, SWT.NONE);
		lblAgentSignatureAlgorithm.setBounds(10, 107, 176, 13);
		lblAgentSignatureAlgorithm.setText("Symmetric encryption algorithm :");
		
		symmetricAlgoCombo = new Combo(grpAlgorithms, SWT.NONE);
		symmetricAlgoCombo.setBounds(192, 104, 93, 21);
		symmetricAlgoCombo.setItems(new String[] {"AES", "DES"});
		symmetricAlgoCombo.select(0);
	}
	
}
