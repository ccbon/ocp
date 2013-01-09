package org.ocpteam.ui.swt;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.ocpteam.component.DSPDataSource;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.swt.QuickMessage;

public class NewDataSourceAction extends Action {
	private DataSourceWindow w;
	private String protocol;

	public NewDataSourceAction(DataSourceWindow w, String protocol) {
		this.w = w;
		this.protocol = protocol;
		setText(protocol.toUpperCase());
		setToolTipText("New " + protocol.toUpperCase());
	}

	@Override
	public void run() {
		JLG.debug("New DataSource");
		try {
			if (w.ds != null) {
				w.closeDataSourceAction.run();
			}
			if (w.ds != null) {
				QuickMessage.error(w.getShell(),
						"Cannot open a datasource if another is already open.");
				return;
			}
			w.ds = w.dsf.getInstance(protocol);
			ResourceBundle swt = w.dsf.getResource(protocol, "swt");
			JLG.debug("bundle=" + swt);
			if (swt != null && swt.containsKey("NewDataSourceScenario")) {
				IScenario scenario = (IScenario) swt
						.getObject("NewDataSourceScenario");
				scenario.setWindow(w);
				scenario.run();
				if (!scenario.succeeded()) {
					w.ds = null;
				}
			} else {
				if (w.ds instanceof DSPDataSource) {
					EditDSPDataSourceScenario scenario = new EditDSPDataSourceScenario();
					scenario.setWindow(w);
					w.setDSEditionMode(DataSourceWindow.NEW_MODE);
					scenario.run();
					if (!scenario.succeeded()) {
						w.ds = null;
					}
				}
			}
			if (w.ds == null) {
				return;
			}
			w.openDataSource(w.ds);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			w.refresh();
		}
	}
}