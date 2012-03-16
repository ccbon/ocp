package org.thirdparty.test;

import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.example.DefaultApplication;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ftp.FTPDataSource;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.thirdparty.protocol.ocp2.OCP2DataSource;

public class Test {
	public static void main(String[] args) {
		try {
			DefaultApplication app = new DefaultApplication();
			DataSourceFactory dsf = app.getDesigner().get(DataSourceFactory.class);
			dsf.getDesigner().replace(OCPDataSource.class, new OCP2DataSource());
			dsf.getDesigner().remove(FTPDataSource.class);
			JLG.debug_on();
			JLG.debug("test");
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
