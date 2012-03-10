package org.thirdparty.test;

import java.util.ListResourceBundle;

import org.ocpteam.layer.rsp.DataSource;
import org.ocpteam.ui.swt.DataSourceWindow;

public class Test {
	public static void main(String[] args) {
		DataSource.protocolResource = new ListResourceBundle() {

			@Override
			protected Object[][] getContents() {
				return new Object[][] {
						{ "OCP", "org.ocpteam.protocol.ocp.OCPDataSource" },
						{ "OCP2", "org.thirdparty.protocol.ocp2.OCP2DataSource" }, };
			}
		};

		DataSourceWindow.main(null);
		
	}
}
