package org.ocpteam.example;

import org.ocpteam.component.DataSourceFactory;
import org.ocpteam.core.TopContainer;
import org.ocpteam.misc.JLG;
import org.ocpteam.ui.swt.DataSourceWindow;
import org.ocpteamx.protocol.dht0.DHTDataSource;
import org.ocpteamx.protocol.dht1.DHT1DataSource;
import org.ocpteamx.protocol.dht4.DHT4DataSource;
import org.ocpteamx.protocol.dht5.DHT5DataSource;
import org.ocpteamx.protocol.dht5.DHT5v2DataSource;
import org.ocpteamx.protocol.dht5.DHT5v3DataSource;
import org.ocpteamx.protocol.dropbox.DropboxDataSource;
import org.ocpteamx.protocol.ftp.FTPDataSource;
import org.ocpteamx.protocol.gdrive.GDriveDataSource;
import org.ocpteamx.protocol.map.MapDataSource;
import org.ocpteamx.protocol.ocp.OCPDataSource;
import org.ocpteamx.protocol.sftp.SFTPDataSource;
import org.ocpteamx.protocol.zip.ZipDataSource;

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
		dsf.addComponent(DHT4DataSource.class);
		dsf.addComponent(DHT5DataSource.class);
		dsf.addComponent(DHT5v2DataSource.class);
		dsf.addComponent(DHT5v3DataSource.class);
		dsf.addComponent(GDriveDataSource.class);
		dsf.addComponent(DropboxDataSource.class);
		
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
