package org.ocpteam.protocol.ocp;

import org.ocpteam.misc.URL;

public interface Listener {

	void start();

	void stop();

	URL getUrl();

}
