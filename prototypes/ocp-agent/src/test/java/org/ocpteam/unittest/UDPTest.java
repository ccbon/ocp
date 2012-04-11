package org.ocpteam.unittest;

import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.ocpteam.component.NATTraversal;
import org.ocpteam.component.UDPListener;
import org.ocpteam.example.MinimalistProtocol;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.network.UDPClient;

public class UDPTest {

	@org.junit.Test
	public void simple() {
		try {
			assertTrue(new UDPTest().test());
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
			UDPListener udplistener = new UDPListener();
			udplistener.removeComponent(NATTraversal.class);
			udplistener.init();
			udplistener.setUrl(new URI("tcp://localhost:23456"));
			udplistener.setProtocol(protocol);
			udplistener.start();

			UDPClient udpclient = new UDPClient("localhost", 23456);
			while (i < n) {
				String s = "hello" + i;
				udpclient.send(s.getBytes());
				i++;
			}

			udplistener.stop();
			udplistener.start();
			i = 0;
			while (i < n) {
				String s = "hello" + i;
				udpclient.send(s.getBytes());
				i++;
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
