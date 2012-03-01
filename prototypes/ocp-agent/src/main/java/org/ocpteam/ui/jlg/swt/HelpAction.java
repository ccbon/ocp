package org.ocpteam.ui.jlg.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.ocpteam.misc.JLG;


public class HelpAction extends Action {

	private DataSourceWindow window;

	public HelpAction(DataSourceWindow w) {
		window = w;
		setText("&Help@F1");
		setToolTipText("Help");
		try {
			ImageDescriptor i = ImageDescriptor.createFromImageData(new ImageData(HelpAction.class.getResourceAsStream("linkto_help.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Starting help in OS default browser...");
			Program.launch(window.getHelpURL());
		}

}