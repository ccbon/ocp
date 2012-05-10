package org.thirdparty.test;

import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.example.DefaultApplication;
import org.ocpteam.misc.JLG;
import org.ocpteamx.protocol.ftp.FTPDataSource;
import org.ocpteamx.protocol.ocp.OCPDataSource;
import org.thirdparty.protocol.ocp2.OCP2DataSource;

public class Test {
	public static void main(String[] args) {
		try {
			DefaultApplication app = new DefaultApplication();
			DataSourceFactory dsf = app.getComponent(DataSourceFactory.class);
			dsf.replaceComponent(OCPDataSource.class, new OCP2DataSource());
			dsf.removeComponent(FTPDataSource.class);
			JLG.debug_on();
			JLG.debug("test");
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
