package org.ocpteam.test;

import java.net.URL;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ocpteam.ocp.OCPAgent;

public class SponsorServerList {
	public static void main(String[] args) {
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(new URL(OCPAgent.DEFAULT_SPONSOR_SERVER_URL));
			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);
			
			Object[] result = (Object[]) client.execute("list",
					new Object[] {});
			System.out.println("result = " + result.getClass());
			System.out.println("result = " + result.length);
			for (int i = 0; i < result.length; i++) {
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) result[i];
				System.out.println("result = " + result[i]);
				System.out.println("result = " + result[i].getClass());
				System.out.println("url = " + map.get("url"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
