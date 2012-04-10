package org.ocpteam.example;

import java.net.URI;

import org.ocpteam.component.TCPListener;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.network.TCPClient;

public class DHTConnectStress extends TopContainer {

	public static void main(String[] args) {
		try {
			DHTConnectStress app = new DHTConnectStress();
			app.init();
			app.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws Exception {
		super.init();
		JLG.debug_on();
	}

	public void start() throws Exception {
		IProtocol protocol = new MinimalistProtocol();
		TCPListener tcplistener = new TCPListener();
		tcplistener.init();
		tcplistener.setProtocol(protocol);
		tcplistener.setUrl(new URI("tcp://localhost:12345"));
		tcplistener.start();
		
		TCPClient tcpClient = new TCPClient("localhost", 12345, protocol);
		byte[] response = tcpClient.request("coucou".getBytes());
		JLG.debug("response=" + new String(response));
		tcplistener.stop();
		response = tcpClient.request("coucou".getBytes());
		JLG.debug("response=" + new String(response));
		
	}



}
