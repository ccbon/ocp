package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;


public class ViewDataModelAction extends Action {
	private DataSourceWindow w;

	public ViewDataModelAction(DataSourceWindow w) {
		this.w = w;
		setText("&Data Model@Ctrl+E");
		setToolTipText("Data Model");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ViewDataModelAction.class
							.getResourceAsStream("view_user_explorer.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("Data Model");
		try {
			w.viewDataModel();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			w.refresh();
			w.getShell().setFocus();
		}
	}
}