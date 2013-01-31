package org.thirdparty.protocol.ocp2;

import java.util.ResourceBundle;

import org.ocpteam.interfaces.IDataStore;
import org.ocpteam.misc.LOG;
import org.ocpteamx.protocol.ocp.OCPDataSource;

public class OCP2DataSource extends OCPDataSource {

	public OCP2DataSource() throws Exception {
		super();
		replaceComponent(IDataStore.class, new ThirdPartyPersistentMap());
	}
	
	@Override
	public String getProtocolName() {
		return "OCP2";
	}
	
	@Override
	public ResourceBundle getResource(String subpackage) throws Exception {
		String packageString = OCPDataSource.class.getPackage()
				.getName()
				+ "." + subpackage.toLowerCase();
		String resourceClassString = packageString + "."
				+ "OCP" + subpackage.toUpperCase()
				+ "Resource";
		LOG.info("class=" + resourceClassString);
		return (ResourceBundle) Class.forName(resourceClassString)
				.newInstance();

	}

}
