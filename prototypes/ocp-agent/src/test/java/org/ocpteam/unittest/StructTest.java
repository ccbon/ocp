package org.ocpteam.unittest;

import java.io.Serializable;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.Test;
import org.ocpteam.component.FListMarshaler;
import org.ocpteam.component.Protocol;
import org.ocpteam.core.TopContainer;
import org.ocpteam.entity.Session;
import org.ocpteam.interfaces.IActivity;
import org.ocpteam.interfaces.IMarshaler;
import org.ocpteam.interfaces.IStructurable;
import org.ocpteam.interfaces.ITransaction;
import org.ocpteam.misc.Id;
import org.ocpteam.misc.JLG;
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
import org.ocpteam.serializable.SecureUser;
import org.ocpteam.serializable.Tree;
import org.ocpteam.serializable.TreeEntry;

public class StructTest extends TopContainer {
	@Test
	public void mytest() {
		JLG.debug_on();
		JLG.bUseSet = true;
		JLG.set.add(StructTest.class.getName());
		// JLG.set.add(FListMarshaler.class.getName());
		try {
			testEqual();
			testFields();
			testStructure();
			testNode();
			testContact();
			testContent();
			testEOMObject();
			testPointer();
			testEmptySecureUser();
			testTreeEntry();
			testTree();
			testInputFlow();
			testInputMessage();
			testSecureUser();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + JLG.NL + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(su.toStructure()));
	}

	public void testStructure() throws Exception {
		Structure s1 = new Structure("Test");
		s1.setIntField("int", 12345);
		s1.setStringField("string", "Hello World");
		s1.setByteArrayField("byte[]", new byte[] { 12, 34, 56 });
		Structure substruct1 = new Structure("Sub");
		substruct1.setIntField("int", 12);
		s1.setStructureSubstructField("substruct", substruct1);
		s1.setListField("array1", new String[] { "Hello", "World" });
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("hello", "world");
		map1.put("Yannis", "Thomias");
		s1.setMapField("map1", map1);

		Structure s2 = new Structure("Test");
		s2.setIntField("int", 12345);
		s2.setStringField("string", "Hello World");
		s2.setByteArrayField("byte[]", new byte[] { 12, 34, 56 });
		Structure substruct2 = new Structure("Sub");
		substruct2.setIntField("int", 12);
		s2.setStructureSubstructField("substruct", substruct2);
		s2.setListField("array1", new String[] { "Hello", "World" });
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("hello", "world");
		map2.put("Yannis", "Thomias");
		s2.setMapField("map1", map2);

		JLG.debug("Same? " + s1.equals(s2));
	}

	public void testFields() throws Exception {
		JLG.debug("String test");
		SField t = new SField("string", "Hello World");
		SField b = new SField("string", "Hello World");
		JLG.debug("b=" + b.equals(t));
		JLG.debug("----------------------------");
		JLG.debug("int test");
		t = new SField("int", 123456);
		b = new SField("int", 123456);
		JLG.debug("b=" + b.equals(t));
		JLG.debug("----------------------------");
		JLG.debug("byte[] test");
		t = new SField("bytes", new byte[] { 12, 34, 56 });
		b = new SField("bytes", new byte[] { 12, 34, 56 });
		JLG.debug("b=" + b.equals(t));
	}

	public void testEqual() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		org.ocpteam.serializable.TestObject t = new org.ocpteam.serializable.TestObject();
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(t.toStructure()));
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
		}, "hello world", new byte[] { 12, 34, 56 });
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(t.toStructure()));
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
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testTree() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
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
		JLG.debug("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testTreeEntry() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		TreeEntry t = new TreeEntry("file", new Pointer("0123"), TreeEntry.FILE);
		Structure s = t.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(t.toStructure()));
	}

	public void testEmptySecureUser() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		SecretKey secretKey = null;
		SecureUser su = new SecureUser();
		su.setSecretKey(secretKey);
		Structure s = su.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		JLG.debug("b=" + s.equals(s2));
	}

	public void testPointer() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Pointer p = new Pointer(new Id("0123"));
		Structure s = p.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(p.toStructure()));
	}

	public void testEOMObject() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		EOMObject eom = new EOMObject();
		Structure s = eom.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(eom.toStructure()));
	}

	public void testContent() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Content c = new Content("Yannis", new byte[] { 01, 23 }, new byte[] {
				98, 76 });
		Structure s = c.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(c.toStructure()));
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
		JLG.debug("contact=" + c.toString());
		Structure s = c.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(c.toStructure()));
	}

	public void testNode() throws Exception {
		IMarshaler marshaler = new FListMarshaler();
		Node n = new Node(new Id("0123"), 3);
		Structure s = n.toStructure();
		JLG.debug("s=" + s);
		byte[] array = marshaler.marshal(s);
		JLG.debug("array=" + new String(array));
		Structure s2 = marshaler.unmarshal(array);
		JLG.debug("s2=" + s2);
		IStructurable b = s2.toObject();
		JLG.debug("b=" + b.toStructure().equals(n.toStructure()));
	}
}
