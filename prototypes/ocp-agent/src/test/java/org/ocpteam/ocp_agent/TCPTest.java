package org.ocpteam.ocp_agent;

import static org.junit.Assert.assertTrue;

import org.ocpteam.component.BahBahProtocol;
import org.ocpteam.component.TCPListener;
import org.ocpteam.component.TestScenario;
import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.TCPClient;
import org.ocpteam.misc.URL;

public class TCPTest extends TestScenario {

	@org.junit.Test
	public void simple() {
		try {
			assertTrue(new TCPTest().test());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean test() {
		try {
			JLG.debug_on();
			
			TCPListener tcplistener = new TCPListener();
			tcplistener.setUrl(new URL("tcp://localhost:23456"));
			tcplistener.getDesigner().add(IProtocol.class, new BahBahProtocol());
			tcplistener.start();
			
			TCPClient tcpclient = new TCPClient();
			tcpclient.setPort(23456);
			tcpclient.setHostname("localhost");
			byte[] response = tcpclient.request("hello".getBytes());
			JLG.debug("response=" + new String(response));
			
			tcplistener.stop();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
