package org.ocpteam.test;

import org.ocpteam.core.Application;
import org.ocpteam.functionality.DataSourceFactory;
import org.ocpteam.functionality.TestScenario;
import org.ocpteam.protocol.ocp.OCPDataSource;

public class OCPSimpleTest extends TestScenario {
	public static void main(String[] args) {
		try {
			Application app = new Application();
			app.designer.add(DataSourceFactory.class);
			DataSourceFactory dsf = app.designer.get(DataSourceFactory.class);
			dsf.designer.add(OCPDataSource.class);
			app.designer.add(TestScenario.class, new OCPSimpleTest());

			app.designer.get(TestScenario.class).test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean test() {
		try {
			System.out.println("Hello Test Scenario");
			DataSourceFactory dsf = app.designer.get(DataSourceFactory.class);
			OCPDataSource ds = (OCPDataSource) dsf.getInstance("OCP");
			ds.open();
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
