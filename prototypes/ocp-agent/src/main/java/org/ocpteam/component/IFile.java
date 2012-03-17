package org.ocpteam.component;

import java.util.Collection;

public interface IFile {

	Collection<? extends IFile> listFiles();

	boolean isFile();

	boolean isDirectory();

	String getName();

}
