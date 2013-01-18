package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.program.Program;
import org.ocpteam.component.PersistentFileMap;
import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;

public class OpenDataStoreFolderAction extends Action {
	private DataSourceWindow w;

	public OpenDataStoreFolderAction(DataSourceWindow w) {
		this.w = w;
		setText("Open datastore folder");
		setToolTipText("Open datastore folder");
	}

	@Override
	public void run() {
		try {
			PersistentFileMap fs = (PersistentFileMap) w.ds
					.getComponent(IDataStore.class);
			LOG.debug("root path=" + fs.getURI());
			Program.launch(fs.getURI());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
