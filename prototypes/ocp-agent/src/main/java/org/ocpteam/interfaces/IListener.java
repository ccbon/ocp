package org.ocpteam.interfaces;

import java.net.URI;

import org.ocpteam.core.IContainer;

public interface IListener extends IContainer {

	void start() throws Exception;

	void stop();

	URI getUrl();
	
	void setUrl(URI url);
	
	void setProtocol(IProtocol p);

}
