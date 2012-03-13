package org.ocpteam.functionality;

import java.util.Map;

import org.ocpteam.design.Functionality;

public interface PersistentMap extends Functionality, Map<byte[], byte[]> {
	public void setRoot(String root) throws Exception;
}
