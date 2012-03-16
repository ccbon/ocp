package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertTrue;

import org.ocpteam.component.DataSource;
import org.ocpteam.component.TestScenario;
import org.ocpteam.protocol.ocp.OCPDataSource;

public class OCPSimpleTest extends TestScenario {

	@org.junit.Test
	public void simple() {
		try {
			assertTrue(new OCPSimpleTest().test());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean test() {
		try {
			System.out.println("Hello Test Scenario");
			DataSource ds = new OCPDataSource();
			ds.open();
			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
