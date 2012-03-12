package org.thirdparty.test;

import org.ocpteam.core.Application;
import org.ocpteam.functionality.DataSourceFactory;
import org.ocpteam.functionality.PersistentMap;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.thirdparty.protocol.ocp2.OCP2DataSource;
import org.thirdparty.protocol.ocp2.ThirdPartyPersistentMap;

public class Test {
	public static void main(String[] args) {
		try {
			Application app = new Application();
			app.designer.add(DataSourceFactory.class);
			app.designer.add(DataSourceWindow.class);
			
			DataSourceFactory dsf = app.designer.get(DataSourceFactory.class);
			dsf.designer.add(OCPDataSource.class);
			dsf.designer.add(OCP2DataSource.class);
			dsf.designer.get(OCP2DataSource.class).designer.add(PersistentMap.class, new ThirdPartyPersistentMap());
			
			app.designer.get(DataSourceWindow.class).start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
