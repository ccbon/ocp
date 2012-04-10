package org.ocpteam.interfaces;

import java.net.URI;

import org.ocpteam.core.IComponent;

public interface IListener extends IComponent {

	void start() throws Exception;

	void stop();

	URI getUrl();
	
	void setUrl(URI url);
	
	void setProtocol(IProtocol p);

}
