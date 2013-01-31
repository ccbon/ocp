package org.ocpteam.unittest;

import java.io.Serializable;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;
import org.ocpteam.component.FListMarshaler;
import org.ocpteam.component.FListSerializer;
import org.ocpteam.component.Protocol;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;
import org.ocpteam.misc.SField;
import org.ocpteam.misc.Structure;
import org.ocpteam.serializable.Address;
import org.ocpteam.serializable.Contact;
import org.ocpteam.serializable.Content;
import org.ocpteam.serializable.EOMObject;
import org.ocpteam.serializable.InputFlow;
import org.ocpteam.serializable.InputMessage;
import org.ocpteam.serializable.Node;
import org.ocpteam.serializable.Pointer;
import org.ocpteam.serializable.RootContent;
import org.ocpteam.serializable.SecureUser;
import org.ocpteam.serializable.Tree;
import org.ocpteam.serializable.TreeEntry;
import org.ocpteam.serializable.User;

public class StructTest extends TopContainer {
	@Test
	public void mytest() {
		LOG.debug_on();
		LOG.bUseSet = true;
		LOG.set.add(StructTest.class.getName());
		LOG.set.add(InputMessage.class.getName());
		try {
			testRootContent();
//			testProperties();
			// testEqual();
			// testFields();
			// testStructure();
			// testNode();
			// testContact();
			// testContent();
			// testEOMObject();
			// testPointer();
			// testEmptySecureUser();
			// testTreeEntry();
			// testTree();
			// testInputFlow();
//			testInputMessage();
			// testSecureUser();/
//			testUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void testRootContent() throws Exception {
		RootContent rc = new RootContent();
		Address addr1 = new Address("0123");
		rc.getMap().put("key1", addr1 );
		Address addr2 = new Address("1234");
		rc.getMap().put("key2", addr2  );
		byte[] array = new FListSerializer().serialize(rc);
		LOG.info("array=" + JLG.NL + new String(array));
		RootContent rc2 = (RootContent) new FListSerializer().deserialize(array);
		LOG.info("rc2=" + rc2);
		LOG.info("b=" + rc2.getMap().equals(rc.getMap()));
	}

	public void testProperties() throws Exception {
		Properties p = new Properties();
		p.setProperty("hello", "world");
		p.setProperty("key1", "value1");
		byte[] array = new FListSerializer().serialize(p);
		LOG.info("array=" + JLG.NL + new String(array));
		Properties p2 = (Properties) new FListSerializer().deserialize(array);
		LOG.info("p2=" + p2);
		LOG.info("b=" + p2.equals(p));
	}

	public void testUser() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		User u = new User();
		u.setProperty("hello", "world");
		u.setProperty("key1", "value1");
		Structure s = u .toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + JLG.NL + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(u.toStructure()));
	}

	public void testSecureUser() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		SecureUser su = new SecureUser();

		KeyGenerator kg = KeyGenerator.getInstance(su.getSecretKeyAlgo());
		SecretKey secretKey = kg.generateKey();
		su.setSecretKey(secretKey);

		KeyPairGenerator kpg = KeyPairGenerator
				.getInstance(su.getKeyPairAlgo());
		KeyPair keyPair = kpg.generateKeyPair();
		su.setKeyPair(keyPair);

		su.setRootAddress(new Address("0123"));
		su.setProperty("key1", "value1");
		su.setProperty("key2", "value2");
		su.setProperty("key3", "");

		Structure s = su.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + JLG.NL + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(su.toStructure()));
	}

	public void testStructure() throws Exception {
		Structure s1 = new Structure("Test");
		s1.setIntField("int", 12345);
		s1.setStringField("string", "Hello World");
		s1.setBinField("byte[]", new byte[] { 12, 34, 56 });
		Structure substruct1 = new Structure("Sub");
		substruct1.setIntField("int", 12);
		s1.setStructureToSubstructField("substruct", substruct1);
		s1.setListField("array1", new String[] { "Hello", "World" });
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("hello", "world");
		map1.put("Yannis", "Thomias");
		s1.setMapField("map1", map1);

		Structure s2 = new Structure("Test");
		s2.setIntField("int", 12345);
		s2.setStringField("string", "Hello World");
		s2.setBinField("byte[]", new byte[] { 12, 34, 56 });
		Structure substruct2 = new Structure("Sub");
		substruct2.setIntField("int", 12);
		s2.setStructureToSubstructField("substruct", substruct2);
		s2.setListField("array1", new String[] { "Hello", "World" });
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("hello", "world");
		map2.put("Yannis", "Thomias");
		s2.setMapField("map1", map2);

		LOG.info("Same? " + s1.equals(s2));
	}

	public void testFields() throws Exception {
		LOG.info("String test");
		SField t = new SField("string", "Hello World");
		SField b = new SField("string", "Hello World");
		LOG.info("b=" + b.equals(t));
		LOG.info("----------------------------");
		LOG.info("int test");
		t = new SField("int", 123456);
		b = new SField("int", 123456);
		LOG.info("b=" + b.equals(t));
		LOG.info("----------------------------");
		LOG.info("byte[] test");
		t = new SField("bytes", new byte[] { 12, 34, 56 });
		b = new SField("bytes", new byte[] { 12, 34, 56 });
		LOG.info("b=" + b.equals(t));
	}

	public void testEqual() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		org.ocpteam.serializable.TestObject t = new org.ocpteam.serializable.TestObject();
		Structure s = t.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testInputMessage() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
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
		});
		Structure s = t.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testInputFlow() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
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
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testTree() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Tree t = new Tree();
		t.addFile("file1", new Pointer("0123"));
		t.addFile("file2", new Pointer("4567"));
		Structure s = t.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testTreeEntry() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		TreeEntry t = new TreeEntry("file", new Pointer("0123"), TreeEntry.FILE);
		Structure s = t.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testEmptySecureUser() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		SecretKey secretKey = null;
		SecureUser su = new SecureUser();
		su.setSecretKey(secretKey);
		Structure s = su.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		LOG.info("b=" + s.equals(s2));
	}

	public void testPointer() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Pointer p = new Pointer(new Id("0123"));
		Structure s = p.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(p.toStructure()));
	}

	public void testEOMObject() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		EOMObject eom = new EOMObject();
		Structure s = eom.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(eom.toStructure()));
	}

	public void testContent() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Content c = new Content("Yannis", new byte[] { 01, 23 }, new byte[] {
				98, 76 });
		Structure s = c.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(c.toStructure()));
	}

	public void testContact() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Node n = new Node(new Id("0123"), 3);
		Contact c = new Contact();
		c.setHost("localhost");
		c.setName("Yannis");
		c.setNode(n);
		c.setTcpPort(12345);
		c.setUdpPort(67890);
		LOG.info("contact=" + c.toString());
		Structure s = c.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(c.toStructure()));
	}

	public void testNode() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Node n = new Node(new Id("0123"), 3);
		Structure s = n.toStructure();
		LOG.info("s=" + s);
		byte[] array = marshaler.marshal(s);
		LOG.info("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		LOG.info("s2=" + s2);
		IStructurable b = s2.toStructurable();
		LOG.info("b=" + b.toStructure().equals(n.toStructure()));
	}
}
