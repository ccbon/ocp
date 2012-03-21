package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.core.TopContainer;
import org.ocpteam.protocol.dht.DHTDataSource;

public class DHTStress extends TopContainer {
	
	public static void main(String args[]) {
		try {
			DHTStress app = new DHTStress();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DHTStress() throws Exception {
		getDesigner().add(DHTDataSource.class);
	}

	private void start() throws Exception {
		// instanciate N DHT DataSource
		int dsNbr = 10;
		DHTDataSource[] dsa = new DHTDataSource[dsNbr];
		for (int i = 0; i < dsNbr; i++) {
			Class<? extends DHTDataSource> c = getDesigner().get(DHTDataSource.class).getClass();
			dsa[i] = c.newInstance();
			Properties p = new Properties();
			dsa[i].setConfig(p);
			dsa[i].connect();
		}
		
	}

}
