package org.ocpteam.ui.swt;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.ocpteam.functionality.DataSourceFactory;
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
			w.ds = w.app.designer.get(DataSourceFactory.class).getInstance(protocol);
			ResourceBundle swt = w.ds.getResource("swt");
			Scenario scenario = (Scenario) swt.getObject("NewDataSourceScenario");
			scenario.setWindow(w);
			scenario.run();
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