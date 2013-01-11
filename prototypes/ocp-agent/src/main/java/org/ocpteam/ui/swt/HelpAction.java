package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.ocpteam.misc.LOG;


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

	@Override
	public void run() {
		LOG.debug("Starting help in OS default browser...");
			Program.launch(window.getHelpURL());
		}

}