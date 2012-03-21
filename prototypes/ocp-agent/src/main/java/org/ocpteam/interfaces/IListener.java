package org.ocpteam.interfaces;

import org.ocpteam.core.IComponent;
import org.ocpteam.misc.URL;

public interface IListener extends IComponent {

	void start();

	void stop();

	URL getUrl();
	
	void setUrl(URL url);
	
	void setProtocol(IProtocol p);

}
