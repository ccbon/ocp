package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht.DHTDataModel;
import org.ocpteam.protocol.dht.DHTDataSource;

public class DHTInterestingStress extends TopContainer {

	public static void main(String[] args) {
		try {
			DHTInterestingStress app = new DHTInterestingStress();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int n;
	private int port;
	private double availabilityRate;
	private DHTDataSource[] ds;
	public boolean stopNow = false;
	private int duration;
	private long activity_sleep;
	

	public DHTInterestingStress() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 10;
		duration = 1500;
		port = 40000;
		availabilityRate = 0.9;
		activity_sleep = 100;
		JLG.debug_on();
		JLG.bUseSet = true;
		//JLG.set.add(TCPServer.class.getName());
		//JLG.set.add(DHTInterestingStress.class.getName());
		//JLG.set.add(DHTDataModel.class.getName());
		//JLG.set.add(TCPClient.class.getName());
		//JLG.set.add(JLG.class.getName());
		//JLG.set.add(Client.class.getName());
		//JLG.set.add(NATTraversal.class.getName());
	}

	protected void activity() throws Exception {
		Thread.sleep(activity_sleep);
		for (int i = 0; i < n; i++) {
			if (ds[i].isConnected()) {
				System.out.print("" + i);
			} else {
				System.out.print(" ");
			}	
		}
		System.out.println();
		Context c = null;
		int r = 0;
		while (c == null) {
			JLG.debug("try to pick up a connected datasource");
			r = JLG.random(n);
			DataSource d = ds[r];
			c = d.getContext();
			if (stopNow == true) {
				return;
			}
		}
		JLG.debug("found datasource=" + r);
		DHTDataModel dht = (DHTDataModel) c.getDataModel();
		dht.set("key" + JLG.random(100), "value" + JLG.random(100));
		
		c = null;
		while (c == null) {
			JLG.debug("try to pick up a connected datasource 2");
			r = JLG.random(n);
			DataSource d = ds[r];
			c = d.getContext();
			if (stopNow == true) {
				return;
			}

		}
		JLG.debug("found datasource=" + r);
		//JLG.debug("keyset size: " + dht.keySet().size());
		//String key = "key" + JLG.random(100);
		//JLG.debug("getting " + key + " : " + dht.get(key));
	}

	public void start() throws Exception {
		ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			Class<? extends DHTDataSource> c = getComponent(DHTDataSource.class)
					.getClass();
			ds[i] = c.newInstance();
			ds[i].init();
			ds[i].setName("ds_" + i);
			// unfortunately, the teleal library does not work well with many
			// threads...
			ds[i].listener.removeComponent(NATTraversal.class);
		}
		// first agent
		Properties p = new Properties();
		p.setProperty("agent.isFirst", "yes");
		p.setProperty("listener.tcp.url", "tcp://localhost:" + port);
		ds[0].setConfig(p);

		// other agents
		for (int i = 1; i < n; i++) {
			p = new Properties();
			p.setProperty("agent.isFirst", "no");
			int port_i = port + i;
			p.setProperty("listener.tcp.url", "tcp://localhost:" + port_i);
			p.setProperty("sponsor.1", "tcp://localhost:" + port);
			ds[i].setConfig(p);

		}

		// start all one after the other
		for (int i = 0; i < n; i++) {
			ds[i].connect();
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size());
			//JLG.println("ds[" + i + "] contact map : " + cm);
		}
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.println("ds[" + i + "] contact map size: " + cm.size());
			//JLG.println("ds[" + i + "] contact map : " + cm);
		}
		

		// run availability thread.
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (stopNow == false) {
					DHTInterestingStress.this.insureAvailability();
				}
			}
		}, "insureAvailability");

		t.start();

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				while (stopNow == false) {
					try {
						DHTInterestingStress.this.activity();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, "activity");
		t2.start();

		Thread.sleep(duration * 1000);
		stopNow = true;

		for (int i = 0; i < n; i++) {
			JLG.debug("disconnecting " + i);
			ds[i].disconnect();
		}
		JLG.debug("app finished");
		JLG.debug("stopNow=" + stopNow);
		//JLG.showActiveThreads();
		
	}

	protected void insureAvailability() {
		// every second, make a decision to switch the status of an agent.
		try {
			Thread.sleep(1000);
			JLG.debug("wake up");
			for (int i = 1; i < ds.length; i++) {
				DataSource d = ds[i];
				if (d.isConnected()) {
					// take a chance to disconnect
					double r = Math.random();
					if (r > availabilityRate) {
						d.disconnect();
					}
				} else {
					double r = Math.random();
					if (r <= availabilityRate && stopNow == false) {
						d.connect();
					}
				}
			}
		} catch (Exception e) {
			if (!stopNow) {
				e.printStackTrace();
			}
		}
	}

}
