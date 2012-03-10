package org.ocpteam.test;

import org.ocpteam.core.DefaultCloudContainer;
import org.ocpteam.design.Designer;
import org.ocpteam.ejder.EjderAuthentication;
import org.ocpteam.ejder.EjderGUI;
import org.ocpteam.ejder.EjderPersistenceMap;
import org.ocpteam.functionality.Authentication;
import org.ocpteam.functionality.NaivePersistentMap;

public class TestEjder {
	public static void main(String[] args){
		DefaultCloudContainer cloudContainer1 = new DefaultCloudContainer();
		
		cloudContainer1.use(EjderAuthentication.class).insteadOf(Authentication.class)
						.use(EjderPersistenceMap.class).insteadOf(NaivePersistentMap.class)
						.use(EjderGUI.class).insteadOfDefault();
		
		cloudContainer1.start();
	}
}
