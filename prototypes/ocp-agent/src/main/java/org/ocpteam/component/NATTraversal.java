package org.ocpteam.component;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ocpteam.core.Component;
import org.ocpteam.interfaces.INATTraversal;
import org.ocpteam.misc.JLG;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

/**
 * This class is doing NAT Traversal using the uPnP teleal cling library.
 * 
 */
public class NATTraversal extends Component implements INATTraversal {

	private UpnpService upnpService;
	private int port;

	public void setPort(int port) {
		this.port = port;
	}

	public void map() {
		// do that in a standalone thread.
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (NATTraversal.this) {
					JLG.debug("start synchronized on " + NATTraversal.this);
					try {
						
						// be silent and try to do only your job...
						Logger.getLogger("org.teleal.cling")
								.setLevel(Level.OFF);

						InetAddress addr = InetAddress.getLocalHost();
						JLG.debug("my hostname:" + addr.getHostName());
						JLG.debug("my ip:" + addr.getHostAddress());
						PortMapping desiredMapping = new PortMapping(port,
								addr.getHostAddress(),
								PortMapping.Protocol.TCP, "OCP Agent Mapping");

						upnpService = new UpnpServiceImpl(
								new PortMappingListener(desiredMapping));

						upnpService.getControlPoint().search();
						JLG.debug("NAT run done.");
					} catch (Exception e) {
						e.printStackTrace();
					}
					JLG.debug("stop synchronized on " + NATTraversal.this);
				}
			}
		}).start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				JLG.debug("NAT Traversal: hook on exit...");
				unmap();
			}
		});
	}

	public void unmap() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				JLG.debug("unmap on " + NATTraversal.this);
				synchronized (NATTraversal.this) {
					JLG.debug("start sync on " + NATTraversal.this);
					if (upnpService != null) {
						JLG.debug("About to stop nat traversal for port "
								+ port + ".");
						upnpService.shutdown();
						upnpService = null;
					}
					JLG.debug("stop sync on " + NATTraversal.this);
				}
			}
		}).start();
	}

}
