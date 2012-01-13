package com.guenego.ocp;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.guenego.misc.JLG;

public class PersistentHashMap implements Map<Address, Content> {

	private File dir;

	public static class PersistentMapEntry implements
			Map.Entry<Address, Content> {

		private Address address;
		private Content content;

		public PersistentMapEntry(Address address, Content content) {
			this.address = address;
			this.content = content;
		}

		@Override
		public Address getKey() {
			return address;
		}

		@Override
		public Content getValue() {
			return content;
		}

		@Override
		public Content setValue(Content value) {
			content = value;
			return content;
		}

	}

	public PersistentHashMap(Agent agent) {
		try {
			String root = agent.p.getProperty("storage.dir",
					System.getenv("TEMP") + "/ocp_agent_storage/" + agent.name);
			dir = new File(root);
			JLG.mkdir(dir);
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public void clear() {
		try {
			JLG.rm(dir);
			JLG.mkdir(dir);
		} catch (Exception e) {
			JLG.error(e);
		}
	}

	@Override
	public boolean containsKey(Object key) {
		try {
			for (File child : dir.listFiles()) {
				Address address = new Address(child.getName());
				if (address.equals(key)) {
					return true;
				}
			}

		} catch (Exception e) {
			JLG.error(e);
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		try {
			for (File child : dir.listFiles()) {
				Content content = (Content) JLG.deserialize(new String(JLG
						.getBinaryFile(child)));
				if (content.equals(value)) {
					return true;
				}
			}

		} catch (Exception e) {
			JLG.error(e);
		}

		return false;
	}

	@Override
	public Set<java.util.Map.Entry<Address, Content>> entrySet() {
		Set<java.util.Map.Entry<Address, Content>> result = new HashSet<java.util.Map.Entry<Address, Content>>();
		try {
			for (File child : dir.listFiles()) {
				Address address = new Address(child.getName());
				Content content = (Content) JLG.deserialize(new String(JLG
						.getBinaryFile(child)));
				PersistentMapEntry entry = new PersistentMapEntry(address,
						content);
				result.add(entry);
			}

		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
	}

	@Override
	public Content get(Object key) {
		try {
			File file = new File(dir, key.toString());
			if (file.exists()) {
				Content content = (Content) JLG.deserialize(new String(JLG
						.getBinaryFile(file)));
				return content;
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Set<Address> keySet() {
		Set<Address> result = new HashSet<Address>();
		try {
			for (File child : dir.listFiles()) {
				Address address = new Address(child.getName());
				result.add(address);
			}
		} catch (Exception e) {
			JLG.error(e);
		}

		return result;
	}

	@Override
	public Content put(Address key, Content value) {
		try {
			File file = new File(dir, key.toString());
			JLG.setBinaryFile(file, JLG.serialize(value).getBytes());
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends Address, ? extends Content> m) {
		Iterator<? extends Address> it = m.keySet().iterator();
		while (it.hasNext()) {
			Address address = (Address) it.next();
			Content content = m.get(address);
			put(address, content);
		}
	}

	@Override
	public Content remove(Object key) {
		try {
			File file = new File(dir, key.toString());
			if (file.exists()) {
				Content content = (Content) JLG.deserialize(new String(JLG
						.getBinaryFile(file)));
				file.delete();
				return content;
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return null;
	}

	@Override
	public int size() {
		return dir.listFiles().length;
	}

	@Override
	public Collection<Content> values() {
		Collection<Content> result = new HashSet<Content>();
		try {
			for (File child : dir.listFiles()) {
				Content content = (Content) JLG.deserialize(new String(JLG
						.getBinaryFile(child)));
				result.add(content);
			}
		} catch (Exception e) {
			JLG.error(e);
		}
		return result;
	}

}
