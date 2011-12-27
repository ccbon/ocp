package com.guenego.ocp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import com.guenego.misc.JLG;

public class NATTraversal {

	private InetAddress addr;
	private UpnpService upnpService;
	
	public void map(int port) {
		// TODO Auto-generated method stub
		
		
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

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				JLG.debug("About to remove nat traversal upnp");
				if (upnpService != null) {
					upnpService.shutdown();
				}
			}
		});
	}

}
