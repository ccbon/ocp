package org.ocpteam.component;

import java.util.List;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.protocol.ocp.Listener;

/**
 * A server is a set of listener. Starting the server starts automatically all its listeners.
 * Idem for stopping.
 *
 */
public class Server implements IComponent, IServer {

	protected DataSource ds;
	protected boolean bIsStarted = false;
	
	public List<Listener> listenerList;

	@Override
	public void setParent(IContainer parent) {
		this.ds = (DataSource) parent;
	}

	@Override
	public boolean isStarted() {
		return bIsStarted;
	}

	@Override
	public void start() throws Exception {
		//TODO : start the listeners
		bIsStarted = true;
		
	}

	@Override
	public void stop() throws Exception {
		//TODO : stop the listeners
		bIsStarted  = false;
	}

}
