package org.ocpteam.core;

import org.ocpteam.functionality.DataSourceFactory;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ftp.FTPDataSource;
import org.ocpteam.protocol.map.MapDataSource;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.protocol.sftp.SFTPDataSource;
import org.ocpteam.protocol.zip.ZipDataSource;
import org.ocpteam.ui.swt.DataSourceWindow;

public class DefaultApplication extends Application {

	public DefaultApplication() throws Exception {
		super();
		getDesigner().add(DataSourceFactory.class);
		DataSourceFactory dsf = getDesigner().get(DataSourceFactory.class);
		dsf.getDesigner().add(OCPDataSource.class);
		dsf.getDesigner().add(FTPDataSource.class);
		dsf.getDesigner().add(SFTPDataSource.class);
		dsf.getDesigner().add(ZipDataSource.class);
		dsf.getDesigner().add(MapDataSource.class);

		getDesigner().add(DataSourceWindow.class);
	}
	
	public void start() {
		JLG.debug_on();
		try {
			getDesigner().get(DataSourceWindow.class).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
