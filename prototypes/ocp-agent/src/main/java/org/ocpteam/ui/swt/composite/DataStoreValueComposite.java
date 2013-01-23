package org.ocpteam.ui.swt.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class DataStoreValueComposite extends Composite {

	private Text text;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param string
	 */
	public DataStoreValueComposite(Composite parent, int style, String content) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		text = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setFont(SWTResourceManager.getFont("Courier New", 9, SWT.NORMAL));
		text.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text.setText(content);
	}
}
