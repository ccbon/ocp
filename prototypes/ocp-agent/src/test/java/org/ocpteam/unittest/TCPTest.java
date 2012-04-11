package org.ocpteam.unittest;

import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.ocpteam.component.NATTraversal;
import org.ocpteam.component.TCPListener;
import org.ocpteam.example.MinimalistProtocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
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
			tcplistener.setUrl(new URI("tcp://localhost:23456"));
			tcplistener.setProtocol(protocol);
			tcplistener.start();

			TCPClient tcpclient = new TCPClient("localhost", 23456, protocol);
			while (i < n) {
				String response = (String) tcpclient.request("hello");
				JLG.debug("response[" + i + "]=" + response);
				i++;
			}

			tcplistener.stop();
			tcplistener.start();
			i = 0;
			while (i < n) {
				String response = (String) tcpclient.request("hello");
				JLG.debug("response[" + i + "]=" + response);
				i++;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
