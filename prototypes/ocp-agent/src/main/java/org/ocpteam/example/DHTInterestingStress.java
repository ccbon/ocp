package org.ocpteam.example;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.ocpteam.component.ContactMap;
import org.ocpteam.component.DataSource;
import org.ocpteam.component.NATTraversal;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.Context;
import org.ocpteam.misc.JLG;
import org.ocpteamx.protocol.dht0.DHTDataModel;
import org.ocpteamx.protocol.dht0.DHTDataSource;

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
	private int duration;
	private ScheduledExecutorService scheduler;

	public DHTInterestingStress() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 10;
		duration = 5;
		port = 40000;
		availabilityRate = 0.8;
		JLG.debug_on();
		JLG.bUseSet = true;
		// JLG.set.add(TCPServer.class.getName());
		JLG.set.add(DHTInterestingStress.class.getName());
		// JLG.set.add(DHTDataModel.class.getName());
		// JLG.set.add(TCPClient.class.getName());
		JLG.set.add(JLG.class.getName());
		// JLG.set.add(Client.class.getName());
		// JLG.set.add(NATTraversal.class.getName());
	}

	protected void activity() {
		for (int i = 0; i < n; i++) {
			if (ds[i].isConnected()) {
				int size = ds[i].contactMap.size();
				String s = String.format("%2d", size);
				System.out.print("X[" + s + "]");
			} else {
				System.out.print("     ");
			}
		}
		System.out.println();
		JLG.debug("try to pick up a connected datasource");
		int r = JLG.random(n);
		DataSource d = ds[r];
		synchronized (d) {
			if (!d.isConnected()) {
				return;
			}
			try {
				JLG.debug("found datasource=" + r);
				Context c = d.getContext();
				DHTDataModel dht = (DHTDataModel) c.getDataModel();
				dht.set("key" + JLG.random(100), "value" + JLG.random(100));
				JLG.debug("keyset size: " + dht.keySet().size());
				String key = "key" + JLG.random(100);
				JLG.debug("getting " + key + " : " + dht.get(key));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void start() throws Exception {
		scheduler = Executors.newScheduledThreadPool(10);
//		scheduler.execute(new Runnable() {
//
//			@Override
//			public void run() {
//				while (true) {
//					ThreadMXBean tmx = ManagementFactory.getThreadMXBean();
//					long[] ids = tmx.findDeadlockedThreads();
//					if (ids != null) {
//						ThreadInfo[] infos = tmx.getThreadInfo(ids, true, true);
//						System.out
//								.println("The following threads are deadlocked:");
//						for (ThreadInfo ti : infos) {
//							System.out.println(ti);
//						}
//					}
//				}
//
//			}
//		});
		connectAll();

		// run availability thread.
		final Runnable insureAvailability = new Runnable() {
			@Override
			public void run() {
				try {
					JLG.debug("availability");
					for (int i = 1; i < ds.length; i++) {
						synchronized (ds[i]) {
							double r = Math.random();
							if (ds[i].isConnected()) {
								// take a chance to disconnect
								if (r > availabilityRate) {
									ds[i].disconnect();
								}
							} else {
								if (r <= availabilityRate) {
									ds[i].connect();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		final Runnable activity = new Runnable() {

			@Override
			public void run() {
				DHTInterestingStress.this.activity();
			}
		};

		
		final ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
				insureAvailability, 1, 1, TimeUnit.SECONDS);
		final ScheduledFuture<?> sf2 = scheduler.scheduleAtFixedRate(activity,
				0, 100, TimeUnit.MILLISECONDS);

		scheduler.schedule(new Runnable() {
			public void run() {
				sf.cancel(true);
				sf2.cancel(true);
			}
		}, duration, TimeUnit.SECONDS);
		Thread.sleep((duration + 1) * 1000);
		scheduler.shutdown();
		scheduler.shutdownNow();
		disconnectAll();
		JLG.debug("app finished");
		JLG.showActiveThreads();
	}

	private void disconnectAll() throws Exception {
		for (int i = 0; i < n; i++) {
			synchronized (ds[i]) {
				JLG.debug("disconnecting " + i);
				if (ds[i].isConnected()) {
					ds[i].disconnect();
				}
			}
		}
	}

	private void connectAll() throws Exception {
		ds = new DHTDataSource[n];
		for (int i = 0; i < n; i++) {
			ds[i] = getComponent(DHTDataSource.class).getClass().newInstance();
			ds[i].init();
			ds[i].setName("ds_" + i);
			// unfortunately, the teleal library does not work well with many
			// threads...
			ds[i].tcplistener.removeComponent(NATTraversal.class);
		}
		// first agent
		Properties p = new Properties();
		p.setProperty("agent.isFirst", "yes");
		p.setProperty("server.port", "" + port);
		ds[0].setConfig(p);

		// other agents
		for (int i = 1; i < n; i++) {
			p = new Properties();
			p.setProperty("agent.isFirst", "no");
			int port_i = port + i;
			p.setProperty("server.port", "" + port_i);
			p.setProperty("sponsor.1", "tcp://localhost:" + port);
			ds[i].setConfig(p);
		}

		// start all one after the other
		for (int i = 0; i < n; i++) {
			ds[i].connect();
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.debug("ds[" + i + "] contact map size: " + cm.size());
		}
		for (int i = 0; i < n; i++) {
			ContactMap cm = ds[i].getComponent(ContactMap.class);
			JLG.debug("ds[" + i + "] contact map size: " + cm.size());
		}
	}

}
