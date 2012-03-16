package org.ocpteam.component;

import java.util.Map;

import org.ocpteam.core.IComponent;

public interface PersistentMap extends IComponent, Map<byte[], byte[]> {
	public void setRoot(String root) throws Exception;
}
