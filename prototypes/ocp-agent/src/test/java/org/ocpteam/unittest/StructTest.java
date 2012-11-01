package org.ocpteam.unittest;

import java.io.Serializable;
import java.net.Socket;

import javax.crypto.SecretKey;

import org.junit.Test;
import org.ocpteam.component.JSONMarshaler;
import org.ocpteam.component.Protocol;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.Content;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.InputMessage;
import org.ocpteam.serializable.Node;
import org.ocpteam.serializable.Pointer;
import org.ocpteam.serializable.SecureUser;
import org.ocpteam.serializable.Tree;
import org.ocpteam.serializable.TreeEntry;

public class StructTest extends TopContainer {
	@Test
	public void mytest() {
		JLG.debug_on();
		try {
			testNode();
//			testContact();
//			testContent();
//			testEOMObject();
//			testPointer();
//			testSecureUser();
//			testTreeEntry();
//			testTree();
//			testInputFlow();
//			testInputMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testInputMessage() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		InputMessage t = new InputMessage(new ITransaction() {

			@Override
			public Serializable run(Session session, Serializable[] objects)
					throws Exception {
				return null;
			}

			@Override
			public String getName() {
				return "Yannis";
			}

			@Override
			public int getId() {
				return 14;
			}
		}, new TreeEntry("coucou", new Pointer("0123"), TreeEntry.FILE));
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testInputFlow() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		InputFlow t = new InputFlow(new IActivity() {

			@Override
			public void run(Session session, Serializable[] objects,
					Socket socket, Protocol protocol) throws Exception {
			}

			@Override
			public int getId() {
				return 14;
			}
		}, new TreeEntry("coucou", new Pointer("0123"), TreeEntry.FILE));
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testTree() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		Tree t = new Tree();
		t.addFile("file1", new Pointer("0123"));
		t.addFile("file2", new Pointer("4567"));
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testTreeEntry() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		TreeEntry t = new TreeEntry("file", new Pointer("0123"), TreeEntry.FILE);
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testSecureUser() throws Exception {
		SecretKey secretKey = null;
		JSONMarshaler marshaler = new JSONMarshaler();
		SecureUser su = new SecureUser();
		su.setSecretKey(secretKey);
		Structure s = su.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testPointer() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		Pointer p = new Pointer(new Id("0123"));
		Structure s = p.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testEOMObject() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		EOMObject eom = new EOMObject();
		Structure s = eom.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testContent() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		Content c = new Content("Yannis", new byte[] { 01, 23 }, new byte[] {
				98, 76 });
		Structure s = c.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testContact() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		Node n = new Node(new Id("0123"), 3);
		Contact c = new Contact();
		c.setHost("localhost");
		c.setName("Yannis");
		c.setNode(n);
		c.setTcpPort(12345);
		c.setUdpPort(67890);
		Structure s = c.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}

	public void testNode() throws Exception {
		JSONMarshaler marshaler = new JSONMarshaler();
		Node n = new Node(new Id("0123"), 3);
		Structure s = n.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b);
	}
}
