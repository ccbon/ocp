package org.ocpteam.example;

import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.core.TopContainer;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht0.DHTDataSource;
import org.ocpteam.protocol.dht1.DHT1DataSource;
import org.ocpteam.protocol.ftp.FTPDataSource;
import org.ocpteam.protocol.map.MapDataSource;
import org.ocpteam.protocol.ocp.OCPDataSource;
import org.ocpteam.protocol.sftp.SFTPDataSource;
import org.ocpteam.protocol.zip.ZipDataSource;
import org.ocpteam.ui.swt.DataSourceWindow;

public class DefaultApplication extends TopContainer {
	
	public static void main(String args[]) {
		try {
			DefaultApplication app = new DefaultApplication();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultApplication() throws Exception {
		super();
		addComponent(DataSourceFactory.class);
		DataSourceFactory dsf = getComponent(DataSourceFactory.class);
		dsf.addComponent(OCPDataSource.class);
		dsf.addComponent(FTPDataSource.class);
		dsf.addComponent(SFTPDataSource.class);
		dsf.addComponent(ZipDataSource.class);
		dsf.addComponent(MapDataSource.class);
		dsf.addComponent(DHTDataSource.class);
		dsf.addComponent(DHT1DataSource.class);

		addComponent(DataSourceWindow.class);
	}

	public void start() {
		JLG.debug_on();
		try {
			getComponent(DataSourceWindow.class).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
