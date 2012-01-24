package org.ocp.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class IPAddress {
	
	
	private static String ip;

	public static String getPublic() throws Exception {
		if (ip == null) {
			InputStream inputStream = null;
			URL whatismyip = new URL(
					"http://automation.whatismyip.com/n09230945.asp");
			try {
				inputStream = whatismyip.openStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				ip = in.readLine(); // you get the IP as a String
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		return ip;
	}

}
