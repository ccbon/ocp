package org.ocpteam.example;

import java.util.Properties;

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
	private boolean stopNow = false;
	private int duration;
	private long activity_sleep;

	public DHTInterestingStress() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 10;
		duration = 15;
		port = 40000;
		availabilityRate = 0.7;
		activity_sleep = 100;
		JLG.debug_on();
		JLG.bUseSet = true;
		//JLG.set.add(TCPServer.class.getName());
		JLG.set.add(DHTInterestingStress.class.getName());
		//JLG.set.add(NATTraversal.class.getName());
	}

	protected void activity() throws Exception {
		Thread.sleep(activity_sleep);
		Context c = null;
		while (c == null) {
			DataSource d = ds[JLG.random(n)];
			c = d.getContext();
		}
		DHTDataModel dht = (DHTDataModel) c.getDataModel();
		dht.set("key" + JLG.random(100), "value" + JLG.random(100));
		
		c = null;
		while (c == null) {
			DataSource d = ds[JLG.random(n)];
			c = d.getContext();
		}
		JLG.debug(dht.keySet().toString());
		String key = "key" + JLG.random(100);
		JLG.debug("getting " + key + " : " + dht.get(key));
	}

	public void start() throws Exception {
		ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			Class<? extends DHTDataSource> c = getComponent(DHTDataSource.class)
					.getClass();
			ds[i] = c.newInstance();
			ds[i].init();
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

		// start all
		for (int i = 0; i < n; i++) {
			ds[i].connect();
		}

		// run availability thread.
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (stopNow == false) {
					DHTInterestingStress.this.insureAvailability();
				}
			}
		});

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
		});
		t2.start();

		Thread.sleep(duration * 1000);
		stopNow = true;

		for (int i = 0; i < n; i++) {
			ds[i].disconnect();
		}
		JLG.debug("app finished");
	}

	protected void insureAvailability() {
		// every second, make a decision to switch the status of an agent.
		try {
			Thread.sleep(1000);
			for (DataSource d : ds) {
				if (d.isConnected()) {
					// take a chance to disconnect
					double r = Math.random();
					if (r > availabilityRate) {
						d.disconnect();
					}
				} else {
					double r = Math.random();
					if (r <= availabilityRate) {
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
