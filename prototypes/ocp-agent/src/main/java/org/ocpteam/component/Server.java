package org.ocpteam.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ocpteam.core.IComponent;
import org.ocpteam.core.IContainer;
import org.ocpteam.interfaces.IListener;
import org.ocpteam.interfaces.IServer;

/**
 * A server is a set of listeners. Starting the server starts automatically all its listeners.
 * Idem for stopping.
 *
 */
public class Server implements IComponent, IServer {

	protected DataSource ds;
	protected boolean bIsStarted = false;
	
	protected List<IListener> listenerList;

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
		for (Iterator<IListener> it = getListeners().iterator(); it.hasNext();) {
			it.next().start();
		}
		bIsStarted = true;
	}

	@Override
	public void stop() throws Exception {
		for (Iterator<IListener> it = listenerList.iterator(); it.hasNext();) {
			it.next().stop();
		}
		bIsStarted  = false;
	}

	@Override
	public List<IListener> getListeners() {
		if (listenerList == null) {
			listenerList = new ArrayList<IListener>();
		}
		return listenerList;
	}

}
