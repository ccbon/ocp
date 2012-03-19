package org.ocpteam.interfaces;

/**
 * A INATTraversal job is to allow a listener to be accessible from the internet
 * by doing NAT Traversal
 *
 */
public interface INATTraversal {
	public void setPort(int port);
	public void map();
	public void unmap();
}
