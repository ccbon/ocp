package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;


public class ViewExplorerAction extends Action {
	private DataSourceWindow w;

	public ViewExplorerAction(DataSourceWindow w) {
		this.w = w;
		setText("&Data Model@Ctrl+E");
		setToolTipText("Data Model");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ViewExplorerAction.class
							.getResourceAsStream("view_user_explorer.png")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		JLG.debug("View Explorer");
		try {
			w.viewExplorer();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			w.refresh();
		}
	}
}