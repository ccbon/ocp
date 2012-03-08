package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;

public class CloseDataSourceAction extends Action {
	private DataSourceWindow w;

	public CloseDataSourceAction(DataSourceWindow w) {
		this.w = w;
		setText("&Close@Ctrl+W");
		setToolTipText("Close File");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(
							CloseDataSourceAction.class
									.getResourceAsStream("close.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Close DataSource");

		try {
			if (w.ds == null) {
				throw QuickMessage.exception(w.getShell(),
						"No datasource is open !");
			}
			if (QuickMessage.confirm(w.getShell(),
					"Are you sure you want to close the datasource ?")) {
				w.closeDataSource();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			w.refresh();
		}
	}
}