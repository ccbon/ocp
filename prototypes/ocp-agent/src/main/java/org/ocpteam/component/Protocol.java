package org.ocpteam.component;

import org.ocpteam.interfaces.IProtocol;
import org.ocpteam.interfaces.IStreamSerializer;

public abstract class Protocol extends DataSourceContainer implements IProtocol {

	private IStreamSerializer streamSerializer;

	@Override
	public IStreamSerializer getStreamSerializer() {
		if (streamSerializer == null) {
			streamSerializer = new StreamSerializer();
		}
		return streamSerializer;
	}
	

}
