package org.ocpteam.storage;

import java.util.Collection;

public interface FileInterface {

	Collection<? extends FileInterface> listFiles();

	boolean isFile();

	boolean isDirectory();

	String getName();

}
