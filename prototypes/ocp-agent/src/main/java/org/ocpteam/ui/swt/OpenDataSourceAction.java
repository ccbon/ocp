package org.ocpteam.ui.swt;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.FileDialog;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;


public class OpenDataSourceAction extends Action {
	private DataSourceWindow w;

	public OpenDataSourceAction(DataSourceWindow w) {
		this.w = w;
		setText("&Open@Ctrl+O");
		setToolTipText("Open File");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(OpenDataSourceAction.class
							.getResourceAsStream("open.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		JLG.debug("Open DataSource");
		try {
			if (w.ds != null) {
				w.closeDataSourceAction.run();
			}

			FileDialog fileDialog = new FileDialog(w.getShell());
			fileDialog.setFilterPath(System.getProperty("user.home"));
			fileDialog.setText("Please select a file and click OK");
//			String[] filterExt = { "*.zip", "*.uri", "*.*" };
//			fileDialog.setFilterExtensions(filterExt);
			String filename = fileDialog.open();
			w.openDataSource(DataSource.getInstance(new File(filename)));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			w.refresh();
		}
	}
}