package org.ocpteam.functionality;

import java.util.Map;

import org.ocpteam.design.Functionality;
import org.ocpteam.layer.rsp.DataSource;

public interface PersistentMap extends Functionality<DataSource>, Map<byte[], byte[]> {
	public void setRoot(String root) throws Exception;
}
