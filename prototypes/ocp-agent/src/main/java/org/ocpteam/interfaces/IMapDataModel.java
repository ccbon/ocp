package org.ocpteam.interfaces;

import java.util.Set;

public interface IMapDataModel extends IDataModel {
	
	void set(String key, String value) throws Exception;
	
	String get(String key) throws Exception;
	
	void remove(String key) throws Exception;

	Set<String> keySet() throws Exception;

}
