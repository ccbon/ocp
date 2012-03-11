package org.ocpteam.ejder;

import javax.annotation.PostConstruct;

import org.ocpteam.functionality.NaivePersistentMap;

public class EjderPersistenceMap extends NaivePersistentMap  {
	public EjderPersistenceMap(){
		
	}
	
	@PostConstruct
	public void test(){
		// Implement the logic after this object has been injected
	}
}
