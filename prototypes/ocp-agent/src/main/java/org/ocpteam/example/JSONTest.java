package org.ocpteam.example;

import org.ocpteam.component.JSONMarshaler;
import org.ocpteam.core.TopContainer;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Node;

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
		LOG.debug_on();
		Node a = new Node(new Id("0123"), 3);
		Structure s = a.toStructure();
		LOG.debug("s=" + s);
		byte[] array = getComponent(IMarshaler.class).marshal(s);
		LOG.debug("array=" + new String(array));
		Structure s2 = getComponent(IMarshaler.class).unmarshal(array);
		LOG.debug("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.debug("b=" + b);
	}

}
