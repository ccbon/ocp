package org.ocpteam.protocol.ocp;

import org.ocpteam.component.Authentication;
import org.ocpteam.component.DataSource;

public class OCPAuthentication extends Authentication {
	
	public OCPAuthentication() {
		super();
	}
	
	public OCPAuthentication(DataSource ds, String username, Object challenge) {
		super(ds, username, challenge);
	}

	@Override
	public boolean allowsUserCreation() {
		return true;
	}
}
