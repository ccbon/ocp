package org.ocpteam.interfaces;

import java.io.File;

/**
 * A Document is opened by an application and handled by an application.
 * It is stored on a local file system.
 * 
 */
public interface IDocument {
	public void open(File file) throws Exception;
	public void newTemp() throws Exception;
	public boolean isNew();
	public void save() throws Exception;
	public void saveAs(File file) throws Exception;
	public void close() throws Exception;
}
