package org.ocpteam.ejder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.ocpteam.functionality.NaivePersistentMap;
import org.ocpteam.functionality.PersistentMap;
import org.ocpteam.layer.rsp.DataSource;

public class EjderPersistenceMap extends NaivePersistentMap  {
	public EjderPersistenceMap(){
		
	}
	
	@PostConstruct
	public void test(){
		// Implement the logic after this object has been injected
	}
}
