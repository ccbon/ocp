package org.thirdparty.protocol.ocp2;

import java.util.ResourceBundle;

import org.ocpteam.component.PersistentMap;
import org.ocpteam.misc.JLG;
import org.ocpteam.protocol.ocp.OCPDataSource;

public class OCP2DataSource extends OCPDataSource {

	public OCP2DataSource() throws Exception {
		super();
		getDesigner().add(PersistentMap.class, new ThirdPartyPersistentMap());
	}
	
	@Override
	public String getProtocol() {
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
		JLG.debug("class=" + resourceClassString);
		return (ResourceBundle) Class.forName(resourceClassString)
				.newInstance();

	}

}
