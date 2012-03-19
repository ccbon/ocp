package org.ocpteam.interfaces;

import java.util.List;

public interface IServer extends IStartable {
	
	public List<IListener> getListeners();

}
