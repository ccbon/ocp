package com.guenego.ocp;

import com.guenego.misc.Id;
import com.guenego.misc.URL;

public abstract class Channel {

	public static Channel getInstance(URL url) {
		if (url.getProtocol().equalsIgnoreCase("tcp")) {
			return new TCPChannel(url);
		} else {
			return new UnknownChannel(url);
		}
	}
	
	public abstract String request(String string) throws Exception;

	public abstract Id ping() throws Exception;

}
