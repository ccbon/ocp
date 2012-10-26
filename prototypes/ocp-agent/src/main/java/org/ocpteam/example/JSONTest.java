package org.ocpteam.example;

import org.ocpteam.component.JSONSerializer;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.ISerializer;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Address;

public class JSONTest extends TopContainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JSONTest app = new JSONTest();
			app.addComponent(ISerializer.class, new JSONSerializer());
			app.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		JLG.debug_on();
		Address a = new Address("012F12");
		Structure s = a.toStructure();
		JLG.debug("a=" + s);
		Address b = (Address) s.toObject();
		JLG.debug("b=" + b);
	}

}
