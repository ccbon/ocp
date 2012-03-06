package org.ocpteam.ui.swt;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.misc.JLG;


public class NewDataSourceAction extends Action {
	private DataSourceWindow w;
	private String protocol;

	public NewDataSourceAction(DataSourceWindow w, String protocol) {
		this.w = w;
		this.protocol = protocol;
		setText(protocol.toUpperCase());
		setToolTipText("New " + protocol.toUpperCase());
	}

	public void run() {
		JLG.debug("New DataSource");
		try {
			if (w.ds != null) {
				w.closeDataSourceAction.run();
			}
			ResourceBundle swt = DataSource.getResource(protocol, "swt");
			Scenario scenario = (Scenario) swt.getObject("NewDataSourceScenario");
			scenario.run(w);
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