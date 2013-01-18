package org.ocpteam.ui.swt.action;

import org.eclipse.jface.action.Action;
import org.ocpteam.misc.LOG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteam.ui.swt.EditDSPDataSourceScenario;

public class EditDataSouceAction extends Action {

	private DataSourceWindow w;

	public EditDataSouceAction(DataSourceWindow w) {
		this.w = w;
		setText("&Edit Datasource properties");
		setToolTipText("Edit Datasource properties");
	}

	@Override
	public void run() {
		LOG.debug("EditDataSourceAction");
		try {
			EditDSPDataSourceScenario scenario = new EditDSPDataSourceScenario();
			scenario.setWindow(w);
			w.setDSEditionMode(DataSourceWindow.EDIT_MODE);
			scenario.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
