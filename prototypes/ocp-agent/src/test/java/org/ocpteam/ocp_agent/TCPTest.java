package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertTrue;

import org.ocpteam.component.NATTraversal;
import org.ocpteam.component.TCPListener;
import org.ocpteam.example.MinimalistProtocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;
import org.ocpteam.network.TCPClient;

public class TCPTest {

	@org.junit.Test
	public void simple() {
		try {
			assertTrue(new TCPTest().test());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean test() {
		try {
			int i = 0;
			int n = 3;
			JLG.debug_on();
			IProtocol protocol = new MinimalistProtocol();
			TCPListener tcplistener = new TCPListener();
			tcplistener.removeComponent(NATTraversal.class);
			tcplistener.init();
			tcplistener.setUrl(new URL("tcp://localhost:23456"));
			tcplistener.setProtocol(protocol);
			tcplistener.start();

			TCPClient tcpclient = new TCPClient("localhost", 23456, protocol);
			while (i < n) {
				byte[] response = tcpclient.request("hello".getBytes());
				JLG.debug("response[" + i + "]=" + new String(response));
				i++;
			}

			tcplistener.stop();
			tcplistener.start();
			i = 0;
			while (i < n) {
				byte[] response = tcpclient.request("hello".getBytes());
				JLG.debug("response[" + i + "]=" + new String(response));
				i++;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
