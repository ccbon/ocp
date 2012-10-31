package org.ocpteam.example;

import org.ocpteam.component.JSONMarshaler;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.AddressUser;

public class JSONTest extends TopContainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JSONTest app = new JSONTest();
			app.addComponent(IMarshaler.class, new JSONMarshaler());
			app.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void start() throws Exception {
		JLG.debug_on();
		AddressUser a = new AddressUser();
		a.setProperty("coucou", "kiki");
		a.setProperty("Hello", "World");
		a.setUsername("Yannis");
		a.setRootAddress(new Address("0123"));
		Structure s = a.toStructure();
		JLG.debug("s=" + s);
		byte[] array = getComponent(IMarshaler.class).marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = getComponent(IMarshaler.class).unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

}
