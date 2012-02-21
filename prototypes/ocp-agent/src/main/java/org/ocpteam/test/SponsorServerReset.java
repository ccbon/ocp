package org.ocpteam.test;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.protocol.ocp.OCPAgent;

public class SponsorServerReset {
	public static void main(String[] args) {
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(OCPAgent.DEFAULT_SPONSOR_SERVER_URL));
			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);
			String result = (String) client.execute("reset",
					new Object[] {});
			System.out.println("result = " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
