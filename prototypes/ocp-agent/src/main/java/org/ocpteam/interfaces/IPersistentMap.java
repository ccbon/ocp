package org.ocpteam.interfaces;



/**
 * A map which content stays on the hard drive when process completed or died.
 *
 */
public interface IPersistentMap extends IDataStore {
	/**
	 * Set the root directory on the hard drive file system.
	 * @param uri
	 * @throws Exception
	 */
	public void setURI(String uri) throws Exception;
}
