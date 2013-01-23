package org.ocpteam.ui.swt.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class DataStoreValueComposite extends Composite {

	private Text text_1;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param string
	 */
	public DataStoreValueComposite(Composite parent, int style, String content) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		text_1 = new Text(this, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_1.setText(content);
	}
}
