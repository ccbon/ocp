package org.ocpteam.interfaces;

import java.util.Map;

import org.ocpteam.serializable.Address;

/**
 * A map which content stays on the hard drive when process completed or died.
 *
 */
public interface IPersistentMap extends Map<Address, byte[]> {
	/**
	 * Set the root directory on the hard drive file system.
	 * @param root
	 * @throws Exception
	 */
	public void setRoot(String root) throws Exception;
}
