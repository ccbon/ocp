package org.ocpteam.component;

import org.ocpteam.core.Component;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.URL;

public class TCPListener extends Component implements IListener {

	private URL url;

	@Override
	public void start() {
		JLG.debug("starting a tcp listener on url: " + url);

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public void setUrl(URL url) {
		this.url = url;
	}

}
