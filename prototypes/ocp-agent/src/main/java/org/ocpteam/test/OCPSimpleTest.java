package org.ocpteam.test;

import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.component.TestScenario;
import org.ocpteam.core.TopContainer;
import org.ocpteam.protocol.ocp.OCPDataSource;

public class OCPSimpleTest extends TestScenario {
	public static void main(String[] args) {
		try {
			TopContainer app = new TopContainer();
			app.addComponent(DataSourceFactory.class);
			DataSourceFactory dsf = app.getComponent(DataSourceFactory.class);
			dsf.addComponent(OCPDataSource.class);
			app.addComponent(TestScenario.class, new OCPSimpleTest());

			app.getComponent(TestScenario.class).test();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean test() {
		try {
			System.out.println("Hello Test Scenario");
			DataSourceFactory dsf = getParent().getComponent(DataSourceFactory.class);
			OCPDataSource ds = (OCPDataSource) dsf.getInstance("OCP");
			ds.connect();
			ds.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
