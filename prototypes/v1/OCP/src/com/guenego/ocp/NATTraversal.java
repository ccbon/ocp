package com.guenego.ocp;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import com.guenego.misc.JLG;

public class NATTraversal {

	private InetAddress addr;
	private UpnpService upnpService;
	
	public void map(int port) {
		// be silent and try to do only your job...
		Logger.getLogger("org.teleal.cling").setLevel(Level.OFF);
		
		try {
			addr = InetAddress.getLocalHost();
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

		

		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				JLG.debug("NAT Traversal: hook on exit...");
				if (upnpService != null) {
					JLG.debug("About to shutdown the uPnP service.");
					upnpService.shutdown();
				}
			}
		});
	}

}
