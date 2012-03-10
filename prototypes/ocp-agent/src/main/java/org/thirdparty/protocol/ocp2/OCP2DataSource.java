package org.thirdparty.protocol.ocp2;

import org.ocpteam.functionality.PersistentMap;
import org.ocpteam.protocol.ocp.OCPDataSource;

public class OCP2DataSource extends OCPDataSource {

	public OCP2DataSource() throws Exception {
		super();
		designer.add(PersistentMap.class, new ThirdPartyPersistentMap());
	}

}
