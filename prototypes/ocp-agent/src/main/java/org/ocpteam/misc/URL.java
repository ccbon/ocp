package org.ocpteam.misc;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String protocol;
	private String host;
	private int port;
	

	public URL(String sUrl) throws Exception {
		Pattern pattern = Pattern.compile("^(\\w+)://([\\w\\.]+):(\\d+).*$");
		Matcher matcher = pattern.matcher(sUrl);
		boolean found = false;
		if (matcher.find()) {
			found = true;
			protocol = matcher.group(1);
			host = matcher.group(2);
			port = Integer.parseInt(matcher.group(3));
			//JLG.debug("sProtocol: " + sProtocol);
			//JLG.debug("sHost: " + sHost);
			//JLG.debug("iPort: " + iPort);
		}
		if (!found) {
			throw new Exception("not a good url: " + sUrl);
		}
	}

	public URL(String protocol, String ip, int port) {
		this.protocol = protocol;
		this.host = ip;
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public int getPort() {
		return port;
	}

	public int getDefaultPort() {
		return 0;
	}

	public String getHost() {
		return host;
	}
	
	@Override
	public String toString() {
		return protocol + "://" + host + ":" + port;
	}

	public void setHost(String hostAddress) {
		host = hostAddress;
		
	}

	public URL duplicate() {
		return new URL(protocol, host, port);
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
		
	}

}
