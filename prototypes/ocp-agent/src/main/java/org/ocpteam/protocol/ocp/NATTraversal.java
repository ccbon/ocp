package org.ocpteam.protocol.ocp;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ocpteam.misc.JLG;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;


public class NATTraversal {

	private UpnpService upnpService;
	private int port;
	
	public NATTraversal(int port) {
		this.port = port;
	}
	
	public void map() {
		// do that in a standalone thread.
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					// be silent and try to do only your job...
					Logger.getLogger("org.teleal.cling").setLevel(Level.OFF);

					InetAddress addr = InetAddress.getLocalHost();
					JLG.debug("my hostname:" + addr.getHostName());
					JLG.debug("my ip:" + addr.getHostAddress());
					PortMapping desiredMapping =
					        new PortMapping(
					        		port,
					        		addr.getHostAddress(),
					                PortMapping.Protocol.TCP,
					                "OCP Agent Mapping"
					        );

					upnpService =
					        new UpnpServiceImpl(
					                new PortMappingListener(desiredMapping)
					        );

					upnpService.getControlPoint().search();

				} catch (Exception e) {
					//e.printStackTrace();
				}
				
			}
		}).start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				unmap();
			}
		});		
	}

	public void unmap() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				JLG.debug("NAT Traversal: hook on exit...");
				if (upnpService != null) {
					JLG.debug("About to stop nat traversal for port " + port + ".");
					upnpService.shutdown();
					upnpService = null;
				}
				
			}
		}).start();
	}

}
