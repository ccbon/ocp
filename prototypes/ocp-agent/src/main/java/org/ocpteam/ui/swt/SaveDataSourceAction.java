package org.ocpteam.ui.swt;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.ocpteam.misc.JLG;

public class SaveDataSourceAction extends Action {
	private DataSourceWindow window;

	public SaveDataSourceAction(DataSourceWindow w) {
		window = w;
		setText("&Save@Ctrl+S");
		setToolTipText("Save File");
		try {
			ImageDescriptor i = ImageDescriptor
					.createFromImageData(new ImageData(ExitAction.class
							.getResourceAsStream("save_edit.gif")));
			setImageDescriptor(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		JLG.debug("Saving datasource");
		try {
			if (window.ds.getFile() == null || window.ds.isNew()) {
				window.saveAsDataSourceAction.run();
			} else {
				window.ds.save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		window.refresh();
	}
}