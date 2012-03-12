package org.ocpteam.core;

import org.ocpteam.functionality.DataSourceFactory;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ftp.FTPDataSource;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.protocol.sftp.SFTPDataSource;
import org.ocpteam.protocol.zip.ZipDataSource;
import org.ocpteam.ui.swt.DataSourceWindow;

public class DefaultApplication extends Application {

	public DefaultApplication() throws Exception {
		super();
		designer.add(DataSourceFactory.class);
		DataSourceFactory dsf = designer.get(DataSourceFactory.class);
		dsf.designer.add(OCPDataSource.class);
		dsf.designer.add(FTPDataSource.class);
		dsf.designer.add(SFTPDataSource.class);
		dsf.designer.add(ZipDataSource.class);

		designer.add(DataSourceWindow.class);
	}
	
	public void start() {
		JLG.debug_on();
		try {
			designer.get(DataSourceWindow.class).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
