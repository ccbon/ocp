package org.ocpteam.protocol.ocp;

import org.ocpteam.layer.rsp.Authentication;
import org.ocpteam.layer.rsp.DataSource;

public class OCPAuthentication extends Authentication {

	public OCPAuthentication(DataSource ds) {
		super(ds);
	}
	
	@Override
	public boolean allowsUserCreation() {
		return true;
	}
}
