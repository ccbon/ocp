package org.ocpteam.example;

import java.util.Properties;

import org.ocpteam.component.NATTraversal;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.MessageSerializer;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.dht.DHTDataModel;
import org.ocpteam.protocol.dht.DHTDataSource;

public class DHTSimpleStress extends TopContainer {

	public static void main(String[] args) {
		try {
			DHTSimpleStress app = new DHTSimpleStress();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int n;
	private int port;

	public DHTSimpleStress() throws Exception {
		addComponent(DHTDataSource.class);
	}

	@Override
	public void init() throws Exception {
		super.init();
		n = 2;
		port = 35000;
		JLG.debug_on();
		JLG.bUseSet = true;
		//JLG.set.add(TCPServer.class.getName());
		JLG.set.add(DHTSimpleStress.class.getName());
		//JLG.set.add(JLG.class.getName());
		JLG.set.add(MessageSerializer.class.getName());
		//JLG.set.add(NATTraversal.class.getName());
	}

	public void start() throws Exception {	
		final DHTDataSource[] ds = new DHTDataSource[n];
		
		Runnable run1 = new Runnable() {
			int counter = 1;
			
			@Override
			public void run() {
				try {
					bootstrap();
					int i = 0;
					while(true){
						doMassiveSetOperations();
						//Thread.sleep(20);
						//System.gc();
						JLG.println("Stress test is running..." + i);
						i++;
					}
				} catch (Exception e) {
					JLG.debug("Threads: " + Thread.activeCount());
					e.printStackTrace();
				} finally {
					for (int i = 0; i < n; i++) {
						try {
							JLG.debug("disconnecting " + i);
							ds[i].disconnect();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			
			public void bootstrap() throws Exception{
				for (int i = 0; i < n; i++) {
					Class<? extends DHTDataSource> c = getComponent(DHTDataSource.class)
							.getClass();
					ds[i] = c.newInstance();
					ds[i].init();
					//unfortunately, the teleal library does not work well with many threads...
					ds[i].listener.removeComponent(NATTraversal.class);
				}
				// first agent
				Properties p = new Properties();
				p.setProperty("agent.isFirst", "yes");
				p.setProperty("listener.tcp.url", "tcp://localhost:" + port);
				ds[0].setConfig(p);
				ds[0].connect();

				// other agents
				for (int i = 1; i < n; i++) {
					p = new Properties();
					p.setProperty("agent.isFirst", "no");
					int port_i = port + i;
					p.setProperty("listener.tcp.url", "tcp://localhost:" + port_i);
					p.setProperty("sponsor.1", "tcp://localhost:" + port);
					ds[i].setConfig(p);
					ds[i].connect();
				}			
			}
			
			public void doMassiveSetOperations() throws Exception{
				//int cyclicCounter = (counter % 100);
				for (int i = 0; i < n; i++) {
					DHTDataModel dht = (DHTDataModel) ds[i].getContext().getDataModel();
					dht.set("key" + i + counter, "value" + i + counter);
				}
				
				for (int i = 0; i < n; i++) {
					DHTDataModel dht = (DHTDataModel) ds[i].getContext().getDataModel();
					JLG.debug("size=" + dht.keySet().size());
					//int j = 9 - i;
					//JLG.debug("key" + j + " = " + dht.get("key" + j));
				}	
				counter++;
				counter=counter % 10000;
			}
		};

		run1.run();
		
		
		JLG.debug("app finished");
	}

}
