package org.ocpteam.interfaces;

import org.ocpteam.misc.URL;

public interface IListener {

	void start();

	void stop();

	URL getUrl();
	
	void setUrl(URL url);

}
