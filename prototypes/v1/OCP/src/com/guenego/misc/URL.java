package com.guenego.misc;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String sProtocol;
	private String sHost;
	private int iPort;
	

	public URL(String sUrl) throws JLGException {
		JLG.debug("start creating url");
		Pattern pattern = Pattern.compile("^(\\w+)://([\\w\\.]+):(\\d+).*$");
		Matcher matcher = pattern.matcher(sUrl);
		boolean found = false;
		JLG.debug("groupCount = " + matcher.groupCount());
		if (matcher.find()) {
			found = true;
			sProtocol = matcher.group(1);
			sHost = matcher.group(2);
			iPort = Integer.parseInt(matcher.group(3));
			//JLG.debug("sProtocol: " + sProtocol);
			//JLG.debug("sHost: " + sHost);
			//JLG.debug("iPort: " + iPort);
		}
		if (!found) {
			throw new JLGException("not a good url: " + sUrl);
		}
	}

	public URL(String protocol, String ip, int port) {
		this.sProtocol = protocol;
		this.sHost = ip;
		this.iPort = port;
	}

	public String getProtocol() {
		return sProtocol;
	}

	public int getPort() {
		return iPort;
	}

	public int getDefaultPort() {
		return 0;
	}

	public String getHost() {
		return sHost;
	}
	
	@Override
	public String toString() {
		return sProtocol + "://" + sHost + ":" + iPort;
	}

	public void setHost(String hostAddress) {
		sHost = hostAddress;
		
	}

}
