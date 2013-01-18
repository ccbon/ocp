package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.swt.QuickMessage;
import org.ocpteam.ui.swt.DataSourceWindow;

public class CloseDataSourceAction extends Action {
	private DataSourceWindow w;

	public CloseDataSourceAction(DataSourceWindow w) {
		this.w = w;
		setText("&Close@Ctrl+W");
		setToolTipText("Close File");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(
							DataSourceWindow.class
									.getResourceAsStream("close.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		LOG.debug("Close DataSource");

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